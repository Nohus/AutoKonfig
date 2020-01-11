@file:Suppress("FunctionName")

package dev.nohus.autokonfig

import dev.nohus.autokonfig.AutoKonfig.SettingProvider
import java.time.*
import kotlin.reflect.KClass

/**
 * Created by Marcin Wisniowski (Nohus) on 05/01/2020.
 */

/**
 * Settings using a specific AutoKonfig
 */
fun AutoKonfig.StringSetting(default: String? = null, name: String? = null): SettingProvider<String> = this.StringSettingProvider(default, name, null)
fun AutoKonfig.IntSetting(default: Int? = null, name: String? = null): SettingProvider<Int> = this.IntSettingProvider(default, name, null)
fun AutoKonfig.LongSetting(default: Long? = null, name: String? = null): SettingProvider<Long> = this.LongSettingProvider(default, name, null)
fun AutoKonfig.FloatSetting(default: Float? = null, name: String? = null): SettingProvider<Float> = this.FloatSettingProvider(default, name, null)
fun AutoKonfig.DoubleSetting(default: Double? = null, name: String? = null): SettingProvider<Double> = this.DoubleSettingProvider(default, name, null)
fun AutoKonfig.BooleanSetting(default: Boolean? = null, name: String? = null): SettingProvider<Boolean> = this.BooleanSettingProvider(default, name, null)
fun <T : Enum<T>> AutoKonfig.EnumSetting(enum: Class<T>, default: T? = null, name: String? = null): SettingProvider<T> = this.EnumSettingProvider(enum, default, name, null)
fun <T : Enum<T>> AutoKonfig.EnumSetting(enum: KClass<T>, default: T? = null, name: String? = null): SettingProvider<T> = EnumSetting(enum.java, default, name)
fun AutoKonfig.InstantSetting(default: Instant? = null, name: String? = null): SettingProvider<Instant> = this.InstantSettingProvider(default, name, null)
fun AutoKonfig.DurationSetting(default: Duration? = null, name: String? = null): SettingProvider<Duration> = this.DurationSettingProvider(default, name, null)
fun AutoKonfig.LocalTimeSetting(default: LocalTime? = null, name: String? = null): SettingProvider<LocalTime> = this.LocalTimeSettingProvider(default, name, null)
fun AutoKonfig.LocalDateSetting(default: LocalDate? = null, name: String? = null): SettingProvider<LocalDate> = this.LocalDateSettingProvider(default, name, null)
fun AutoKonfig.LocalDateTimeSetting(default: LocalDateTime? = null, name: String? = null): SettingProvider<LocalDateTime> = this.LocalDateTimeSettingProvider(default, name, null)
fun AutoKonfig.FlagSetting(name: String? = null) = this.BooleanSetting(false, name)

/**
 * Settings using the default AutoKonfig
 */
