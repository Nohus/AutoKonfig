package dev.nohus.autokonfig

import java.util.*
import kotlin.properties.ReadOnlyProperty
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
                throw AutoKonfigException("Failed to parse setting \"$key\", the value is \"$value\", but ${e.reason}")
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
        fun getBoolean(key: String, default: Boolean? = null): Boolean = DefaultAutoKonfig.getBoolean(key, default)
        fun getFlag(key: String): Boolean = DefaultAutoKonfig.getFlag(key)

        private fun mapString(value: String) = value
        private fun mapInt(value: String) = value.toIntOrNull() ?: throw SettingParseException("must be an Int number")
        private fun mapLong(value: String) = value.toLongOrNull() ?: throw SettingParseException("must be a Long number")
        private fun mapFloat(value: String) = value.toFloatOrNull() ?: throw SettingParseException("must be a Float number")
        private fun mapDouble(value: String) = value.toDoubleOrNull() ?: throw SettingParseException("must be a Double number")
        private fun <T> mapEnum(value: String, map: Map<String, T>): T {
            return try {
                map[value] ?: map.entries.first { it.key.toLowerCase(Locale.US) == value.toLowerCase(Locale.US) }.value
            } catch (e: NoSuchElementException) {
                throw SettingParseException("possible values are ${map.keys}")
            }
        }
        private fun mapBoolean(value: String) = value in listOf("true", "yes", "1")
    }
}
