@file:Suppress("FunctionName")

package dev.nohus.autokonfig.types

import dev.nohus.autokonfig.AutoKonfig
import dev.nohus.autokonfig.DefaultAutoKonfig
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period
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
fun AutoKonfig.BigIntegerSetting(default: BigInteger? = null, name: String? = null) = getSettingProvider(BigIntegerSettingType, default, name, null)
fun AutoKonfig.BigDecimalSetting(default: BigDecimal? = null, name: String? = null) = getSettingProvider(BigDecimalSettingType, default, name, null)
fun <T : Enum<T>> AutoKonfig.EnumSetting(enum: Class<T>, default: T? = null, name: String? = null) = getSettingProvider(EnumSettingType(enum), default, name, null)
fun <T : Enum<T>> AutoKonfig.EnumSetting(enum: KClass<T>, default: T? = null, name: String? = null) = EnumSetting(enum.java, default, name)
fun AutoKonfig.InstantSetting(default: Instant? = null, name: String? = null) = getSettingProvider(InstantSettingType, default, name, null)
fun AutoKonfig.DurationSetting(default: Duration? = null, name: String? = null) = getSettingProvider(DurationSettingType, default, name, null)
fun AutoKonfig.PeriodSetting(default: Period? = null, name: String? = null) = getSettingProvider(PeriodSettingType, default, name, null)
fun AutoKonfig.LocalTimeSetting(default: LocalTime? = null, name: String? = null) = getSettingProvider(LocalTimeSettingType, default, name, null)
fun AutoKonfig.LocalDateSetting(default: LocalDate? = null, name: String? = null) = getSettingProvider(LocalDateSettingType, default, name, null)
fun AutoKonfig.LocalDateTimeSetting(default: LocalDateTime? = null, name: String? = null) = getSettingProvider(LocalDateTimeSettingType, default, name, null)
fun <T> AutoKonfig.ListSetting(type: SettingType<T>, default: List<T>? = null, name: String? = null) = getSettingProvider(ListSettingType(type), default, name, null)
fun <T> AutoKonfig.SetSetting(type: SettingType<T>, default: Set<T>? = null, name: String? = null) = getSettingProvider(SetSettingType(type), default, name, null)
fun AutoKonfig.BytesSetting(default: Long? = null, name: String? = null) = getSettingProvider(BytesSettingType, default, name, null)
fun AutoKonfig.BooleanSetting(default: Boolean? = null, name: String? = null) = getSettingProvider(BooleanSettingType, default, name, null)
fun AutoKonfig.FlagSetting(name: String? = null) = BooleanSetting(false, name)

/**
 * Helpers for getting optional typed setting providers
 */
fun AutoKonfig.OptionalStringSetting(name: String? = null) = getNullableSettingProvider(StringSettingType, name, null)
fun AutoKonfig.OptionalIntSetting(name: String? = null) = getNullableSettingProvider(IntSettingType, name, null)
fun AutoKonfig.OptionalLongSetting(name: String? = null) = getNullableSettingProvider(LongSettingType, name, null)
fun AutoKonfig.OptionalFloatSetting(name: String? = null) = getNullableSettingProvider(FloatSettingType, name, null)
fun AutoKonfig.OptionalDoubleSetting(name: String? = null) = getNullableSettingProvider(DoubleSettingType, name, null)
fun AutoKonfig.OptionalBigIntegerSetting(name: String? = null) = getNullableSettingProvider(BigIntegerSettingType, name, null)
fun AutoKonfig.OptionalBigDecimalSetting(name: String? = null) = getNullableSettingProvider(BigDecimalSettingType, name, null)
fun <T : Enum<T>> AutoKonfig.OptionalEnumSetting(enum: Class<T>, name: String? = null) = getNullableSettingProvider(EnumSettingType(enum), name, null)
fun <T : Enum<T>> AutoKonfig.OptionalEnumSetting(enum: KClass<T>, name: String? = null) = OptionalEnumSetting(enum.java, name)
fun AutoKonfig.OptionalInstantSetting(name: String? = null) = getNullableSettingProvider(InstantSettingType, name, null)
fun AutoKonfig.OptionalDurationSetting(name: String? = null) = getNullableSettingProvider(DurationSettingType, name, null)
fun AutoKonfig.OptionalPeriodSetting(name: String? = null) = getNullableSettingProvider(PeriodSettingType, name, null)
fun AutoKonfig.OptionalLocalTimeSetting(name: String? = null) = getNullableSettingProvider(LocalTimeSettingType, name, null)
fun AutoKonfig.OptionalLocalDateSetting(name: String? = null) = getNullableSettingProvider(LocalDateSettingType, name, null)
fun AutoKonfig.OptionalLocalDateTimeSetting(name: String? = null) = getNullableSettingProvider(LocalDateTimeSettingType, name, null)
fun <T> AutoKonfig.OptionalListSetting(type: SettingType<T>, name: String? = null) = getNullableSettingProvider(ListSettingType(type), name, null)
fun <T> AutoKonfig.OptionalSetSetting(type: SettingType<T>, name: String? = null) = getNullableSettingProvider(SetSettingType(type), name, null)
fun AutoKonfig.OptionalBytesSetting(name: String? = null) = getNullableSettingProvider(BytesSettingType, name, null)
fun AutoKonfig.OptionalBooleanSetting(name: String? = null) = getNullableSettingProvider(BooleanSettingType, name, null)

