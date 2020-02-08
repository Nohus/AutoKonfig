package dev.nohus.autokonfig

import dev.nohus.autokonfig.types.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Duration
import java.time.LocalTime
import java.time.Period

/**
 * Created by Marcin Wisniowski (Nohus) on 08/02/2020.
 */

class SettingsTypesTest : BaseAutoKonfigTest() {

    @Test
    fun `boolean settings can be read`() {
        """
            a = true
            b = yes
            c = on
            d = 1
            e = false
            f = no
            g = off
            h = 0
        """.trimIndent().useAsProperties()
        val a by BooleanSetting()
        val b by BooleanSetting()
        val c by BooleanSetting()
        val d by BooleanSetting()
        val e by BooleanSetting()
        val f by BooleanSetting()
        val g by BooleanSetting()
        val h by BooleanSetting()
        Assertions.assertTrue(a)
        Assertions.assertTrue(b)
        Assertions.assertTrue(c)
        Assertions.assertTrue(d)
        Assertions.assertFalse(e)
        Assertions.assertFalse(f)
        Assertions.assertFalse(g)
        Assertions.assertFalse(h)
    }

    @Test
    fun `flag settings are false by default`() {
        """
            a = true
        """.trimIndent().useAsProperties()
        val a by FlagSetting()
        val b by FlagSetting()
        Assertions.assertTrue(a)
        Assertions.assertFalse(b)
    }

    object TypesGroup : Group() {
        val string by StringSetting()
        val int by IntSetting()
        val long by LongSetting()
        val float by FloatSetting()
        val double by DoubleSetting()
        val bytes by BytesSetting()
        val boolean by BooleanSetting()
        val flag by FlagSetting()
    }

    @Test
    fun `settings of all basic types can be read`() {
        """
            string = hello
            int = 10
            long = 3000000000
            float = 3.14
            double = 3.1415
            bytes = 512kB
            boolean = false
            flag = true
        """.trimIndent().useAsProperties()
        val string by StringSetting()
        val int by IntSetting()
        val long by LongSetting()
        val float by FloatSetting()
        val double by DoubleSetting()
        val bytes by BytesSetting()
        val boolean by BooleanSetting()
        val flag by FlagSetting()
        Assertions.assertEquals("hello", string)
        Assertions.assertEquals(10, int)
        Assertions.assertEquals(3000000000, long)
        Assertions.assertEquals(3.14f, float)
        Assertions.assertEquals(3.1415, double)
        Assertions.assertEquals(512000, bytes)
        Assertions.assertFalse(boolean)
        Assertions.assertTrue(flag)
    }

    @Test
    fun `settings of all basic types can be read in a group`() {
        """
            typesGroup.string = hello
            typesGroup.int = 10
            typesGroup.long = 3000000000
            typesGroup.float = 3.14
            typesGroup.double = 3.1415
            typesGroup.bytes = 512kB
            typesGroup.boolean = false
            typesGroup.flag = true
        """.trimIndent().useAsProperties()
        Assertions.assertEquals("hello", TypesGroup.string)
        Assertions.assertEquals(10, TypesGroup.int)
        Assertions.assertEquals(3000000000, TypesGroup.long)
        Assertions.assertEquals(3.14f, TypesGroup.float)
        Assertions.assertEquals(3.1415, TypesGroup.double)
        Assertions.assertEquals(512000, TypesGroup.bytes)
        Assertions.assertFalse(TypesGroup.boolean)
        Assertions.assertTrue(TypesGroup.flag)
    }

    @Test
    fun `settings of all basic types can be read directly`() {
        """
            string = hello
            int = 10
            long = 3000000000
            float = 3.14
            double = 3.1415
            bytes = 512kB
            boolean = false
            flag = true
        """.trimIndent().useAsProperties()
        Assertions.assertEquals("hello", AutoKonfig.getString("string"))
        Assertions.assertEquals(10, AutoKonfig.getInt("int"))
        Assertions.assertEquals(3000000000, AutoKonfig.getLong("long"))
        Assertions.assertEquals(3.14f, AutoKonfig.getFloat("float"))
        Assertions.assertEquals(3.1415, AutoKonfig.getDouble("double"))
        Assertions.assertEquals(512000, AutoKonfig.getBytes("bytes"))
        Assertions.assertFalse(AutoKonfig.getBoolean("boolean"))
        Assertions.assertTrue(AutoKonfig.getFlag("flag"))
    }

    private enum class Letters {
        Alpha, Beta
    }

