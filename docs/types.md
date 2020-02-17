# Types

AutoKonfig includes the following types of properties:

## Primitives

### String

Type|Delegate|Getter|Example
----|--------|------|-------
String|StringSetting()|getString()|`color = blue`

Any property is guaranteed to be readable as a `String`.

### Numbers

Type|Delegate|Getter|Example
----|--------|------|-------
Int|IntSetting()|getInt()|`count = 8`
Long|LongSetting()|getLong()|`max = 5000000000`
Float|FloatSetting()|getFloat()|`half = 0.5`
Double|DoubleSetting()|getDouble()|`pi = 3.141592`
BigInteger|BigIntegerSetting()|getBigInteger()|`particles = 100000000000005`
BigDecimal|BigDecimalSetting()|getBigDecimal()|`exact = 100000000000005.00002`

### Boolean

Type|Delegate|Getter|Example
----|--------|------|-------
Boolean|BooleanSetting()|getBoolean()|`sync = yes`
Boolean|FlagSetting()|getFlag()|`--verbose`

The flag type is the same as a boolean type with a default value of `false`.
It is intended as a convenience when reading command line flags (properties with no values).

The following values map to Boolean types:

True|False
----|-----
true|false
yes|no
on|off
1|0

All others are considered `false`.

!!! info
	Boolean values are case-insensitive.

## Enums

Type|Delegate|Getter|Example
----|--------|------|-------
Enum class|EnumSetting(_enum class_)|getEnum(_enum class_)|`direction = Right`

The enum type support can be used to read any enum.

Usage:

``` Kotlin
enum class Letters {
	Alpha, Beta, Gamma
}
```

``` Kotlin
val letter by EnumSetting(Letters::class)
```

The enum values are not case sensitive.

## Time

### Instant

Type|Delegate|Getter|Example
----|--------|------|-------
Instant|InstantSetting()|getInstant()|`start = 2020-02-03T10:15:30Z`

An instant is a point in time, it is parsed using the ISO-8601 instant format.

### Duration

Type|Delegate|Getter|Example
----|--------|------|-------
Duration|DurationSetting()|getDuration()|`timeout = 10s`

An amount of time. Can be suffixed by a unit, otherwise assumed to be in milliseconds. Whitespace is allowed between the number and the unit.

Unit|Suffixes
----|--------
Milliseconds|`ms`, `millis`, `milliseconds`
Microseconds|`us`, `micros`, `microseconds`
Nanoseconds|`ns`, `nanos`, `nanoseconds`
Seconds|`s`, `second`, `seconds`
Minutes|`m`, `minute`, `minutes`
Hours|`h`, `hour`, `hours`
Days|`d`, `day`, `days`

### Period

Type|Delegate|Getter|Example
----|--------|------|-------
Period|PeriodSetting()|getPeriod()|`expiry = 7 days`

A date-based amount of time. Can be suffixed by a unit, otherwise assumed to be in days. Whitespace is allowed between the number and the unit.

Unit|Suffixes
----|--------
Days|`d`, `day`, `days`
Weeks|`w`, `week`, `weeks`
Months|`m`, `month`, `months`
Years|`y`, `year`, `years`

### LocalTime

Type|Delegate|Getter|Example
----|--------|------|-------
LocalTime|LocalTimeSetting()|getLocalTime()|`backupTime = "10:30:00"`

A time without a time zone. Only the hour and minute values are required (`22:00`), but seconds and nanoseconds can be specified (`08:10:20.000000001`).

!!! warning
	A colon (`:`) can be used as a key/value separator, and so the value has to be quoted. If a file has the `.properties` extension, it is parsed in Java properties compatibility mode where it's also allowed without quoting.

### LocalDate

Type|Delegate|Getter|Example
----|--------|------|-------
LocalDate|LocalDateSetting()|getLocalDate()|`update = 2020-02-05`

A date without a time zone.

### LocalDateTime

Type|Delegate|Getter|Example
----|--------|------|-------
LocalDateTime|LocalDateTimeSetting()|getLocalDateTime()|`update = "2020-01-09T10:30:00"`

A date and time without a time zone. Parsed like a [LocalDate](#localdate) and [LocalTime](#localtime), with the addition of a `T` delimeter.

## Collections

Collections are lists or sets of values of any other type. To create a collection delegate or get a collection, a type has to be provided:

``` Kotlin
val numbers by ListSetting(IntSettingType)
```

``` Kotlin
val numbers = AutoKonfig.getList(IntSettingType)
```

Parsing rules are identical to rules for the element type, with comma (`,`) delimeters between elements and the array enclosed in square brackets (`[`, `]`).

!!! tip
	Setting type names are similar to delegate names, but have an additional `Type` suffix.

!!! info
	Nested collections are also supported: `AutoKonfig.getList(ListSettingType(IntSettingType))`

### Lists

Type|Delegate|Getter|Example
----|--------|------|-------
List<_type_>|ListSetting(_type_)|getList(_type_)|`numbers = [1,2,3,1,2]`

An ordered list of elements.

### Sets

Type|Delegate|Getter|Example
----|--------|------|-------
Set<_type_>|SetSetting(_type_)|getSet(_type_)|`selections = [1,2,3]`

An unordered set of elements with duplicates ignored.

## Bytes

Type|Delegate|Getter|Example
----|--------|------|-------
Long|BytesSetting()|getBytes()|`cache = 512MB`

A number of bytes. Can be suffixed by a unit, otherwise assumed to be in bytes. Whitespace is allowed between the number and the unit.

#### Decimal units

Unit|Bytes|Suffixes
----|-----|--------
Bytes|1|`b`, `B`, `byte`, `bytes`
Kilobytes|1000|`kB`, `kilobyte`, `kilobytes`
Megabytes|1000<sup>2</sup>|`MB`, `megabyte`, `megabytes`
Gigabytes|1000<sup>3</sup>|`GB`, `gigabyte`, `gigabytes`
Terabytes|1000<sup>4</sup>|`TB`, `terabyte`, `terabytes`
Petabytes|1000<sup>5</sup>|`PB`, `petabyte`, `petabytes`
Exabytes|1000<sup>6</sup>|`EB`, `exabyte`, `exabytes`
Zettabytes|1000<sup>7</sup>|`ZB`, `zettabyte`, `zettabytes`
Yottabytes|1000<sup>8</sup>|`YB`, `yottabyte`, `yottabytes`

#### Binary units

Unit|Bytes|Suffixes
----|-----|--------
Bytes|1|`b`, `B`, `byte`, `bytes`
Kibibytes|1024|`k`, `K`, `Ki`, `KiB`, `kibibyte`, `kibibytes`
Mebibytes|1024<sup>2</sup>|`m`, `M`, `Mi`, `MiB`, `mebibyte`, `mebibytes`
Gibibytes|1024<sup>3</sup>|`g`, `G`, `Gi`, `GiB`, `gibibyte`, `gibibytes`
Tebibytes|1024<sup>4</sup>|`t`, `T`, `Ti`, `TiB`, `tebibyte`, `tebibytes`
Pebibytes|1024<sup>5</sup>|`p`, `P`, `Pi`, `PiB`, `pebibyte`, `pebibytes`
Exbibytes|1024<sup>6</sup>|`e`, `E`, `Ei`, `EiB`, `exbibyte`, `exbibytes`
Zebibytes|1024<sup>7</sup>|`z`, `Z`, `Zi`, `ZiB`, `zebibyte`, `zebibytes`
Yobibytes|1024<sup>8</sup>|`y`, `Y`, `Yi`, `YiB`, `yobibyte`, `yobibytes`