/**
 * Helpers for getting typed setting providers from the default AutoKonfig
 */
fun StringSetting(default: String? = null, name: String? = null) = DefaultAutoKonfig.StringSetting(default, name)
fun IntSetting(default: Int? = null, name: String? = null) = DefaultAutoKonfig.IntSetting(default, name)
fun LongSetting(default: Long? = null, name: String? = null) = DefaultAutoKonfig.LongSetting(default, name)
fun FloatSetting(default: Float? = null, name: String? = null) = DefaultAutoKonfig.FloatSetting(default, name)
fun DoubleSetting(default: Double? = null, name: String? = null) = DefaultAutoKonfig.DoubleSetting(default, name)
fun BigIntegerSetting(default: BigInteger? = null, name: String? = null) = DefaultAutoKonfig.BigIntegerSetting(default, name)
fun BigDecimalSetting(default: BigDecimal? = null, name: String? = null) = DefaultAutoKonfig.BigDecimalSetting(default, name)
fun <T : Enum<T>> EnumSetting(enum: Class<T>, default: T? = null, name: String? = null) = DefaultAutoKonfig.EnumSetting(enum, default, name)
fun <T : Enum<T>> EnumSetting(enum: KClass<T>, default: T? = null, name: String? = null) = DefaultAutoKonfig.EnumSetting(enum, default, name)
fun InstantSetting(default: Instant? = null, name: String? = null) = DefaultAutoKonfig.InstantSetting(default, name)
fun DurationSetting(default: Duration? = null, name: String? = null) = DefaultAutoKonfig.DurationSetting(default, name)
fun PeriodSetting(default: Period? = null, name: String? = null) = DefaultAutoKonfig.PeriodSetting(default, name)
fun LocalTimeSetting(default: LocalTime? = null, name: String? = null) = DefaultAutoKonfig.LocalTimeSetting(default, name)
fun LocalDateSetting(default: LocalDate? = null, name: String? = null) = DefaultAutoKonfig.LocalDateSetting(default, name)
fun LocalDateTimeSetting(default: LocalDateTime? = null, name: String? = null) = DefaultAutoKonfig.LocalDateTimeSetting(default, name)
fun <T> ListSetting(type: SettingType<T>, default: List<T>? = null, name: String? = null) = DefaultAutoKonfig.ListSetting(type, default, name)
fun <T> SetSetting(type: SettingType<T>, default: Set<T>? = null, name: String? = null) = DefaultAutoKonfig.SetSetting(type, default, name)
fun BytesSetting(default: Long? = null, name: String? = null) = DefaultAutoKonfig.BytesSetting(default, name)
fun BooleanSetting(default: Boolean? = null, name: String? = null) = DefaultAutoKonfig.BooleanSetting(default, name)
fun FlagSetting(name: String? = null) = DefaultAutoKonfig.FlagSetting(name)

/**
 * Helpers for getting optional typed setting providers from the default AutoKonfig
 */