    private object EnumGroup : Group() {
        val setting by EnumSetting(Letters::class)
        val settingJava by EnumSetting(Letters::class.java, name = "setting")
    }

    @Test
    fun `enum settings can be read`() {
        """
            EnumGroup.setting = Alpha
        """.trimIndent().useAsProperties()
        Assertions.assertEquals(Letters.Alpha, EnumGroup.setting)
        Assertions.assertEquals(Letters.Alpha, EnumGroup.settingJava)
    }

    @Test
    fun `enum settings can be read directly`() {
        """
            setting = Alpha
        """.trimIndent().useAsProperties()
        Assertions.assertEquals(Letters.Alpha, AutoKonfig.getEnum(Letters::class, "setting"))
        Assertions.assertEquals(Letters.Alpha, AutoKonfig.getEnum(Letters::class.java, "setting"))
    }

    @Test
    fun `enum settings are case-insensitive`() {
        """
            EnumGroup.setting = beTA
        """.trimIndent().useAsProperties()
        Assertions.assertEquals(Letters.Beta, EnumGroup.setting)
        Assertions.assertEquals(Letters.Beta, EnumGroup.settingJava)
    }

    @Test
    fun `invalid enum value throws an exception`() {
        """
            setting = Gamma
        """.trimIndent().useAsProperties()
        val exception = assertThrows<AutoKonfigException> {
            val setting by EnumSetting(Letters::class)
        }
        val exceptionJava = assertThrows<AutoKonfigException> {
            val settingJava by EnumSetting(Letters::class.java, name = "setting")
        }
        Assertions.assertEquals(
            "Failed to parse setting \"setting\", the value is \"Gamma\", but possible values are [Alpha, Beta]",
            exception.message
        )
        Assertions.assertEquals(
            "Failed to parse setting \"setting\", the value is \"Gamma\", but possible values are [Alpha, Beta]",
            exceptionJava.message
        )
    }

    @Test
    fun `temporal settings can be read`() {
        """
            instant = 2011-12-03T10:15:30Z
            duration = 10s
            period = 10d
            local-time = 10:15:30
            local-date = 2020-01-09
            local-date-time = 2020-01-09T10:15:30
        """.trimIndent().useAsProperties()
        val instant by InstantSetting()
        val duration by DurationSetting()
        val period by PeriodSetting()
        val localTime by LocalTimeSetting()
        val localDate by LocalDateSetting()
        val localDateTime by LocalDateTimeSetting()
        Assertions.assertEquals("2011-12-03T10:15:30Z", instant.toString())
        Assertions.assertEquals(Duration.ofSeconds(10), duration)
        Assertions.assertEquals(Period.ofDays(10), period)
        Assertions.assertEquals("10:15:30", localTime.toString())
        Assertions.assertEquals("2020-01-09", localDate.toString())
        Assertions.assertEquals("2020-01-09T10:15:30", localDateTime.toString())
    }

    private object Temporal : Group() {
        val instant by InstantSetting()
        val duration by DurationSetting()
        val period by PeriodSetting()
        val localTime by LocalTimeSetting()
        val localDate by LocalDateSetting()
        val localDateTime by LocalDateTimeSetting()
    }

    @Test
    fun `temporal settings can be read in a group`() {
        """
            temporal.instant = 2011-12-03T10:15:30Z
            temporal.duration = 10s
            temporal.period = 10d
            temporal.local-time = 10:15:30
            temporal.local-date = 2020-01-09
            temporal.local-date-time = 2020-01-09T10:15:30
        """.trimIndent().useAsProperties()
        Assertions.assertEquals("2011-12-03T10:15:30Z", Temporal.instant.toString())
        Assertions.assertEquals(Duration.ofSeconds(10), Temporal.duration)
        Assertions.assertEquals(Period.ofDays(10), Temporal.period)
        Assertions.assertEquals("10:15:30", Temporal.localTime.toString())
        Assertions.assertEquals("2020-01-09", Temporal.localDate.toString())
        Assertions.assertEquals("2020-01-09T10:15:30", Temporal.localDateTime.toString())
    }

    @Test
    fun `temporal settings can be read directly`() {
        """
            instant = 2011-12-03T10:15:30Z
            duration = 10s
            local-time = 10:15:30
            local-date = 2020-01-09
            local-date-time = 2020-01-09T10:15:30
        """.trimIndent().useAsProperties()
        Assertions.assertEquals("2011-12-03T10:15:30Z", AutoKonfig.getInstant("instant").toString())
        Assertions.assertEquals(Duration.ofSeconds(10), AutoKonfig.getDuration("duration"))
        Assertions.assertEquals("10:15:30", AutoKonfig.getLocalTime("local-time").toString())
        Assertions.assertEquals("2020-01-09", AutoKonfig.getLocalDate("local-date").toString())
        Assertions.assertEquals("2020-01-09T10:15:30", AutoKonfig.getLocalDateTime("local-date-time").toString())
    }

