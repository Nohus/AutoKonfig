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
 * Created by Marcin Wisniowski (Nohus) on 11/01/2020.
 */

/**
 * Helpers for getting typed settings
 */
fun AutoKonfig.getString(key: String, default: String? = null): String = get(StringSettingType, key, default)
fun AutoKonfig.getInt(key: String, default: Int? = null): Int = get(IntSettingType, key, default)
fun AutoKonfig.getLong(key: String, default: Long? = null): Long = get(LongSettingType, key, default)
fun AutoKonfig.getFloat(key: String, default: Float? = null): Float = get(FloatSettingType, key, default)
fun AutoKonfig.getDouble(key: String, default: Double? = null): Double = get(DoubleSettingType, key, default)
fun AutoKonfig.getBigInteger(key: String, default: BigInteger? = null): BigInteger = get(BigIntegerSettingType, key, default)
fun AutoKonfig.getBigDecimal(key: String, default: BigDecimal? = null): BigDecimal = get(BigDecimalSettingType, key, default)
fun <T : Enum<T>> AutoKonfig.getEnum(enum: Class<T>, key: String, default: T?): T = get(EnumSettingType(enum), key, default)
fun <T : Enum<T>> AutoKonfig.getEnum(enum: KClass<T>, key: String, default: T?): T = getEnum(enum.java, key, default)
fun AutoKonfig.getInstant(key: String, default: Instant? = null): Instant = get(InstantSettingType, key, default)
fun AutoKonfig.getDuration(key: String, default: Duration? = null): Duration = get(DurationSettingType, key, default)
fun AutoKonfig.getPeriod(key: String, default: Period? = null): Period = get(PeriodSettingType, key, default)
fun AutoKonfig.getLocalTime(key: String, default: LocalTime? = null): LocalTime = get(LocalTimeSettingType, key, default)
fun AutoKonfig.getLocalDate(key: String, default: LocalDate? = null): LocalDate = get(LocalDateSettingType, key, default)
fun AutoKonfig.getLocalDateTime(key: String, default: LocalDateTime? = null): LocalDateTime = get(LocalDateTimeSettingType, key, default)
fun <T> AutoKonfig.getList(type: SettingType<T>, key: String, default: List<T>? = null) = get(ListSettingType(type), key, default)
fun <T> AutoKonfig.getSet(type: SettingType<T>, key: String, default: Set<T>? = null) = get(SetSettingType(type), key, default)
fun AutoKonfig.getBytes(key: String, default: Long? = null): Long = get(BytesSettingType, key, default)
fun AutoKonfig.getBoolean(key: String, default: Boolean? = null): Boolean = get(BooleanSettingType, key, default)
fun AutoKonfig.getFlag(key: String): Boolean = getBoolean(key, false)

/**
 * Helpers for getting typed settings from the default AutoKonfig
 */
fun AutoKonfig.Companion.getString(key: String, default: String? = null): String = DefaultAutoKonfig.getString(key, default)
fun AutoKonfig.Companion.getInt(key: String, default: Int? = null): Int = DefaultAutoKonfig.getInt(key, default)
fun AutoKonfig.Companion.getLong(key: String, default: Long? = null): Long = DefaultAutoKonfig.getLong(key, default)
fun AutoKonfig.Companion.getFloat(key: String, default: Float? = null): Float = DefaultAutoKonfig.getFloat(key, default)
fun AutoKonfig.Companion.getDouble(key: String, default: Double? = null): Double = DefaultAutoKonfig.getDouble(key, default)
fun AutoKonfig.Companion.getBigInteger(key: String, default: BigInteger? = null): BigInteger = DefaultAutoKonfig.getBigInteger(key, default)
fun AutoKonfig.Companion.getBigDecimal(key: String, default: BigDecimal? = null): BigDecimal = DefaultAutoKonfig.getBigDecimal(key, default)
fun <T : Enum<T>> AutoKonfig.Companion.getEnum(enum: Class<T>, key: String, default: T? = null): T = DefaultAutoKonfig.getEnum(enum, key, default)
fun <T : Enum<T>> AutoKonfig.Companion.getEnum(enum: KClass<T>, key: String, default: T? = null): T = DefaultAutoKonfig.getEnum(enum, key, default)
fun AutoKonfig.Companion.getInstant(key: String, default: Instant? = null): Instant = DefaultAutoKonfig.getInstant(key, default)
fun AutoKonfig.Companion.getDuration(key: String, default: Duration? = null): Duration = DefaultAutoKonfig.getDuration(key, default)
fun AutoKonfig.Companion.getPeriod(key: String, default: Period? = null): Period = DefaultAutoKonfig.getPeriod(key, default)
fun AutoKonfig.Companion.getLocalTime(key: String, default: LocalTime? = null): LocalTime = DefaultAutoKonfig.getLocalTime(key, default)
fun AutoKonfig.Companion.getLocalDate(key: String, default: LocalDate? = null): LocalDate = DefaultAutoKonfig.getLocalDate(key, default)
fun AutoKonfig.Companion.getLocalDateTime(key: String, default: LocalDateTime? = null): LocalDateTime = DefaultAutoKonfig.getLocalDateTime(key, default)
fun <T> AutoKonfig.Companion.getList(type: SettingType<T>, key: String, default: List<T>? = null) = DefaultAutoKonfig.getList(type, key, default)
fun <T> AutoKonfig.Companion.getSet(type: SettingType<T>, key: String, default: Set<T>? = null) = DefaultAutoKonfig.getSet(type, key, default)
fun AutoKonfig.Companion.getBytes(key: String, default: Long? = null): Long = DefaultAutoKonfig.getBytes(key, default)
fun AutoKonfig.Companion.getBoolean(key: String, default: Boolean? = null): Boolean = DefaultAutoKonfig.getBoolean(key, default)
fun AutoKonfig.Companion.getFlag(key: String): Boolean = DefaultAutoKonfig.getFlag(key)