fun StringSetting(default: String? = null, name: String? = null): SettingProvider<String> = DefaultAutoKonfig.StringSetting(default, name)
fun IntSetting(default: Int? = null, name: String? = null): SettingProvider<Int> = DefaultAutoKonfig.IntSetting(default, name)
fun LongSetting(default: Long? = null, name: String? = null): SettingProvider<Long> = DefaultAutoKonfig.LongSetting(default, name)
fun FloatSetting(default: Float? = null, name: String? = null): SettingProvider<Float> = DefaultAutoKonfig.FloatSetting(default, name)
fun DoubleSetting(default: Double? = null, name: String? = null): SettingProvider<Double> = DefaultAutoKonfig.DoubleSetting(default, name)
fun BooleanSetting(default: Boolean? = null, name: String? = null): SettingProvider<Boolean> = DefaultAutoKonfig.BooleanSetting(default, name)
fun <T : Enum<T>> EnumSetting(enum: Class<T>, default: T? = null, name: String? = null): SettingProvider<T> = DefaultAutoKonfig.EnumSetting(enum, default, name)
fun <T : Enum<T>> EnumSetting(enum: KClass<T>, default: T? = null, name: String? = null): SettingProvider<T> = DefaultAutoKonfig.EnumSetting(enum, default, name)
fun InstantSetting(default: Instant? = null, name: String? = null): SettingProvider<Instant> = DefaultAutoKonfig.InstantSetting(default, name)
fun DurationSetting(default: Duration? = null, name: String? = null): SettingProvider<Duration> = DefaultAutoKonfig.DurationSetting(default, name)
fun LocalTimeSetting(default: LocalTime? = null, name: String? = null): SettingProvider<LocalTime> = DefaultAutoKonfig.LocalTimeSetting(default, name)
fun LocalDateSetting(default: LocalDate? = null, name: String? = null): SettingProvider<LocalDate> = DefaultAutoKonfig.LocalDateSetting(default, name)
fun LocalDateTimeSetting(default: LocalDateTime? = null, name: String? = null): SettingProvider<LocalDateTime> = DefaultAutoKonfig.LocalDateTimeSetting(default, name)
fun FlagSetting(name: String? = null): SettingProvider<Boolean> = DefaultAutoKonfig.FlagSetting(name)

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
     * Settings using a specific AutoKonfig
     */
    fun AutoKonfig.StringSetting(default: String? = null, name: String? = null): SettingProvider<String> = this.StringSettingProvider(default, name, this@Group)
    fun AutoKonfig.IntSetting(default: Int? = null, name: String? = null): SettingProvider<Int> = this.IntSettingProvider(default, name, this@Group)
    fun AutoKonfig.LongSetting(default: Long? = null, name: String? = null): SettingProvider<Long> = this.LongSettingProvider(default, name, this@Group)
    fun AutoKonfig.FloatSetting(default: Float? = null, name: String? = null): SettingProvider<Float> = this.FloatSettingProvider(default, name, this@Group)
    fun AutoKonfig.DoubleSetting(default: Double? = null, name: String? = null): SettingProvider<Double> = this.DoubleSettingProvider(default, name, this@Group)
    fun AutoKonfig.BooleanSetting(default: Boolean? = null, name: String? = null): SettingProvider<Boolean> = this.BooleanSettingProvider(default, name, this@Group)
    fun <T : Enum<T>> AutoKonfig.EnumSetting(enum: Class<T>, default: T? = null, name: String? = null): SettingProvider<T> = this.EnumSettingProvider(enum, default, name, this@Group)
    fun <T : Enum<T>> AutoKonfig.EnumSetting(enum: KClass<T>, default: T? = null, name: String? = null): SettingProvider<T> = EnumSetting(enum.java, default, name)
    fun AutoKonfig.InstantSetting(default: Instant? = null, name: String? = null): SettingProvider<Instant> = this.InstantSettingProvider(default, name, this@Group)
    fun AutoKonfig.DurationSetting(default: Duration? = null, name: String? = null): SettingProvider<Duration> = this.DurationSettingProvider(default, name, this@Group)
    fun AutoKonfig.LocalTimeSetting(default: LocalTime? = null, name: String? = null): SettingProvider<LocalTime> = this.LocalTimeSettingProvider(default, name, this@Group)
    fun AutoKonfig.LocalDateSetting(default: LocalDate? = null, name: String? = null): SettingProvider<LocalDate> = this.LocalDateSettingProvider(default, name, this@Group)
    fun AutoKonfig.LocalDateTimeSetting(default: LocalDateTime? = null, name: String? = null): SettingProvider<LocalDateTime> = this.LocalDateTimeSettingProvider(default, name, this@Group)
    fun AutoKonfig.FlagSetting(name: String? = null) = this.BooleanSetting(false, name)

    /**
     * Settings using the default AutoKonfig
     */
    fun StringSetting(default: String? = null, name: String? = null): SettingProvider<String> = DefaultAutoKonfig.StringSetting(default, name)
    fun IntSetting(default: Int? = null, name: String? = null): SettingProvider<Int> = DefaultAutoKonfig.IntSetting(default, name)
    fun LongSetting(default: Long? = null, name: String? = null): SettingProvider<Long> = DefaultAutoKonfig.LongSetting(default, name)
    fun FloatSetting(default: Float? = null, name: String? = null): SettingProvider<Float> = DefaultAutoKonfig.FloatSetting(default, name)
    fun DoubleSetting(default: Double? = null, name: String? = null): SettingProvider<Double> = DefaultAutoKonfig.DoubleSetting(default, name)
    fun BooleanSetting(default: Boolean? = null, name: String? = null): SettingProvider<Boolean> = DefaultAutoKonfig.BooleanSetting(default, name)
    fun <T : Enum<T>> EnumSetting(enum: Class<T>, default: T? = null, name: String? = null): SettingProvider<T> = DefaultAutoKonfig.EnumSetting(enum, default, name)
    fun <T : Enum<T>> EnumSetting(enum: KClass<T>, default: T? = null, name: String? = null): SettingProvider<T> = DefaultAutoKonfig.EnumSetting(enum, default, name)
    fun InstantSetting(default: Instant? = null, name: String? = null): SettingProvider<Instant> = DefaultAutoKonfig.InstantSetting(default, name)
    fun DurationSetting(default: Duration? = null, name: String? = null): SettingProvider<Duration> = DefaultAutoKonfig.DurationSetting(default, name)
    fun LocalTimeSetting(default: LocalTime? = null, name: String? = null): SettingProvider<LocalTime> = DefaultAutoKonfig.LocalTimeSetting(default, name)
    fun LocalDateSetting(default: LocalDate? = null, name: String? = null): SettingProvider<LocalDate> = DefaultAutoKonfig.LocalDateSetting(default, name)
    fun LocalDateTimeSetting(default: LocalDateTime? = null, name: String? = null): SettingProvider<LocalDateTime> = DefaultAutoKonfig.LocalDateTimeSetting(default, name)
    fun FlagSetting(name: String? = null): SettingProvider<Boolean> = DefaultAutoKonfig.FlagSetting(name)
}