    @Test
    fun `invalid temporal values throw exceptions`() {
        """
            instant = invalid
            duration = invalid
            local-time = invalid
            local-date = invalid
            local-date-time = invalid
        """.trimIndent().useAsProperties()
        var exception: AutoKonfigException = assertThrows {
            val instant by InstantSetting()
        }
        Assertions.assertEquals(
            "Failed to parse setting \"instant\", the value is \"invalid\", but must be an Instant",
            exception.message
        )
        exception = assertThrows {
            val duration by DurationSetting()
        }
        Assertions.assertEquals(
            "Failed to parse setting \"duration\", the value is \"invalid\", but it is missing a number",
            exception.message
        )
        exception = assertThrows {
            val localTime by LocalTimeSetting()
        }
        Assertions.assertEquals(
            "Failed to parse setting \"localTime\", the value is \"invalid\", but must be a LocalTime",
            exception.message
        )
        exception = assertThrows {
            val localDate by LocalDateSetting()
        }
        Assertions.assertEquals(
            "Failed to parse setting \"localDate\", the value is \"invalid\", but must be a LocalDate",
            exception.message
        )
        exception = assertThrows {
            val localDateTime by LocalDateTimeSetting()
        }
        Assertions.assertEquals(
            "Failed to parse setting \"localDateTime\", the value is \"invalid\", but must be a LocalDateTime",
            exception.message
        )
    }

    @Test
    fun `LocalTime settings can be read`() {
        """
            a = "05:00"
            b = "22:30:00"
            c = "07:20:40.5"
            d = "08:10:20.000000001"
        """.trimIndent().useAsHocon()
        Assertions.assertEquals(LocalTime.of(5, 0), AutoKonfig.getLocalTime("a"))
        Assertions.assertEquals(LocalTime.of(22, 30), AutoKonfig.getLocalTime("b"))
        Assertions.assertEquals(LocalTime.of(7, 20, 40, 500000000), AutoKonfig.getLocalTime("c"))
        Assertions.assertEquals(LocalTime.of(8, 10, 20, 1), AutoKonfig.getLocalTime("d"))
    }

    @Test
    fun `natural Duration settings can be read`() {
        """
            plain = 10
            unit = 20s
            space = 25 s
            whitespace = 30    s
            nanos = 50ns
            long = 100000 days
            fraction = 0.5 day
        """.trimIndent().useAsHocon()
        Assertions.assertEquals(Duration.ofMillis(10), AutoKonfig.getDuration("plain"))
        Assertions.assertEquals(Duration.ofSeconds(20), AutoKonfig.getDuration("unit"))
        Assertions.assertEquals(Duration.ofSeconds(25), AutoKonfig.getDuration("space"))
        Assertions.assertEquals(Duration.ofSeconds(30), AutoKonfig.getDuration("whitespace"))
        Assertions.assertEquals(Duration.ofNanos(50), AutoKonfig.getDuration("nanos"))
        Assertions.assertEquals(Duration.ofDays(100000), AutoKonfig.getDuration("long"))
        Assertions.assertEquals(Duration.ofHours(12), AutoKonfig.getDuration("fraction"))
    }

    @Test
    fun `invalid natural Duration values throw exceptions`() {
        """
            missing = 
            onlyUnit = days
            invalidUnit = 5 whiles
            invalidNumber = 5.5.5 seconds
            unitFirst = seconds 10
        """.trimIndent().useAsProperties()
        var exception: AutoKonfigException = assertThrows {
            AutoKonfig.getDuration("missing")
        }
        Assertions.assertEquals(
            "Failed to parse setting \"missing\", the value is \"\", but it is missing a number",
            exception.message
        )
        exception = assertThrows {
            AutoKonfig.getDuration("onlyUnit")
        }
        Assertions.assertEquals(
            "Failed to parse setting \"onlyUnit\", the value is \"days\", but it is missing a number",
            exception.message
        )
        exception = assertThrows {
            AutoKonfig.getDuration("invalidUnit")
        }
        Assertions.assertEquals(
            "Failed to parse setting \"invalidUnit\", the value is \"5 whiles\", but the unit \"whiles\" must be one of [\"\", \"ms\", \"millis\", \"milliseconds\", \"us\", \"micros\", \"microseconds\", \"ns\", \"nanos\", \"nanoseconds\", \"s\", \"second\", \"seconds\", \"m\", \"minute\", \"minutes\", \"h\", \"hour\", \"hours\", \"d\", \"day\", \"days\"]",
            exception.message
        )
        exception = assertThrows {
            AutoKonfig.getDuration("invalidNumber")
        }
        Assertions.assertEquals(
            "Failed to parse setting \"invalidNumber\", the value is \"5.5.5 seconds\", but \"5.5.5\" is not a number",
            exception.message
        )

        exception = assertThrows {
            AutoKonfig.getDuration("unitFirst")
        }
        Assertions.assertEquals(
            "Failed to parse setting \"unitFirst\", the value is \"seconds 10\", but it is missing a number",
            exception.message
        )
    }