fun OptionalStringSetting(name: String? = null) = DefaultAutoKonfig.OptionalStringSetting(name)
fun OptionalIntSetting(name: String? = null) = DefaultAutoKonfig.OptionalIntSetting(name)
fun OptionalLongSetting(name: String? = null) = DefaultAutoKonfig.OptionalLongSetting(name)
fun OptionalFloatSetting(name: String? = null) = DefaultAutoKonfig.OptionalFloatSetting(name)
fun OptionalDoubleSetting(name: String? = null) = DefaultAutoKonfig.OptionalDoubleSetting(name)
fun OptionalBigIntegerSetting(name: String? = null) = DefaultAutoKonfig.OptionalBigIntegerSetting(name)
fun OptionalBigDecimalSetting(name: String? = null) = DefaultAutoKonfig.OptionalBigDecimalSetting(name)
fun <T : Enum<T>> OptionalEnumSetting(enum: Class<T>, name: String? = null) = DefaultAutoKonfig.OptionalEnumSetting(enum, name)
fun <T : Enum<T>> OptionalEnumSetting(enum: KClass<T>, name: String? = null) = DefaultAutoKonfig.OptionalEnumSetting(enum, name)
fun OptionalInstantSetting(name: String? = null) = DefaultAutoKonfig.OptionalInstantSetting(name)
fun OptionalDurationSetting(name: String? = null) = DefaultAutoKonfig.OptionalDurationSetting(name)
fun OptionalPeriodSetting(name: String? = null) = DefaultAutoKonfig.OptionalPeriodSetting(name)
fun OptionalLocalTimeSetting(name: String? = null) = DefaultAutoKonfig.OptionalLocalTimeSetting(name)
fun OptionalLocalDateSetting(name: String? = null) = DefaultAutoKonfig.OptionalLocalDateSetting(name)
fun OptionalLocalDateTimeSetting(name: String? = null) = DefaultAutoKonfig.OptionalLocalDateTimeSetting(name)
fun <T> OptionalListSetting(type: SettingType<T>, name: String? = null) = DefaultAutoKonfig.OptionalListSetting(type, name)
fun <T> OptionalSetSetting(type: SettingType<T>, name: String? = null) = DefaultAutoKonfig.OptionalSetSetting(type, name)
fun OptionalBytesSetting(name: String? = null) = DefaultAutoKonfig.OptionalBytesSetting(name)
fun OptionalBooleanSetting(name: String? = null) = DefaultAutoKonfig.OptionalBooleanSetting(name)

/**
 * Setting groups
 */
@Suppress("MemberVisibilityCanBePrivate")
open class Group(name: String? = null) {

    private val effectiveName = name ?: javaClass.simplerName

    private val Class<*>.simplerName get() = simpleName.substringBeforeLast("$").substringAfterLast("$")

