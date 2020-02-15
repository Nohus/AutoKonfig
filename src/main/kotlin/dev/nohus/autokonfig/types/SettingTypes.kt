@file:Suppress("FunctionName")

package dev.nohus.autokonfig.types

import com.typesafe.config.ConfigList
import dev.nohus.autokonfig.SettingParseException
import dev.nohus.autokonfig.Value
import dev.nohus.autokonfig.Value.ComplexValue
import dev.nohus.autokonfig.Value.SimpleValue
import dev.nohus.autokonfig.utils.MemoryUnit
import java.math.BigInteger
import java.time.*
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Marcin Wisniowski (Nohus) on 11/01/2020.
 */

data class SettingType<T>(val transform: (Value) -> T)
val StringSettingType: SettingType<String> = SettingType(::mapString)
val IntSettingType: SettingType<Int> = SettingType(::mapInt)
val LongSettingType: SettingType<Long> = SettingType(::mapLong)
val FloatSettingType: SettingType<Float> = SettingType(::mapFloat)
val DoubleSettingType: SettingType<Double> = SettingType(::mapDouble)
fun <T : Enum<T>> EnumSettingType(enum: Class<T>): SettingType<T> = SettingType { mapEnum(it, enum) }
val InstantSettingType: SettingType<Instant> = SettingType(::mapInstant)
val DurationSettingType: SettingType<Duration> = SettingType(::mapDuration)
val PeriodSettingType: SettingType<Period> = SettingType(::mapPeriod)
val LocalTimeSettingType: SettingType<LocalTime> = SettingType(::mapLocalTime)
val LocalDateSettingType: SettingType<LocalDate> = SettingType(::mapLocalDate)
val LocalDateTimeSettingType: SettingType<LocalDateTime> = SettingType(::mapLocalDateTime)
fun <T> ListSettingType(type: SettingType<T>): SettingType<List<T>> = SettingType { mapList(it, type) }
fun <T> SetSettingType(type: SettingType<T>): SettingType<Set<T>> = SettingType { mapSet(it, type) }
val BytesSettingType: SettingType<Long> = SettingType(::mapBytes)
val BooleanSettingType: SettingType<Boolean> = SettingType(::mapBoolean)

private fun mapString(value: Value) = value.simple
private fun mapInt(value: Value) = try { value.simple.toInt() } catch (e: NumberFormatException) { throw SettingParseException("must be an Int number", e) }
private fun mapLong(value: Value) = try { value.simple.toLong() } catch (e: NumberFormatException) { throw SettingParseException("must be a Long number", e) }
private fun mapFloat(value: Value) = try { value.simple.toFloat() } catch (e: NumberFormatException) { throw SettingParseException("must be a Float number", e) }
private fun mapDouble(value: Value) = try { value.simple.toDouble() } catch (e: NumberFormatException) { throw SettingParseException("must be a Double number", e) }
private fun <T : Enum<T>> mapEnum(value: Value, enum: Class<T>): T {
    val map = enum.enumConstants.map { it.name to it }.toMap()
    return try {
        map[value.simple] ?: map.entries.first { it.key.toLowerCase(Locale.US) == value.simple.toLowerCase(Locale.US) }.value
    } catch (e: NoSuchElementException) {
        throw SettingParseException("possible values are ${map.keys}", e)
    }
}
private fun mapInstant(value: Value) = try { Instant.parse(value.simple) } catch (e: DateTimeParseException) { throw SettingParseException("must be an Instant", e) }
private val durationUnits = mapOf(
    listOf("", "ms", "millis", "milliseconds") to TimeUnit.MILLISECONDS,
    listOf("us", "micros", "microseconds") to TimeUnit.MICROSECONDS,
    listOf("ns", "nanos", "nanoseconds") to TimeUnit.NANOSECONDS,
    listOf("s", "second", "seconds") to TimeUnit.SECONDS,
    listOf("m", "minute", "minutes") to TimeUnit.MINUTES,
    listOf("h", "hour", "hours") to TimeUnit.HOURS,
    listOf("d", "day", "days") to TimeUnit.DAYS
)
private fun mapDuration(value: Value): Duration {
    val duration = mapValueWithUnit(value.simple, durationUnits) { it.toNanos(1).toBigInteger() }
    return Duration.ofNanos(duration)
}
private val periodUnits = mapOf(
    listOf("", "d", "day", "days") to ChronoUnit.DAYS,
    listOf("w", "week", "weeks") to ChronoUnit.WEEKS,
    listOf("m", "month", "months") to ChronoUnit.MONTHS,
    listOf("y", "year", "years") to ChronoUnit.YEARS
)
private fun mapPeriod(value: Value): Period {
    val duration = mapValueWithUnit(value.simple, periodUnits) { it.duration.toDays().toBigInteger() }
    return Period.ofDays(duration.toInt())
}
private fun mapLocalTime(value: Value) = try { LocalTime.parse(value.simple) } catch (e: DateTimeParseException) { throw SettingParseException("must be a LocalTime", e) }
private fun mapLocalDate(value: Value) = try { LocalDate.parse(value.simple) } catch (e: DateTimeParseException) { throw SettingParseException("must be a LocalDate", e) }
private fun mapLocalDateTime(value: Value) = try { LocalDateTime.parse(value.simple) } catch (e: DateTimeParseException) { throw SettingParseException("must be a LocalDateTime", e) }
private fun <T> mapList(value: Value, type: SettingType<T>): List<T> {
    if (value !is ComplexValue) throw SettingParseException("is not a list")
    val configList = value.value as? ConfigList ?: throw SettingParseException("\"$value\" is not a list")
    return configList.map {
        try {
            type.transform(Value.wrap(it))
        } catch (e: SettingParseException) {
            throw SettingParseException("list element \"${Value.wrap(it)}\" ${e.reason}", e)
        }
    }
}
private fun <T> mapSet(value: Value, type: SettingType<T>) = mapList(value, type).toSet()
private fun mapBytes(value: Value): Long {
    return mapValueWithUnit(value.simple, MemoryUnit.unitsMap) { it.bytes }
}
private fun mapBoolean(value: Value) = value.simple in listOf("true", "yes", "on", "1")

private fun <T> mapValueWithUnit(value: String, units: Map<List<String>, T>, multiplier: (T) -> BigInteger): Long {
    val (numberString, unitString) = getValueWithUnit(value)

    val unit = units.entries.firstOrNull { unitString in it.key }?.value
        ?: throw SettingParseException("the unit \"$unitString\" must be one of ${units.keys.flatten().map { "\"$it\"" }}")

    return numberString.toBigIntegerOrNull()?.let { (multiplier(unit) * it).longValueExact() }
        ?: numberString.toBigDecimalOrNull()?.let { (multiplier(unit).toBigDecimal() * it).toBigInteger().longValueExact() }
        ?: throw SettingParseException("\"$numberString\" is not a number")
}

private fun getValueWithUnit(value: String): Pair<String, String> {
    val unitIndex = value.indexOfFirst { it.isLetter() }
    val unitString = if (unitIndex > -1) value.substring(unitIndex) else ""
    val numberString = (if (unitIndex > -1) value.substringBefore(unitString) else value).trim()
    if (numberString.isEmpty()) throw SettingParseException("it is missing a number")

    return numberString to unitString
}

private val Value.simple: String get() {
    if (this is SimpleValue) return value
    else {
        val complex = this as ComplexValue
        throw SettingParseException("is unexpectedly of type \"${complex.type}\"")
    }
}