    @Test
    fun `natural Period settings are parsed correctly`() {
        """
            plain = 10
            unit = 20m
            space = 25 w
            whitespace = 30    y
            days = 50day
            long = 100000 weeks
            fraction = 0.5 month
        """.trimIndent().useAsHocon()
        Assertions.assertEquals(Period.ofDays(10), AutoKonfig.getPeriod("plain"))
        Assertions.assertEquals(Period.ofDays(600), AutoKonfig.getPeriod("unit"))
        Assertions.assertEquals(Period.ofWeeks(25), AutoKonfig.getPeriod("space"))
        Assertions.assertEquals(Period.ofDays(10950), AutoKonfig.getPeriod("whitespace"))
        Assertions.assertEquals(Period.ofDays(50), AutoKonfig.getPeriod("days"))
        Assertions.assertEquals(Period.ofWeeks(100000), AutoKonfig.getPeriod("long"))
        Assertions.assertEquals(Period.ofDays(15), AutoKonfig.getPeriod("fraction"))
    }

    @Test
    fun `invalid natural Period values throw exceptions`() {
        """
            missing = 
            onlyUnit = days
            invalidUnit = 5 whiles
            invalidNumber = 5.5.5 days
            unitFirst = days 10
        """.trimIndent().useAsProperties()
        var exception: AutoKonfigException = assertThrows {
            AutoKonfig.getPeriod("missing")
        }
        Assertions.assertEquals(
            "Failed to parse setting \"missing\", the value is \"\", but it is missing a number",
            exception.message
        )
        exception = assertThrows {
            AutoKonfig.getPeriod("onlyUnit")
        }
        Assertions.assertEquals(
            "Failed to parse setting \"onlyUnit\", the value is \"days\", but it is missing a number",
            exception.message
        )
        exception = assertThrows {
            AutoKonfig.getPeriod("invalidUnit")
        }
        Assertions.assertEquals(
            "Failed to parse setting \"invalidUnit\", the value is \"5 whiles\", but the unit \"whiles\" must be one of [\"\", \"d\", \"day\", \"days\", \"w\", \"week\", \"weeks\", \"m\", \"month\", \"months\", \"y\", \"year\", \"years\"]",
            exception.message
        )
        exception = assertThrows {
            AutoKonfig.getPeriod("invalidNumber")
        }
        Assertions.assertEquals(
            "Failed to parse setting \"invalidNumber\", the value is \"5.5.5 days\", but \"5.5.5\" is not a number",
            exception.message
        )

        exception = assertThrows {
            AutoKonfig.getPeriod("unitFirst")
        }
        Assertions.assertEquals(
            "Failed to parse setting \"unitFirst\", the value is \"days 10\", but it is missing a number",
            exception.message
        )
    }

    @Test
    fun `bytes settings are parsed correctly`() {
        """
            a = 1
            b = 1 b
            c = 1 B
            d = 1 byte
            e = 1 bytes
            f = 1 mebibyte
            g = 1 mebibytes
            h = 1 m
            i = 1 M
            j = 1 Mi
            k = 1 MiB
            l = 1 megabyte
            m = 1 megabytes
            n = 1 MB
        """.trimIndent().useAsHocon()
        Assertions.assertEquals(1, AutoKonfig.getBytes("a"))
        Assertions.assertEquals(1, AutoKonfig.getBytes("b"))
        Assertions.assertEquals(1, AutoKonfig.getBytes("c"))
        Assertions.assertEquals(1, AutoKonfig.getBytes("d"))
        Assertions.assertEquals(1, AutoKonfig.getBytes("e"))
        Assertions.assertEquals(1_048_576, AutoKonfig.getBytes("f"))
        Assertions.assertEquals(1_048_576, AutoKonfig.getBytes("g"))
        Assertions.assertEquals(1_048_576, AutoKonfig.getBytes("h"))
        Assertions.assertEquals(1_048_576, AutoKonfig.getBytes("i"))
        Assertions.assertEquals(1_048_576, AutoKonfig.getBytes("j"))
        Assertions.assertEquals(1_048_576, AutoKonfig.getBytes("k"))
        Assertions.assertEquals(1_000_000, AutoKonfig.getBytes("l"))
        Assertions.assertEquals(1_000_000, AutoKonfig.getBytes("m"))
        Assertions.assertEquals(1_000_000, AutoKonfig.getBytes("n"))
    }

