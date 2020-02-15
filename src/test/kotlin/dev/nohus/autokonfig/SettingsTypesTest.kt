package dev.nohus.autokonfig

import dev.nohus.autokonfig.types.*
import org.junit.jupiter.api.Assertions.*
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
            b = Yes
            c = on
            d = 1
            e = FALSE
            f = no
            g = oFf
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
        assertTrue(a)
        assertTrue(b)
        assertTrue(c)
        assertTrue(d)
        assertFalse(e)
        assertFalse(f)
        assertFalse(g)
        assertFalse(h)
    }

    @Test
    fun `flag settings are false by default`() {
        """
            a = true
        """.trimIndent().useAsProperties()
        val a by FlagSetting()
        val b by FlagSetting()
        assertTrue(a)
        assertFalse(b)
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
        assertEquals("hello", string)
        assertEquals(10, int)
        assertEquals(3000000000, long)
        assertEquals(3.14f, float)
        assertEquals(3.1415, double)
        assertEquals(512000, bytes)
        assertFalse(boolean)
        assertTrue(flag)
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
        assertEquals("hello", TypesGroup.string)
        assertEquals(10, TypesGroup.int)
        assertEquals(3000000000, TypesGroup.long)
        assertEquals(3.14f, TypesGroup.float)
        assertEquals(3.1415, TypesGroup.double)
        assertEquals(512000, TypesGroup.bytes)
        assertFalse(TypesGroup.boolean)
        assertTrue(TypesGroup.flag)
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
        assertEquals("hello", AutoKonfig.getString("string"))
        assertEquals(10, AutoKonfig.getInt("int"))
        assertEquals(3000000000, AutoKonfig.getLong("long"))
        assertEquals(3.14f, AutoKonfig.getFloat("float"))
        assertEquals(3.1415, AutoKonfig.getDouble("double"))
        assertEquals(512000, AutoKonfig.getBytes("bytes"))
        assertFalse(AutoKonfig.getBoolean("boolean"))
        assertTrue(AutoKonfig.getFlag("flag"))
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
        assertEquals(Letters.Alpha, EnumGroup.setting)
        assertEquals(Letters.Alpha, EnumGroup.settingJava)
    }

    @Test
    fun `enum settings can be read directly`() {
        """
            setting = Alpha
        """.trimIndent().useAsProperties()
        assertEquals(Letters.Alpha, AutoKonfig.getEnum(Letters::class, "setting"))
        assertEquals(Letters.Alpha, AutoKonfig.getEnum(Letters::class.java, "setting"))
    }

    @Test
    fun `enum settings are case-insensitive`() {
        """
            EnumGroup.setting = beTA
        """.trimIndent().useAsProperties()
        assertEquals(Letters.Beta, EnumGroup.setting)
        assertEquals(Letters.Beta, EnumGroup.settingJava)
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
        assertEquals(
            "Failed to parse setting \"setting\", the value is \"Gamma\", but possible values are [Alpha, Beta]",
            exception.message
        )
        assertEquals(
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
        assertEquals("2011-12-03T10:15:30Z", instant.toString())
        assertEquals(Duration.ofSeconds(10), duration)
        assertEquals(Period.ofDays(10), period)
        assertEquals("10:15:30", localTime.toString())
        assertEquals("2020-01-09", localDate.toString())
        assertEquals("2020-01-09T10:15:30", localDateTime.toString())
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
        assertEquals("2011-12-03T10:15:30Z", Temporal.instant.toString())
        assertEquals(Duration.ofSeconds(10), Temporal.duration)
        assertEquals(Period.ofDays(10), Temporal.period)
        assertEquals("10:15:30", Temporal.localTime.toString())
        assertEquals("2020-01-09", Temporal.localDate.toString())
        assertEquals("2020-01-09T10:15:30", Temporal.localDateTime.toString())
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
        assertEquals("2011-12-03T10:15:30Z", AutoKonfig.getInstant("instant").toString())
        assertEquals(Duration.ofSeconds(10), AutoKonfig.getDuration("duration"))
        assertEquals("10:15:30", AutoKonfig.getLocalTime("local-time").toString())
        assertEquals("2020-01-09", AutoKonfig.getLocalDate("local-date").toString())
        assertEquals("2020-01-09T10:15:30", AutoKonfig.getLocalDateTime("local-date-time").toString())
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
        assertEquals(
            "Failed to parse setting \"instant\", the value is \"invalid\", but must be an Instant",
            exception.message
        )
        exception = assertThrows {
            val duration by DurationSetting()
        }
        assertEquals(
            "Failed to parse setting \"duration\", the value is \"invalid\", but it is missing a number",
            exception.message
        )
        exception = assertThrows {
            val localTime by LocalTimeSetting()
        }
        assertEquals(
            "Failed to parse setting \"localTime\", the value is \"invalid\", but must be a LocalTime",
            exception.message
        )
        exception = assertThrows {
            val localDate by LocalDateSetting()
        }
        assertEquals(
            "Failed to parse setting \"localDate\", the value is \"invalid\", but must be a LocalDate",
            exception.message
        )
        exception = assertThrows {
            val localDateTime by LocalDateTimeSetting()
        }
        assertEquals(
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
        assertEquals(LocalTime.of(5, 0), AutoKonfig.getLocalTime("a"))
        assertEquals(LocalTime.of(22, 30), AutoKonfig.getLocalTime("b"))
        assertEquals(LocalTime.of(7, 20, 40, 500000000), AutoKonfig.getLocalTime("c"))
        assertEquals(LocalTime.of(8, 10, 20, 1), AutoKonfig.getLocalTime("d"))
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
        assertEquals(Duration.ofMillis(10), AutoKonfig.getDuration("plain"))
        assertEquals(Duration.ofSeconds(20), AutoKonfig.getDuration("unit"))
        assertEquals(Duration.ofSeconds(25), AutoKonfig.getDuration("space"))
        assertEquals(Duration.ofSeconds(30), AutoKonfig.getDuration("whitespace"))
        assertEquals(Duration.ofNanos(50), AutoKonfig.getDuration("nanos"))
        assertEquals(Duration.ofDays(100000), AutoKonfig.getDuration("long"))
        assertEquals(Duration.ofHours(12), AutoKonfig.getDuration("fraction"))
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
        assertEquals(
            "Failed to parse setting \"missing\", the value is \"\", but it is missing a number",
            exception.message
        )
        exception = assertThrows {
            AutoKonfig.getDuration("onlyUnit")
        }
        assertEquals(
            "Failed to parse setting \"onlyUnit\", the value is \"days\", but it is missing a number",
            exception.message
        )
        exception = assertThrows {
            AutoKonfig.getDuration("invalidUnit")
        }
        assertEquals(
            "Failed to parse setting \"invalidUnit\", the value is \"5 whiles\", but the unit \"whiles\" must be one of [\"\", \"ms\", \"millis\", \"milliseconds\", \"us\", \"micros\", \"microseconds\", \"ns\", \"nanos\", \"nanoseconds\", \"s\", \"second\", \"seconds\", \"m\", \"minute\", \"minutes\", \"h\", \"hour\", \"hours\", \"d\", \"day\", \"days\"]",
            exception.message
        )
        exception = assertThrows {
            AutoKonfig.getDuration("invalidNumber")
        }
        assertEquals(
            "Failed to parse setting \"invalidNumber\", the value is \"5.5.5 seconds\", but \"5.5.5\" is not a number",
            exception.message
        )

        exception = assertThrows {
            AutoKonfig.getDuration("unitFirst")
        }
        assertEquals(
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
        assertEquals(Period.ofDays(10), AutoKonfig.getPeriod("plain"))
        assertEquals(Period.ofDays(600), AutoKonfig.getPeriod("unit"))
        assertEquals(Period.ofWeeks(25), AutoKonfig.getPeriod("space"))
        assertEquals(Period.ofDays(10950), AutoKonfig.getPeriod("whitespace"))
        assertEquals(Period.ofDays(50), AutoKonfig.getPeriod("days"))
        assertEquals(Period.ofWeeks(100000), AutoKonfig.getPeriod("long"))
        assertEquals(Period.ofDays(15), AutoKonfig.getPeriod("fraction"))
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
        assertEquals(
            "Failed to parse setting \"missing\", the value is \"\", but it is missing a number",
            exception.message
        )
        exception = assertThrows {
            AutoKonfig.getPeriod("onlyUnit")
        }
        assertEquals(
            "Failed to parse setting \"onlyUnit\", the value is \"days\", but it is missing a number",
            exception.message
        )
        exception = assertThrows {
            AutoKonfig.getPeriod("invalidUnit")
        }
        assertEquals(
            "Failed to parse setting \"invalidUnit\", the value is \"5 whiles\", but the unit \"whiles\" must be one of [\"\", \"d\", \"day\", \"days\", \"w\", \"week\", \"weeks\", \"m\", \"month\", \"months\", \"y\", \"year\", \"years\"]",
            exception.message
        )
        exception = assertThrows {
            AutoKonfig.getPeriod("invalidNumber")
        }
        assertEquals(
            "Failed to parse setting \"invalidNumber\", the value is \"5.5.5 days\", but \"5.5.5\" is not a number",
            exception.message
        )

        exception = assertThrows {
            AutoKonfig.getPeriod("unitFirst")
        }
        assertEquals(
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
        assertEquals(1, AutoKonfig.getBytes("a"))
        assertEquals(1, AutoKonfig.getBytes("b"))
        assertEquals(1, AutoKonfig.getBytes("c"))
        assertEquals(1, AutoKonfig.getBytes("d"))
        assertEquals(1, AutoKonfig.getBytes("e"))
        assertEquals(1_048_576, AutoKonfig.getBytes("f"))
        assertEquals(1_048_576, AutoKonfig.getBytes("g"))
        assertEquals(1_048_576, AutoKonfig.getBytes("h"))
        assertEquals(1_048_576, AutoKonfig.getBytes("i"))
        assertEquals(1_048_576, AutoKonfig.getBytes("j"))
        assertEquals(1_048_576, AutoKonfig.getBytes("k"))
        assertEquals(1_000_000, AutoKonfig.getBytes("l"))
        assertEquals(1_000_000, AutoKonfig.getBytes("m"))
        assertEquals(1_000_000, AutoKonfig.getBytes("n"))
    }

    object Collections : Group() {
        val strings by ListSetting(StringSettingType)
        val numbers by SetSetting(IntSettingType)
    }

    @Test
    fun `list settings can be read`() {
        """
            strings = [a,b,c]
            numbers = [1,2,3]
        """.trimIndent().useAsHocon()
        val strings by ListSetting(StringSettingType)
        val numbers by SetSetting(IntSettingType)
        assertEquals(listOf("a", "b", "c"), strings)
        assertEquals(setOf(1, 2, 3), numbers)
    }

    @Test
    fun `list settings can be read in a group`() {
        """
            collections.strings = [a,b,c]
            collections.numbers = [1,2,3,2,1]
        """.trimIndent().useAsHocon()
        assertEquals(listOf("a", "b", "c"), Collections.strings)
        assertEquals(setOf(1, 2, 3), Collections.numbers)
    }

    @Test
    fun `list settings can be read directly`() {
        """
            strings = [a,b,c]
            numbers = [1,2,3]
        """.trimIndent().useAsHocon()
        assertEquals(listOf("a", "b", "c"), AutoKonfig.getList(StringSettingType, "strings"))
        assertEquals(setOf(1, 2, 3), AutoKonfig.getSet(IntSettingType, "numbers"))
    }

    @Test
    fun `list settings with non-primitive types can be read`() {
        """
            foo = [ 1 kB, 2 kilobytes, 3B ]
        """.trimIndent().useAsHocon()
        val foo by ListSetting(BytesSettingType)
        assertEquals(listOf(1000L, 2000L, 3L), foo)
    }

    @Test
    fun `list settings with missing brackets throw exceptions`() {
        """
            nested = [1,2,3]
        """.trimIndent().useAsHocon()
        val exception: AutoKonfigException = assertThrows {
            val nested by ListSetting(ListSettingType(IntSettingType))
        }
        assertEquals(
            "Failed to parse setting \"nested\", the value is \"[1, 2, 3]\", but list element \"1\" is not a list",
            exception.message
        )
    }

    @Test
    fun `list setting with a malformed value throws an exception`() {
        """
            list = [1,2,c]
        """.trimIndent().useAsHocon()
        val exception: AutoKonfigException = assertThrows {
            val list by ListSetting(IntSettingType)
        }
        assertEquals(
            "Failed to parse setting \"list\", the value is \"[1, 2, c]\", but list element \"c\" must be an Int number",
            exception.message
        )
    }

    @Test
    fun `list setting with unexpected complex type throws an exception`() {
        """
            list = [{a: 1}]
        """.trimIndent().useAsHocon()
        val exception: AutoKonfigException = assertThrows {
            val list by ListSetting(IntSettingType)
        }
        assertEquals(
            "Failed to parse setting \"list\", the value is \"[{a=1}]\", but list element \"{a=1}\" is unexpectedly of type \"object\"",
            exception.message
        )
    }
}
