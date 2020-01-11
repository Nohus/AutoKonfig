package dev.nohus.autokonfig

import java.time.*
import java.time.format.DateTimeParseException
import java.util.*

/**
 * Created by Marcin Wisniowski (Nohus) on 11/01/2020.
 */

data class SettingType<T>(val transform: (String) -> T)

val StringSettingType: SettingType<String> = SettingType(::mapString)
val IntSettingType: SettingType<Int> = SettingType(::mapInt)
val LongSettingType: SettingType<Long> = SettingType(::mapLong)
val FloatSettingType: SettingType<Float> = SettingType(::mapFloat)
val DoubleSettingType: SettingType<Double> = SettingType(::mapDouble)
fun <T : Enum<T>> EnumSettingType(enum: Class<T>): SettingType<T> = SettingType { mapEnum(it, getEnumMapping(enum)) }
val InstantSettingType: SettingType<Instant> = SettingType(::mapInstant)
val DurationSettingType: SettingType<Duration> = SettingType(::mapDuration)
val LocalTimeSettingType: SettingType<LocalTime> = SettingType(::mapLocalTime)
val LocalDateSettingType: SettingType<LocalDate> = SettingType(::mapLocalDate)
val LocalDateTimeSettingType: SettingType<LocalDateTime> = SettingType(::mapLocalDateTime)
val BooleanSettingType: SettingType<Boolean> = SettingType(::mapBoolean)

private fun <T: Enum<T>> getEnumMapping(enum: Class<T>): Map<String, T> {
    return EnumSet.allOf(enum).map { it.name to it }.toMap()
}

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