    object Collections : Group() {
        val strings by ListSetting(StringSettingType)
        val strings2 by ListSetting(StringSettingType, ",", name = "strings")
        val strings3 by ListSetting(StringSettingType, Regex(","), name = "strings")
        val numbers by SetSetting(IntSettingType)
        val numbers2 by SetSetting(IntSettingType, ",", name = "numbers")
        val numbers3 by SetSetting(IntSettingType, Regex(","), name = "numbers")
    }

    @Test
    fun `list settings can be read`() {
        """
            strings = a,b,c
            numbers = 1,2,3
        """.trimIndent().useAsProperties()
        val strings by ListSetting(StringSettingType)
        val numbers by SetSetting(IntSettingType)
        Assertions.assertEquals(listOf("a", "b", "c"), strings)
        Assertions.assertEquals(setOf(1, 2, 3), numbers)
    }

    @Test
    fun `list settings can be read in a group`() {
        """
            collections.strings = a,b,c
            collections.numbers = 1,2,3,2,1
        """.trimIndent().useAsProperties()
        Assertions.assertEquals(listOf("a", "b", "c"), Collections.strings)
        Assertions.assertEquals(listOf("a", "b", "c"), Collections.strings2)
        Assertions.assertEquals(listOf("a", "b", "c"), Collections.strings3)
        Assertions.assertEquals(setOf(1, 2, 3), Collections.numbers)
        Assertions.assertEquals(setOf(1, 2, 3), Collections.numbers2)
        Assertions.assertEquals(setOf(1, 2, 3), Collections.numbers3)
    }

    @Test
    fun `list settings can be read directly`() {
        """
            strings = a,b,c
            numbers = 1,2,3
        """.trimIndent().useAsProperties()
        Assertions.assertEquals(listOf("a", "b", "c"), AutoKonfig.getList(StringSettingType, "strings"))
        Assertions.assertEquals(listOf("a", "b", "c"), AutoKonfig.getList(StringSettingType, ",", "strings"))
        Assertions.assertEquals(listOf("a", "b", "c"), AutoKonfig.getList(StringSettingType, Regex(","), "strings"))
        Assertions.assertEquals(setOf(1, 2, 3), AutoKonfig.getSet(IntSettingType, "numbers"))
        Assertions.assertEquals(setOf(1, 2, 3), AutoKonfig.getSet(IntSettingType, ",", "numbers"))
        Assertions.assertEquals(setOf(1, 2, 3), AutoKonfig.getSet(IntSettingType, Regex(","), "numbers"))
    }

    @Test
    fun `list settings with custom separators can be read`() {
        """
            commas = 1,2,3
            commasAndWhitespace = 1,  2,      3
            dots = 1.2.3
            complex = 1AbC2teSt3
        """.trimIndent().useAsProperties()
        val commas by ListSetting(IntSettingType, ",")
        val commasAndWhitespace by ListSetting(IntSettingType, Regex(",\\s+"))
        val dots by SetSetting(IntSettingType, ".")
        val complex by SetSetting(IntSettingType, Regex("[A-z]+"))
        Assertions.assertArrayEquals(listOf(1, 2, 3).toTypedArray(), commas.toTypedArray())
        Assertions.assertArrayEquals(listOf(1, 2, 3).toTypedArray(), commasAndWhitespace.toTypedArray())
        Assertions.assertArrayEquals(setOf(1, 2, 3).toTypedArray(), dots.toTypedArray())
        Assertions.assertArrayEquals(setOf(1, 2, 3).toTypedArray(), complex.toTypedArray())
    }

    @Test
    fun `nested list settings can be read`() {
        """
            nested = 1 2 3|4 5 6|7 8 9
        """.trimIndent().useAsProperties()
        Assertions.assertEquals(
            listOf(listOf(1, 2, 3), listOf(4, 5, 6), listOf(7, 8, 9)),
            AutoKonfig.getList(ListSettingType(IntSettingType, " "), "|", "nested")
        )
    }
}
