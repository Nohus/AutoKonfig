package dev.nohus.autokonfig

import java.time.*
import java.time.format.DateTimeParseException
import java.util.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * Created by Marcin Wisniowski (Nohus) on 05/01/2020.
 */

class AutoKonfig {

    private val settings = SettingsStore()

    internal fun addProperty(key: String, value: String, source: SettingSource) {
        settings.addProperty(key, value, source)
    }

    internal fun addFlag(flag: String, source: SettingSource) {
        settings.addFlag(flag, source)
    }

    fun getKeySource(key: String): String {
        return settings.getSource(key)?.toString() ?: "Key \"$key\" not found"
    }

    fun clear() = apply {
        settings.clear()
    }

    fun getAll() = settings.getAll()

    private fun <T> getValue(key: String, transform: (String) -> T, default: T?): T {
        val value = settings.findValue(key)
        return if (value != null) {
            try {
                transform(value)
            } catch (e: SettingParseException) {
                throw AutoKonfigException("Failed to parse setting \"$key\", the value is \"$value\", but ${e.reason}", e)
            }
        }
        else default ?: throw AutoKonfigException("Required key \"$key\" is missing")
    }

    open inner class SettingProvider<T>(
        private val delegateProvider: (String) -> SettingDelegate<T>,
        private val optional: Boolean,
        private val name: String?,
        private val group: Group?
    ) {

        private fun checkExists(key: String) {
            delegateProvider(key).getValue()
        }

        operator fun provideDelegate(thisRef: Any?, prop: KProperty<*>): ReadOnlyProperty<Any?, T> {
            val effectiveName = name ?: prop.name
            val fullName = if (group != null) "${group.fullName}.$effectiveName" else effectiveName
            if (!optional) checkExists(fullName)
            return delegateProvider(fullName)
        }
    }

    inner class SettingDelegate<T>(
        private val key: String,
        private val transform: (String) -> T,
        private val default: T?
    ) : ReadOnlyProperty<Any?, T> {

        fun getValue() = getValue(key, transform, default)
        override operator fun getValue(thisRef: Any?, property: KProperty<*>): T = getValue()
    }

    internal inner class StringSettingProvider(default: String?, name: String?, group: Group?)
        : SettingProvider<String>({ SettingDelegate(it, ::mapString, default) }, default != null, name, group)
    internal inner class IntSettingProvider(default: Int?, name: String?, group: Group?)
        : SettingProvider<Int>({ SettingDelegate(it, ::mapInt, default) }, default != null, name, group)
    internal inner class LongSettingProvider(default: Long?, name: String?, group: Group?)
        : SettingProvider<Long>({ SettingDelegate(it, ::mapLong, default) }, default != null, name, group)
    internal inner class FloatSettingProvider(default: Float?, name: String?, group: Group?)
        : SettingProvider<Float>({ SettingDelegate(it, ::mapFloat, default) }, default != null, name, group)
    internal inner class DoubleSettingProvider(default: Double?, name: String?, group: Group?)
        : SettingProvider<Double>({ SettingDelegate(it, ::mapDouble, default) }, default != null, name, group)
    internal inner class EnumSettingProvider<T : Enum<T>>(enum: Class<T>, default: T?, name: String?, group: Group?)
        : SettingProvider<T>({ SettingDelegate(it, { mapEnum<T>(it, getEnumMapping(enum)) }, default) }, default != null, name, group)
    internal inner class InstantSettingProvider(default: Instant?, name: String?, group: Group?)
        : SettingProvider<Instant>({ SettingDelegate(it, ::mapInstant, default) }, default != null, name, group)
    internal inner class DurationSettingProvider(default: Duration?, name: String?, group: Group?)
        : SettingProvider<Duration>({ SettingDelegate(it, ::mapDuration, default) }, default != null, name, group)
    internal inner class LocalTimeSettingProvider(default: LocalTime?, name: String?, group: Group?)
        : SettingProvider<LocalTime>({ SettingDelegate(it, ::mapLocalTime, default) }, default != null, name, group)
    internal inner class LocalDateSettingProvider(default: LocalDate?, name: String?, group: Group?)
        : SettingProvider<LocalDate>({ SettingDelegate(it, ::mapLocalDate, default) }, default != null, name, group)
    internal inner class LocalDateTimeSettingProvider(default: LocalDateTime?, name: String?, group: Group?)
        : SettingProvider<LocalDateTime>({ SettingDelegate(it, ::mapLocalDateTime, default) }, default != null, name, group)
    internal inner class BooleanSettingProvider(default: Boolean?, name: String?, group: Group?)
        : SettingProvider<Boolean>({ SettingDelegate(it, ::mapBoolean, default) }, default != null, name, group)

    private fun <T: Enum<T>> getEnumMapping(enum: Class<T>): Map<String, T> {
        return EnumSet.allOf(enum).map { it.name to it }.toMap()
    }

