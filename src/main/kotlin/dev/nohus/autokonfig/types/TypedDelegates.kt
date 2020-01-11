@file:Suppress("FunctionName")

package dev.nohus.autokonfig.types

import dev.nohus.autokonfig.AutoKonfig
import dev.nohus.autokonfig.DefaultAutoKonfig
import java.time.*
import kotlin.reflect.KClass

/**
 * Created by Marcin Wisniowski (Nohus) on 05/01/2020.
 */

/**
 * Helpers for getting typed setting providers
 */
fun AutoKonfig.StringSetting(default: String? = null, name: String? = null) = getSettingProvider(StringSettingType, default, name, null)
fun AutoKonfig.IntSetting(default: Int? = null, name: String? = null) = getSettingProvider(IntSettingType, default, name, null)
fun AutoKonfig.LongSetting(default: Long? = null, name: String? = null) = getSettingProvider(LongSettingType, default, name, null)
fun AutoKonfig.FloatSetting(default: Float? = null, name: String? = null) = getSettingProvider(FloatSettingType, default, name, null)
fun AutoKonfig.DoubleSetting(default: Double? = null, name: String? = null) = getSettingProvider(DoubleSettingType, default, name, null)
fun <T : Enum<T>> AutoKonfig.EnumSetting(enum: Class<T>, default: T? = null, name: String? = null) = getSettingProvider(EnumSettingType(enum), default, name, null)
fun <T : Enum<T>> AutoKonfig.EnumSetting(enum: KClass<T>, default: T? = null, name: String? = null) = EnumSetting(enum.java, default, name)
fun AutoKonfig.InstantSetting(default: Instant? = null, name: String? = null) = getSettingProvider(InstantSettingType, default, name, null)
fun AutoKonfig.DurationSetting(default: Duration? = null, name: String? = null) = getSettingProvider(DurationSettingType, default, name, null)
fun AutoKonfig.LocalTimeSetting(default: LocalTime? = null, name: String? = null) = getSettingProvider(LocalTimeSettingType, default, name, null)
fun AutoKonfig.LocalDateSetting(default: LocalDate? = null, name: String? = null) = getSettingProvider(LocalDateSettingType, default, name, null)
fun AutoKonfig.LocalDateTimeSetting(default: LocalDateTime? = null, name: String? = null) = getSettingProvider(LocalDateTimeSettingType, default, name, null)
fun <T> AutoKonfig.ListSetting(type: SettingType<T>, default: List<T>? = null, name: String? = null) = getSettingProvider(ListSettingType(type), default, name, null)
fun <T> AutoKonfig.ListSetting(type: SettingType<T>, separator: Regex, default: List<T>? = null, name: String? = null) = getSettingProvider(ListSettingType(type, separator), default, name, null)
fun <T> AutoKonfig.ListSetting(type: SettingType<T>, separator: String, default: List<T>? = null, name: String? = null) = getSettingProvider(ListSettingType(type, separator), default, name, null)
fun <T> AutoKonfig.SetSetting(type: SettingType<T>, default: Set<T>? = null, name: String? = null) = getSettingProvider(SetSettingType(type), default, name, null)
fun <T> AutoKonfig.SetSetting(type: SettingType<T>, separator: Regex, default: Set<T>? = null, name: String? = null) = getSettingProvider(SetSettingType(type, separator), default, name, null)
fun <T> AutoKonfig.SetSetting(type: SettingType<T>, separator: String, default: Set<T>? = null, name: String? = null) = getSettingProvider(SetSettingType(type, separator), default, name, null)
fun AutoKonfig.BooleanSetting(default: Boolean? = null, name: String? = null) = getSettingProvider(BooleanSettingType, default, name, null)
fun AutoKonfig.FlagSetting(name: String? = null) = BooleanSetting(false, name)

/**
 * Helpers for getting typed setting providers from the default AutoKonfig
 */