    val fullName: String by lazy {
        val names = mutableListOf<String>()
        var parentClass: Class<*>? = javaClass.enclosingClass
        while (parentClass?.superclass?.simpleName == "Group") {
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
    fun AutoKonfig.BigIntegerSetting(default: BigInteger? = null, name: String? = null) = getSettingProvider(BigIntegerSettingType, default, name, this@Group)
    fun AutoKonfig.BigDecimalSetting(default: BigDecimal? = null, name: String? = null) = getSettingProvider(BigDecimalSettingType, default, name, this@Group)
    fun AutoKonfig.DoubleSetting(default: Double? = null, name: String? = null) = getSettingProvider(DoubleSettingType, default, name, this@Group)
    fun <T : Enum<T>> AutoKonfig.EnumSetting(enum: Class<T>, default: T? = null, name: String? = null) = getSettingProvider(EnumSettingType(enum), default, name, this@Group)
    fun <T : Enum<T>> AutoKonfig.EnumSetting(enum: KClass<T>, default: T? = null, name: String? = null) = EnumSetting(enum.java, default, name)
    fun AutoKonfig.InstantSetting(default: Instant? = null, name: String? = null) = getSettingProvider(InstantSettingType, default, name, this@Group)
    fun AutoKonfig.DurationSetting(default: Duration? = null, name: String? = null) = getSettingProvider(DurationSettingType, default, name, this@Group)
    fun AutoKonfig.PeriodSetting(default: Period? = null, name: String? = null) = getSettingProvider(PeriodSettingType, default, name, this@Group)
    fun AutoKonfig.LocalTimeSetting(default: LocalTime? = null, name: String? = null) = getSettingProvider(LocalTimeSettingType, default, name, this@Group)
    fun AutoKonfig.LocalDateSetting(default: LocalDate? = null, name: String? = null) = getSettingProvider(LocalDateSettingType, default, name, this@Group)
    fun AutoKonfig.LocalDateTimeSetting(default: LocalDateTime? = null, name: String? = null) = getSettingProvider(LocalDateTimeSettingType, default, name, this@Group)
    fun <T> AutoKonfig.ListSetting(type: SettingType<T>, default: List<T>? = null, name: String? = null) = getSettingProvider(ListSettingType(type), default, name, this@Group)
    fun <T> AutoKonfig.SetSetting(type: SettingType<T>, default: Set<T>? = null, name: String? = null) = getSettingProvider(SetSettingType(type), default, name, this@Group)
    fun AutoKonfig.BytesSetting(default: Long? = null, name: String? = null) = getSettingProvider(BytesSettingType, default, name, this@Group)
    fun AutoKonfig.BooleanSetting(default: Boolean? = null, name: String? = null) = getSettingProvider(BooleanSettingType, default, name, this@Group)
    fun AutoKonfig.FlagSetting(name: String? = null) = BooleanSetting(false, name)

    /**
     * Helpers for getting optional typed setting providers
     */
    fun AutoKonfig.OptionalStringSetting(name: String? = null) = getNullableSettingProvider(StringSettingType, name, this@Group)
    fun AutoKonfig.OptionalIntSetting(name: String? = null) = getNullableSettingProvider(IntSettingType, name, this@Group)
    fun AutoKonfig.OptionalLongSetting(name: String? = null) = getNullableSettingProvider(LongSettingType, name, this@Group)
    fun AutoKonfig.OptionalFloatSetting(name: String? = null) = getNullableSettingProvider(FloatSettingType, name, this@Group)
    fun AutoKonfig.OptionalDoubleSetting(name: String? = null) = getNullableSettingProvider(DoubleSettingType, name, this@Group)
    fun AutoKonfig.OptionalBigIntegerSetting(name: String? = null) = getNullableSettingProvider(BigIntegerSettingType, name, this@Group)
    fun AutoKonfig.OptionalBigDecimalSetting(name: String? = null) = getNullableSettingProvider(BigDecimalSettingType, name, this@Group)
    fun <T : Enum<T>> AutoKonfig.OptionalEnumSetting(enum: Class<T>, name: String? = null) = getNullableSettingProvider(EnumSettingType(enum), name, this@Group)
    fun <T : Enum<T>> AutoKonfig.OptionalEnumSetting(enum: KClass<T>, name: String? = null) = OptionalEnumSetting(enum.java, name)
    fun AutoKonfig.OptionalInstantSetting(name: String? = null) = getNullableSettingProvider(InstantSettingType, name, this@Group)
    fun AutoKonfig.OptionalDurationSetting(name: String? = null) = getNullableSettingProvider(DurationSettingType, name, this@Group)
    fun AutoKonfig.OptionalPeriodSetting(name: String? = null) = getNullableSettingProvider(PeriodSettingType, name, this@Group)
    fun AutoKonfig.OptionalLocalTimeSetting(name: String? = null) = getNullableSettingProvider(LocalTimeSettingType, name, this@Group)
    fun AutoKonfig.OptionalLocalDateSetting(name: String? = null) = getNullableSettingProvider(LocalDateSettingType, name, this@Group)
    fun AutoKonfig.OptionalLocalDateTimeSetting(name: String? = null) = getNullableSettingProvider(LocalDateTimeSettingType, name, this@Group)
    fun <T> AutoKonfig.OptionalListSetting(type: SettingType<T>, name: String? = null) = getNullableSettingProvider(ListSettingType(type), name, this@Group)
    fun <T> AutoKonfig.OptionalSetSetting(type: SettingType<T>, name: String? = null) = getNullableSettingProvider(SetSettingType(type), name, this@Group)
    fun AutoKonfig.OptionalBytesSetting(name: String? = null) = getNullableSettingProvider(BytesSettingType, name, this@Group)
    fun AutoKonfig.OptionalBooleanSetting(name: String? = null) = getNullableSettingProvider(BooleanSettingType, name, this@Group)

    /**
     * Helpers for getting typed setting providers from the default AutoKonfig
     */
    fun StringSetting(default: String? = null, name: String? = null) = DefaultAutoKonfig.StringSetting(default, name)
    fun IntSetting(default: Int? = null, name: String? = null) = DefaultAutoKonfig.IntSetting(default, name)
    fun LongSetting(default: Long? = null, name: String? = null) = DefaultAutoKonfig.LongSetting(default, name)
    fun FloatSetting(default: Float? = null, name: String? = null) = DefaultAutoKonfig.FloatSetting(default, name)
    fun DoubleSetting(default: Double? = null, name: String? = null) = DefaultAutoKonfig.DoubleSetting(default, name)
    fun BigIntegerSetting(default: BigInteger? = null, name: String? = null) = DefaultAutoKonfig.BigIntegerSetting(default, name)
    fun BigDecimalSetting(default: BigDecimal? = null, name: String? = null) = DefaultAutoKonfig.BigDecimalSetting(default, name)
    fun <T : Enum<T>> EnumSetting(enum: Class<T>, default: T? = null, name: String? = null) = DefaultAutoKonfig.EnumSetting(enum, default, name)
    fun <T : Enum<T>> EnumSetting(enum: KClass<T>, default: T? = null, name: String? = null) = DefaultAutoKonfig.EnumSetting(enum, default, name)
    fun InstantSetting(default: Instant? = null, name: String? = null) = DefaultAutoKonfig.InstantSetting(default, name)
    fun DurationSetting(default: Duration? = null, name: String? = null) = DefaultAutoKonfig.DurationSetting(default, name)
    fun PeriodSetting(default: Period? = null, name: String? = null) = DefaultAutoKonfig.PeriodSetting(default, name)
    fun LocalTimeSetting(default: LocalTime? = null, name: String? = null) = DefaultAutoKonfig.LocalTimeSetting(default, name)
    fun LocalDateSetting(default: LocalDate? = null, name: String? = null) = DefaultAutoKonfig.LocalDateSetting(default, name)
    fun LocalDateTimeSetting(default: LocalDateTime? = null, name: String? = null) = DefaultAutoKonfig.LocalDateTimeSetting(default, name)
    fun <T> ListSetting(type: SettingType<T>, default: List<T>? = null, name: String? = null) = DefaultAutoKonfig.ListSetting(type, default, name)
    fun <T> SetSetting(type: SettingType<T>, default: Set<T>? = null, name: String? = null) = DefaultAutoKonfig.SetSetting(type, default, name)
    fun BytesSetting(default: Long? = null, name: String? = null) = DefaultAutoKonfig.BytesSetting(default, name)
    fun BooleanSetting(default: Boolean? = null, name: String? = null) = DefaultAutoKonfig.BooleanSetting(default, name)
    fun FlagSetting(name: String? = null) = DefaultAutoKonfig.FlagSetting(name)

    /**
     * Helpers for getting optional typed setting providers from the default AutoKonfig
     */
    fun OptionalStringSetting(name: String? = null) = DefaultAutoKonfig.OptionalStringSetting(name)
    fun OptionalIntSetting(name: String? = null) = DefaultAutoKonfig.OptionalIntSetting(name)
    fun OptionalLongSetting(name: String? = null) = DefaultAutoKonfig.OptionalLongSetting(name)
    fun OptionalFloatSetting(name: String? = null) = DefaultAutoKonfig.OptionalFloatSetting(name)
    fun OptionalDoubleSetting(name: String? = null) = DefaultAutoKonfig.OptionalDoubleSetting(name)
    fun OptionalBigIntegerSetting(name: String? = null) = DefaultAutoKonfig.OptionalBigIntegerSetting(name)
    fun OptionalBigDecimalSetting(name: String? = null) = DefaultAutoKonfig.OptionalBigDecimalSetting(name)
    fun <T : Enum<T>> OptionalEnumSetting(enum: Class<T>, name: String? = null) = DefaultAutoKonfig.OptionalEnumSetting(enum, name)
    fun <T : Enum<T>> OptionalEnumSetting(enum: KClass<T>, name: String? = null) = DefaultAutoKonfig.OptionalEnumSetting(enum, name)
    fun OptionalInstantSetting(name: String? = null) = DefaultAutoKonfig.OptionalInstantSetting(name)
    fun OptionalDurationSetting(name: String? = null) = DefaultAutoKonfig.OptionalDurationSetting(name)
    fun OptionalPeriodSetting(name: String? = null) = DefaultAutoKonfig.OptionalPeriodSetting(name)
    fun OptionalLocalTimeSetting(name: String? = null) = DefaultAutoKonfig.OptionalLocalTimeSetting(name)
    fun OptionalLocalDateSetting(name: String? = null) = DefaultAutoKonfig.OptionalLocalDateSetting(name)
    fun OptionalLocalDateTimeSetting(name: String? = null) = DefaultAutoKonfig.OptionalLocalDateTimeSetting(name)
    fun <T> OptionalListSetting(type: SettingType<T>, name: String? = null) = DefaultAutoKonfig.OptionalListSetting(type, name)
    fun <T> OptionalSetSetting(type: SettingType<T>, name: String? = null) = DefaultAutoKonfig.OptionalSetSetting(type, name)
    fun OptionalBytesSetting(name: String? = null) = DefaultAutoKonfig.OptionalBytesSetting(name)
    fun OptionalBooleanSetting(name: String? = null) = DefaultAutoKonfig.OptionalBooleanSetting(name)
}