    fun getString(key: String, default: String? = null): String = getValue(key, ::mapString, default)
    fun getInt(key: String, default: Int? = null): Int = getValue(key, ::mapInt, default)
    fun getLong(key: String, default: Long? = null): Long = getValue(key, ::mapLong, default)
    fun getFloat(key: String, default: Float? = null): Float = getValue(key, ::mapFloat, default)
    fun getDouble(key: String, default: Double? = null): Double = getValue(key, ::mapDouble, default)
    fun <T : Enum<T>> getEnum(enum: Class<T>, key: String, default: T?): T = getValue(key, { mapEnum(it, getEnumMapping(enum)) }, default)
    fun <T : Enum<T>> getEnum(enum: KClass<T>, key: String, default: T?): T = getEnum(enum.java, key, default)
    fun getInstant(key: String, default: Instant? = null): Instant = getValue(key, ::mapInstant, default)
    fun getDuration(key: String, default: Duration? = null): Duration = getValue(key, ::mapDuration, default)
    fun getLocalTime(key: String, default: LocalTime? = null): LocalTime = getValue(key, ::mapLocalTime, default)
    fun getLocalDate(key: String, default: LocalDate? = null): LocalDate = getValue(key, ::mapLocalDate, default)
    fun getLocalDateTime(key: String, default: LocalDateTime? = null): LocalDateTime = getValue(key, ::mapLocalDateTime, default)
    fun getBoolean(key: String, default: Boolean? = null): Boolean = getValue(key, ::mapBoolean, default)
    fun getFlag(key: String): Boolean = getBoolean(key, false)

    companion object {
        fun clear() = DefaultAutoKonfig.clear()
        fun getAll() = DefaultAutoKonfig.getAll()
        fun getKeySource(key: String) = DefaultAutoKonfig.getKeySource(key)

        fun getString(key: String, default: String? = null): String = DefaultAutoKonfig.getString(key, default)
        fun getInt(key: String, default: Int? = null): Int = DefaultAutoKonfig.getInt(key, default)
        fun getLong(key: String, default: Long? = null): Long = DefaultAutoKonfig.getLong(key, default)
        fun getFloat(key: String, default: Float? = null): Float = DefaultAutoKonfig.getFloat(key, default)
        fun getDouble(key: String, default: Double? = null): Double = DefaultAutoKonfig.getDouble(key, default)
        fun <T : Enum<T>> getEnum(enum: Class<T>, key: String, default: T? = null): T = DefaultAutoKonfig.getEnum(enum, key, default)
        fun <T : Enum<T>> getEnum(enum: KClass<T>, key: String, default: T? = null): T = DefaultAutoKonfig.getEnum(enum, key, default)
        fun getInstant(key: String, default: Instant? = null): Instant = DefaultAutoKonfig.getInstant(key, default)
        fun getDuration(key: String, default: Duration? = null): Duration = DefaultAutoKonfig.getDuration(key, default)
        fun getLocalTime(key: String, default: LocalTime? = null): LocalTime = DefaultAutoKonfig.getLocalTime(key, default)
        fun getLocalDate(key: String, default: LocalDate? = null): LocalDate = DefaultAutoKonfig.getLocalDate(key, default)
        fun getLocalDateTime(key: String, default: LocalDateTime? = null): LocalDateTime = DefaultAutoKonfig.getLocalDateTime(key, default)
        fun getBoolean(key: String, default: Boolean? = null): Boolean = DefaultAutoKonfig.getBoolean(key, default)
        fun getFlag(key: String): Boolean = DefaultAutoKonfig.getFlag(key)

        private fun mapString(value: String) = value
        private fun mapInt(value: String) = try { value.toInt() } catch (e: NumberFormatException) { throw SettingParseException("must be an Int number", e) }
        private fun mapLong(value: String) = try { value.toLong() } catch (e: NumberFormatException) { throw SettingParseException("must be a Long number", e) }
        private fun mapFloat(value: String) = try { value.toFloat() } catch (e: NumberFormatException) { throw SettingParseException("must be a Float number", e) }
        private fun mapDouble(value: String) = try { value.toDouble() } catch (e: NumberFormatException) { throw SettingParseException("must be a Double number", e) }
        private fun <T> mapEnum(value: String, map: Map<String, T>): T {
            return try {
                map[value] ?: map.entries.first { it.key.toLowerCase(Locale.US) == value.toLowerCase(Locale.US) }.value
            } catch (e: NoSuchElementException) {
                throw SettingParseException("possible values are ${map.keys}", e)
            }
        }
        private fun mapInstant(value: String) = try { Instant.parse(value) } catch (e: DateTimeParseException) { throw SettingParseException("must be an Instant", e) }
        private fun mapDuration(value: String) = try { Duration.parse(value) } catch (e: DateTimeParseException) { throw SettingParseException("must be a Duration", e) }
        private fun mapLocalTime(value: String) = try { LocalTime.parse(value) } catch (e: DateTimeParseException) { throw SettingParseException("must be a LocalTime", e) }
        private fun mapLocalDate(value: String) = try { LocalDate.parse(value) } catch (e: DateTimeParseException) { throw SettingParseException("must be a LocalDate", e) }
        private fun mapLocalDateTime(value: String) = try { LocalDateTime.parse(value) } catch (e: DateTimeParseException) { throw SettingParseException("must be a LocalDateTime", e) }
        private fun mapBoolean(value: String) = value in listOf("true", "yes", "1")
    }
}