fun StringSetting(default: String? = null, name: String? = null) = DefaultAutoKonfig.StringSetting(default, name)
fun IntSetting(default: Int? = null, name: String? = null) = DefaultAutoKonfig.IntSetting(default, name)
fun LongSetting(default: Long? = null, name: String? = null) = DefaultAutoKonfig.LongSetting(default, name)
fun FloatSetting(default: Float? = null, name: String? = null) = DefaultAutoKonfig.FloatSetting(default, name)
fun DoubleSetting(default: Double? = null, name: String? = null) = DefaultAutoKonfig.DoubleSetting(default, name)
fun <T : Enum<T>> EnumSetting(enum: Class<T>, default: T? = null, name: String? = null) = DefaultAutoKonfig.EnumSetting(enum, default, name)
fun <T : Enum<T>> EnumSetting(enum: KClass<T>, default: T? = null, name: String? = null) = DefaultAutoKonfig.EnumSetting(enum, default, name)
fun InstantSetting(default: Instant? = null, name: String? = null) = DefaultAutoKonfig.InstantSetting(default, name)
fun DurationSetting(default: Duration? = null, name: String? = null) = DefaultAutoKonfig.DurationSetting(default, name)
fun LocalTimeSetting(default: LocalTime? = null, name: String? = null) = DefaultAutoKonfig.LocalTimeSetting(default, name)
fun LocalDateSetting(default: LocalDate? = null, name: String? = null) = DefaultAutoKonfig.LocalDateSetting(default, name)
fun LocalDateTimeSetting(default: LocalDateTime? = null, name: String? = null) = DefaultAutoKonfig.LocalDateTimeSetting(default, name)
fun <T> ListSetting(type: SettingType<T>, default: List<T>? = null, name: String? = null) = DefaultAutoKonfig.ListSetting(type, default, name)
fun <T> ListSetting(type: SettingType<T>, separator: Regex, default: List<T>? = null, name: String? = null) = DefaultAutoKonfig.ListSetting(type, separator, default, name)
fun <T> ListSetting(type: SettingType<T>, separator: String, default: List<T>? = null, name: String? = null) = DefaultAutoKonfig.ListSetting(type, separator, default, name)
fun <T> SetSetting(type: SettingType<T>, default: Set<T>? = null, name: String? = null) = DefaultAutoKonfig.SetSetting(type, default, name)
fun <T> SetSetting(type: SettingType<T>, separator: Regex, default: Set<T>? = null, name: String? = null) = DefaultAutoKonfig.SetSetting(type, separator, default, name)
fun <T> SetSetting(type: SettingType<T>, separator: String, default: Set<T>? = null, name: String? = null) = DefaultAutoKonfig.SetSetting(type, separator, default, name)
fun BooleanSetting(default: Boolean? = null, name: String? = null) = DefaultAutoKonfig.BooleanSetting(default, name)
fun FlagSetting(name: String? = null) = DefaultAutoKonfig.FlagSetting(name)

/**
 * Setting groups
 */
open class Group(name: String? = null) {

    private val effectiveName = name ?: javaClass.simplerName

    private val Class<*>.simplerName get() = simpleName.substringBeforeLast("$").substringAfterLast("$")

    val fullName: String by lazy {
        val names = mutableListOf<String>()
        var parentClass = javaClass.enclosingClass
        while (parentClass.superclass.simpleName == "Group") {
            (parentClass.kotlin.objectInstance as? Group)?.effectiveName?.let { names += it }
            parentClass = parentClass.enclosingClass
        }
        names += effectiveName 
        names.joinToString(".")
    }

    /**
     * Helpers for getting typed setting providers
     */
    fun AutoKonfig.StringSetting(default: String? = null, name: String? = null) = getSettingProvider(StringSettingType, default, name, this@Group)
    fun AutoKonfig.IntSetting(default: Int? = null, name: String? = null) = getSettingProvider(IntSettingType, default, name, this@Group)
    fun AutoKonfig.LongSetting(default: Long? = null, name: String? = null) = getSettingProvider(LongSettingType, default, name, this@Group)
    fun AutoKonfig.FloatSetting(default: Float? = null, name: String? = null) = getSettingProvider(FloatSettingType, default, name, this@Group)
    fun AutoKonfig.DoubleSetting(default: Double? = null, name: String? = null) = getSettingProvider(DoubleSettingType, default, name, this@Group)
    fun <T : Enum<T>> AutoKonfig.EnumSetting(enum: Class<T>, default: T? = null, name: String? = null) = getSettingProvider(EnumSettingType(enum), default, name, this@Group)
    fun <T : Enum<T>> AutoKonfig.EnumSetting(enum: KClass<T>, default: T? = null, name: String? = null) = EnumSetting(enum.java, default, name)
    fun AutoKonfig.InstantSetting(default: Instant? = null, name: String? = null) = getSettingProvider(InstantSettingType, default, name, this@Group)
    fun AutoKonfig.DurationSetting(default: Duration? = null, name: String? = null) = getSettingProvider(DurationSettingType, default, name, this@Group)
    fun AutoKonfig.LocalTimeSetting(default: LocalTime? = null, name: String? = null) = getSettingProvider(LocalTimeSettingType, default, name, this@Group)
    fun AutoKonfig.LocalDateSetting(default: LocalDate? = null, name: String? = null) = getSettingProvider(LocalDateSettingType, default, name, this@Group)
    fun AutoKonfig.LocalDateTimeSetting(default: LocalDateTime? = null, name: String? = null) = getSettingProvider(LocalDateTimeSettingType, default, name, this@Group)
    fun <T> AutoKonfig.ListSetting(type: SettingType<T>, default: List<T>? = null, name: String? = null) = getSettingProvider(ListSettingType(type), default, name, this@Group)
    fun <T> AutoKonfig.ListSetting(type: SettingType<T>, separator: Regex, default: List<T>? = null, name: String? = null) = getSettingProvider(ListSettingType(type, separator), default, name, this@Group)
    fun <T> AutoKonfig.ListSetting(type: SettingType<T>, separator: String, default: List<T>? = null, name: String? = null) = getSettingProvider(ListSettingType(type, separator), default, name, this@Group)
    fun <T> AutoKonfig.SetSetting(type: SettingType<T>, default: Set<T>? = null, name: String? = null) = getSettingProvider(SetSettingType(type), default, name, this@Group)
    fun <T> AutoKonfig.SetSetting(type: SettingType<T>, separator: Regex, default: Set<T>? = null, name: String? = null) = getSettingProvider(SetSettingType(type, separator), default, name, this@Group)
    fun <T> AutoKonfig.SetSetting(type: SettingType<T>, separator: String, default: Set<T>? = null, name: String? = null) = getSettingProvider(SetSettingType(type, separator), default, name, this@Group)
    fun AutoKonfig.BooleanSetting(default: Boolean? = null, name: String? = null) = getSettingProvider(BooleanSettingType, default, name, this@Group)
    fun AutoKonfig.FlagSetting(name: String? = null) = BooleanSetting(false, name)

    /**
     * Helpers for getting typed setting providers from the default AutoKonfig
     */
    fun StringSetting(default: String? = null, name: String? = null) = DefaultAutoKonfig.StringSetting(default, name)
    fun IntSetting(default: Int? = null, name: String? = null) = DefaultAutoKonfig.IntSetting(default, name)
    fun LongSetting(default: Long? = null, name: String? = null) = DefaultAutoKonfig.LongSetting(default, name)
    fun FloatSetting(default: Float? = null, name: String? = null) = DefaultAutoKonfig.FloatSetting(default, name)
    fun DoubleSetting(default: Double? = null, name: String? = null) = DefaultAutoKonfig.DoubleSetting(default, name)
    fun <T : Enum<T>> EnumSetting(enum: Class<T>, default: T? = null, name: String? = null) = DefaultAutoKonfig.EnumSetting(enum, default, name)
    fun <T : Enum<T>> EnumSetting(enum: KClass<T>, default: T? = null, name: String? = null) = DefaultAutoKonfig.EnumSetting(enum, default, name)
    fun InstantSetting(default: Instant? = null, name: String? = null) = DefaultAutoKonfig.InstantSetting(default, name)
    fun DurationSetting(default: Duration? = null, name: String? = null) = DefaultAutoKonfig.DurationSetting(default, name)
    fun LocalTimeSetting(default: LocalTime? = null, name: String? = null) = DefaultAutoKonfig.LocalTimeSetting(default, name)
    fun LocalDateSetting(default: LocalDate? = null, name: String? = null) = DefaultAutoKonfig.LocalDateSetting(default, name)
    fun LocalDateTimeSetting(default: LocalDateTime? = null, name: String? = null) = DefaultAutoKonfig.LocalDateTimeSetting(default, name)
    fun <T> ListSetting(type: SettingType<T>, default: List<T>? = null, name: String? = null) = DefaultAutoKonfig.ListSetting(type, default, name)
    fun <T> ListSetting(type: SettingType<T>, separator: Regex, default: List<T>? = null, name: String? = null) = DefaultAutoKonfig.ListSetting(type, separator, default, name)
    fun <T> ListSetting(type: SettingType<T>, separator: String, default: List<T>? = null, name: String? = null) = DefaultAutoKonfig.ListSetting(type, separator, default, name)
    fun <T> SetSetting(type: SettingType<T>, default: Set<T>? = null, name: String? = null) = DefaultAutoKonfig.SetSetting(type, default, name)
    fun <T> SetSetting(type: SettingType<T>, separator: Regex, default: Set<T>? = null, name: String? = null) = DefaultAutoKonfig.SetSetting(type, separator, default, name)
    fun <T> SetSetting(type: SettingType<T>, separator: String, default: Set<T>? = null, name: String? = null) = DefaultAutoKonfig.SetSetting(type, separator, default, name)
    fun BooleanSetting(default: Boolean? = null, name: String? = null) = DefaultAutoKonfig.BooleanSetting(default, name)
    fun FlagSetting(name: String? = null) = DefaultAutoKonfig.FlagSetting(name)
}
