package dev.nohus.autokonfig

import dev.nohus.autokonfig.types.BigDecimalSetting
import dev.nohus.autokonfig.types.BigIntegerSetting
import dev.nohus.autokonfig.types.BooleanSetting
import dev.nohus.autokonfig.types.BytesSetting
import dev.nohus.autokonfig.types.BytesSettingType
import dev.nohus.autokonfig.types.DoubleSetting
import dev.nohus.autokonfig.types.DurationSetting
import dev.nohus.autokonfig.types.EnumSetting
import dev.nohus.autokonfig.types.FlagSetting
import dev.nohus.autokonfig.types.FloatSetting
import dev.nohus.autokonfig.types.InstantSetting
import dev.nohus.autokonfig.types.IntSetting
import dev.nohus.autokonfig.types.IntSettingType
import dev.nohus.autokonfig.types.ListSetting
import dev.nohus.autokonfig.types.ListSettingType
import dev.nohus.autokonfig.types.LocalDateSetting
import dev.nohus.autokonfig.types.LocalDateTimeSetting
import dev.nohus.autokonfig.types.LocalTimeSetting
import dev.nohus.autokonfig.types.LongSetting
import dev.nohus.autokonfig.types.PeriodSetting
import dev.nohus.autokonfig.types.SetSetting
import dev.nohus.autokonfig.types.StringSetting
import dev.nohus.autokonfig.types.StringSettingType
import dev.nohus.autokonfig.types.getBigDecimal
import dev.nohus.autokonfig.types.getBigInteger
import dev.nohus.autokonfig.types.getBoolean
import dev.nohus.autokonfig.types.getBytes
import dev.nohus.autokonfig.types.getDouble
import dev.nohus.autokonfig.types.getDuration
import dev.nohus.autokonfig.types.getEnum
import dev.nohus.autokonfig.types.getFlag
import dev.nohus.autokonfig.types.getFloat
import dev.nohus.autokonfig.types.getInstant
import dev.nohus.autokonfig.types.getInt
import dev.nohus.autokonfig.types.getList
import dev.nohus.autokonfig.types.getLocalDate
import dev.nohus.autokonfig.types.getLocalDateTime
import dev.nohus.autokonfig.types.getLocalTime
import dev.nohus.autokonfig.types.getLong
import dev.nohus.autokonfig.types.getPeriod
import dev.nohus.autokonfig.types.getSet
import dev.nohus.autokonfig.types.getString
import dev.nohus.autokonfig.utils.TestAutoKonfig
import dev.nohus.autokonfig.utils.useAsHocon
import dev.nohus.autokonfig.utils.useAsProperties
import io.kotest.assertions.throwables.shouldThrowExactlyUnit
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import java.time.Duration
import java.time.LocalTime
import java.time.Period

/**
 * Created by Marcin Wisniowski (Nohus) on 08/02/2020.
 */

class SettingsTypesTest : FreeSpec({

    val testAutoKonfig = TestAutoKonfig()
    listener(testAutoKonfig)

    "boolean settings can be read" {
        """
            a = true
            b = Yes
            c = on
            d = 1
            e = FALSE
            f = no
            g = oFf
            h = 0
        """.useAsProperties(testAutoKonfig)
        val a by BooleanSetting()
        val b by BooleanSetting()
        val c by BooleanSetting()
        val d by BooleanSetting()
        val e by BooleanSetting()
        val f by BooleanSetting()
        val g by BooleanSetting()
        val h by BooleanSetting()
        a.shouldBeTrue()
        b.shouldBeTrue()
        c.shouldBeTrue()
        d.shouldBeTrue()
        e.shouldBeFalse()
        f.shouldBeFalse()
        g.shouldBeFalse()
        h.shouldBeFalse()
    }

    "flag settings are false by default" {
        """
            a = true
        """.useAsProperties(testAutoKonfig)
        val a by FlagSetting()
        val b by FlagSetting()
        a.shouldBeTrue()
        b.shouldBeFalse()
    }

    "settings of all basic types can be read" {
        """
            string = hello
            int = 10
            long = 3000000000
            float = 3.14
            double = 3.1415
            bigInteger = 100000000000000000005
            bigDecimal = 100000000000000000005.00000002
            bytes = 512kB
            boolean = false
            flag = true
        """.useAsProperties(testAutoKonfig)
        val string by StringSetting()
        val int by IntSetting()
        val long by LongSetting()
        val float by FloatSetting()
        val double by DoubleSetting()
        val bigInteger by BigIntegerSetting()
        val bigDecimal by BigDecimalSetting()
        val bytes by BytesSetting()
        val boolean by BooleanSetting()
        val flag by FlagSetting()
        string shouldBe "hello"
        int shouldBe 10
        long shouldBe 3000000000
        float shouldBe 3.14f
        double shouldBe 3.1415
        bigInteger shouldBe "100000000000000000005".toBigInteger()
        bigDecimal shouldBe "100000000000000000005.00000002".toBigDecimal()
        bytes shouldBe 512000
        boolean.shouldBeFalse()
        flag.shouldBeTrue()
    }

    "settings of all basic types can be read in a group" {
        """
            typesGroup.string = hello
            typesGroup.int = 10
            typesGroup.long = 3000000000
            typesGroup.float = 3.14
            typesGroup.double = 3.1415
            typesGroup.bigInteger = 100000000000000000005
            typesGroup.bigDecimal = 100000000000000000005.00000002
            typesGroup.bytes = 512kB
            typesGroup.boolean = false
            typesGroup.flag = true
        """.useAsProperties(testAutoKonfig)
        TypesGroup.string shouldBe "hello"
        TypesGroup.int shouldBe 10
        TypesGroup.long shouldBe 3000000000
        TypesGroup.float shouldBe 3.14f
        TypesGroup.double shouldBe 3.1415
        TypesGroup.bigInteger shouldBe "100000000000000000005".toBigInteger()
        TypesGroup.bigDecimal shouldBe "100000000000000000005.00000002".toBigDecimal()
        TypesGroup.bytes shouldBe 512000
        TypesGroup.boolean.shouldBeFalse()
        TypesGroup.flag.shouldBeTrue()
    }

    "settings of all basic types can be read directly" {
        """
            string = hello
            int = 10
            long = 3000000000
            float = 3.14
            double = 3.1415
            bigInteger = 100000000000000000005
            bigDecimal = 100000000000000000005.00000002
            bytes = 512kB
            boolean = false
            flag = true
        """.useAsProperties(testAutoKonfig)
        AutoKonfig.getString("string") shouldBe "hello"
        AutoKonfig.getInt("int") shouldBe 10
        AutoKonfig.getLong("long") shouldBe 3000000000
        AutoKonfig.getFloat("float") shouldBe 3.14f
        AutoKonfig.getDouble("double") shouldBe 3.1415
        AutoKonfig.getBigInteger("bigInteger") shouldBe "100000000000000000005".toBigInteger()
        AutoKonfig.getBigDecimal("bigDecimal") shouldBe "100000000000000000005.00000002".toBigDecimal()
        AutoKonfig.getBytes("bytes") shouldBe 512000
        AutoKonfig.getBoolean("boolean").shouldBeFalse()
        AutoKonfig.getFlag("flag").shouldBeTrue()
    }

    "enum settings can be read" {
        """
            EnumGroup.setting = Alpha
        """.useAsProperties(testAutoKonfig)
        EnumGroup.setting shouldBe Letters.Alpha
        EnumGroup.settingJava shouldBe Letters.Alpha
    }

    "enum settings can be read directly" {
        """
            setting = Alpha
        """.useAsProperties(testAutoKonfig)
        AutoKonfig.getEnum(Letters::class, "setting") shouldBe Letters.Alpha
        AutoKonfig.getEnum(Letters::class.java, "setting") shouldBe Letters.Alpha
    }

    "enum settings are case-insensitive" {
        """
            EnumGroup.setting = beTA
        """.useAsProperties(testAutoKonfig)
        EnumGroup.setting shouldBe Letters.Beta
        EnumGroup.settingJava shouldBe Letters.Beta
    }

    "invalid enum value throws an exception" {
        """
            setting = Gamma
        """.useAsProperties(testAutoKonfig)
        val exception = shouldThrowExactlyUnit<AutoKonfigException> {
            val setting by EnumSetting(Letters::class)
        }
        val exceptionJava = shouldThrowExactlyUnit<AutoKonfigException> {
            val settingJava by EnumSetting(Letters::class.java, name = "setting")
        }
        exception shouldHaveMessage "Failed to parse setting \"setting\", the value is \"Gamma\", but possible values are [Alpha, Beta]"
        exceptionJava shouldHaveMessage "Failed to parse setting \"setting\", the value is \"Gamma\", but possible values are [Alpha, Beta]"
    }

    "temporal settings can be read" {
        """
            instant = 2011-12-03T10:15:30Z
            duration = 10s
            period = 10d
            local-time = 10:15:30
            local-date = 2020-01-09
            local-date-time = 2020-01-09T10:15:30
        """.useAsProperties(testAutoKonfig)
        val instant by InstantSetting()
        val duration by DurationSetting()
        val period by PeriodSetting()
        val localTime by LocalTimeSetting()
        val localDate by LocalDateSetting()
        val localDateTime by LocalDateTimeSetting()
        instant.toString() shouldBe "2011-12-03T10:15:30Z"
        duration shouldBe Duration.ofSeconds(10)
        period shouldBe Period.ofDays(10)
        localTime.toString() shouldBe "10:15:30"
        localDate.toString() shouldBe "2020-01-09"
        localDateTime.toString() shouldBe "2020-01-09T10:15:30"
    }

    "temporal settings can be read in a group" {
        """
            temporal.instant = 2011-12-03T10:15:30Z
            temporal.duration = 10s
            temporal.period = 10d
            temporal.local-time = 10:15:30
            temporal.local-date = 2020-01-09
            temporal.local-date-time = 2020-01-09T10:15:30
        """.useAsProperties(testAutoKonfig)
        Temporal.instant.toString() shouldBe "2011-12-03T10:15:30Z"
        Temporal.duration shouldBe Duration.ofSeconds(10)
        Temporal.period shouldBe Period.ofDays(10)
        Temporal.localTime.toString() shouldBe "10:15:30"
        Temporal.localDate.toString() shouldBe "2020-01-09"
        Temporal.localDateTime.toString() shouldBe "2020-01-09T10:15:30"
    }

    "temporal settings can be read directly" {
        """
            instant = 2011-12-03T10:15:30Z
            duration = 10s
            local-time = 10:15:30
            local-date = 2020-01-09
            local-date-time = 2020-01-09T10:15:30
        """.useAsProperties(testAutoKonfig)
        AutoKonfig.getInstant("instant").toString() shouldBe "2011-12-03T10:15:30Z"
        AutoKonfig.getDuration("duration") shouldBe Duration.ofSeconds(10)
        AutoKonfig.getLocalTime("local-time").toString() shouldBe "10:15:30"
        AutoKonfig.getLocalDate("local-date").toString() shouldBe "2020-01-09"
        AutoKonfig.getLocalDateTime("local-date-time").toString() shouldBe "2020-01-09T10:15:30"
    }

    "invalid temporal values throw exceptions" {
        """
            instant = invalid
            duration = invalid
            local-time = invalid
            local-date = invalid
            local-date-time = invalid
        """.useAsProperties(testAutoKonfig)
        var exception = shouldThrowExactlyUnit<AutoKonfigException> {
            val instant by InstantSetting()
        }
        exception shouldHaveMessage "Failed to parse setting \"instant\", the value is \"invalid\", but must be an Instant"
        exception = shouldThrowExactlyUnit {
            val duration by DurationSetting()
        }
        exception shouldHaveMessage "Failed to parse setting \"duration\", the value is \"invalid\", but it is missing a number"
        exception = shouldThrowExactlyUnit {
            val localTime by LocalTimeSetting()
        }
        exception shouldHaveMessage "Failed to parse setting \"localTime\", the value is \"invalid\", but must be a LocalTime"
        exception = shouldThrowExactlyUnit {
            val localDate by LocalDateSetting()
        }
        exception shouldHaveMessage "Failed to parse setting \"localDate\", the value is \"invalid\", but must be a LocalDate"
        exception = shouldThrowExactlyUnit {
            val localDateTime by LocalDateTimeSetting()
        }
        exception shouldHaveMessage "Failed to parse setting \"localDateTime\", the value is \"invalid\", but must be a LocalDateTime"
    }

    "LocalTime settings can be read" {
        """
            a = "05:00"
            b = "22:30:00"
            c = "07:20:40.5"
            d = "08:10:20.000000001"
        """.useAsHocon(testAutoKonfig)
        AutoKonfig.getLocalTime("a") shouldBe LocalTime.of(5, 0)
        AutoKonfig.getLocalTime("b") shouldBe LocalTime.of(22, 30)
        AutoKonfig.getLocalTime("c") shouldBe LocalTime.of(7, 20, 40, 500000000)
        AutoKonfig.getLocalTime("d") shouldBe LocalTime.of(8, 10, 20, 1)
    }

    "natural Duration settings can be read" {
        """
            plain = 10
            unit = 20s
            space = 25 s
            whitespace = 30    s
            nanos = 50ns
            long = 100000 days
            fraction = 0.5 day
        """.useAsHocon(testAutoKonfig)
        AutoKonfig.getDuration("plain") shouldBe Duration.ofMillis(10)
        AutoKonfig.getDuration("unit") shouldBe Duration.ofSeconds(20)
        AutoKonfig.getDuration("space") shouldBe Duration.ofSeconds(25)
        AutoKonfig.getDuration("whitespace") shouldBe Duration.ofSeconds(30)
        AutoKonfig.getDuration("nanos") shouldBe Duration.ofNanos(50)
        AutoKonfig.getDuration("long") shouldBe Duration.ofDays(100000)
        AutoKonfig.getDuration("fraction") shouldBe Duration.ofHours(12)
    }

    "invalid natural Duration values throw exceptions" {
        """
            missing = 
            onlyUnit = days
            invalidUnit = 5 whiles
            invalidNumber = 5.5.5 seconds
            unitFirst = seconds 10
        """.useAsProperties(testAutoKonfig)
        var exception = shouldThrowExactlyUnit<AutoKonfigException> {
            AutoKonfig.getDuration("missing")
        }
        exception shouldHaveMessage "Failed to parse setting \"missing\", the value is \"\", but it is missing a number"
        exception = shouldThrowExactlyUnit {
            AutoKonfig.getDuration("onlyUnit")
        }
        exception shouldHaveMessage "Failed to parse setting \"onlyUnit\", the value is \"days\", but it is missing a number"
        exception = shouldThrowExactlyUnit {
            AutoKonfig.getDuration("invalidUnit")
        }
        exception shouldHaveMessage "Failed to parse setting \"invalidUnit\", the value is \"5 whiles\", but the unit \"whiles\" must be one of [\"\", \"ms\", \"millis\", \"milliseconds\", \"us\", \"micros\", \"microseconds\", \"ns\", \"nanos\", \"nanoseconds\", \"s\", \"second\", \"seconds\", \"m\", \"minute\", \"minutes\", \"h\", \"hour\", \"hours\", \"d\", \"day\", \"days\"]"
        exception = shouldThrowExactlyUnit {
            AutoKonfig.getDuration("invalidNumber")
        }
        exception shouldHaveMessage "Failed to parse setting \"invalidNumber\", the value is \"5.5.5 seconds\", but \"5.5.5\" is not a number"
        exception = shouldThrowExactlyUnit {
            AutoKonfig.getDuration("unitFirst")
        }
        exception shouldHaveMessage "Failed to parse setting \"unitFirst\", the value is \"seconds 10\", but it is missing a number"
    }

    "natural Period settings are parsed correctly" {
        """
            plain = 10
            unit = 20m
            space = 25 w
            whitespace = 30    y
            days = 50day
            long = 100000 weeks
            fraction = 0.5 month
        """.useAsHocon(testAutoKonfig)
        AutoKonfig.getPeriod("plain") shouldBe Period.ofDays(10)
        AutoKonfig.getPeriod("unit") shouldBe Period.ofDays(600)
        AutoKonfig.getPeriod("space") shouldBe Period.ofWeeks(25)
        AutoKonfig.getPeriod("whitespace") shouldBe Period.ofDays(10950)
        AutoKonfig.getPeriod("days") shouldBe Period.ofDays(50)
        AutoKonfig.getPeriod("long") shouldBe Period.ofWeeks(100000)
        AutoKonfig.getPeriod("fraction") shouldBe Period.ofDays(15)
    }

    "invalid natural Period values throw exceptions" {
        """
            missing = 
            onlyUnit = days
            invalidUnit = 5 whiles
            invalidNumber = 5.5.5 days
            unitFirst = days 10
        """.useAsProperties(testAutoKonfig)
        var exception = shouldThrowExactlyUnit<AutoKonfigException> {
            AutoKonfig.getPeriod("missing")
        }
        exception shouldHaveMessage "Failed to parse setting \"missing\", the value is \"\", but it is missing a number"
        exception = shouldThrowExactlyUnit {
            AutoKonfig.getPeriod("onlyUnit")
        }
        exception shouldHaveMessage "Failed to parse setting \"onlyUnit\", the value is \"days\", but it is missing a number"
        exception = shouldThrowExactlyUnit {
            AutoKonfig.getPeriod("invalidUnit")
        }
        exception shouldHaveMessage "Failed to parse setting \"invalidUnit\", the value is \"5 whiles\", but the unit \"whiles\" must be one of [\"\", \"d\", \"day\", \"days\", \"w\", \"week\", \"weeks\", \"m\", \"month\", \"months\", \"y\", \"year\", \"years\"]"
        exception = shouldThrowExactlyUnit {
            AutoKonfig.getPeriod("invalidNumber")
        }
        exception shouldHaveMessage "Failed to parse setting \"invalidNumber\", the value is \"5.5.5 days\", but \"5.5.5\" is not a number"
        exception = shouldThrowExactlyUnit {
            AutoKonfig.getPeriod("unitFirst")
        }
        exception shouldHaveMessage "Failed to parse setting \"unitFirst\", the value is \"days 10\", but it is missing a number"
    }

    "bytes settings are parsed correctly" {
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
        """.useAsHocon(testAutoKonfig)
        AutoKonfig.getBytes("a") shouldBe 1
        AutoKonfig.getBytes("b") shouldBe 1
        AutoKonfig.getBytes("c") shouldBe 1
        AutoKonfig.getBytes("d") shouldBe 1
        AutoKonfig.getBytes("e") shouldBe 1
        AutoKonfig.getBytes("f") shouldBe 1_048_576
        AutoKonfig.getBytes("g") shouldBe 1_048_576
        AutoKonfig.getBytes("h") shouldBe 1_048_576
        AutoKonfig.getBytes("i") shouldBe 1_048_576
        AutoKonfig.getBytes("j") shouldBe 1_048_576
        AutoKonfig.getBytes("k") shouldBe 1_048_576
        AutoKonfig.getBytes("l") shouldBe 1_000_000
        AutoKonfig.getBytes("m") shouldBe 1_000_000
        AutoKonfig.getBytes("n") shouldBe 1_000_000
    }

    "list settings can be read" {
        """
            strings = [a,b,c]
            numbers = [1,2,3]
        """.useAsHocon(testAutoKonfig)
        val strings by ListSetting(StringSettingType)
        val numbers by SetSetting(IntSettingType)
        strings shouldContainExactly listOf("a", "b", "c")
        numbers shouldContainExactly setOf(1, 2, 3)
    }

    "list settings can be read in a group" {
        """
            collections.strings = [a,b,c]
            collections.numbers = [1,2,3,2,1]
        """.useAsHocon(testAutoKonfig)
        Collections.strings shouldContainExactly listOf("a", "b", "c")
        Collections.numbers shouldContainExactly setOf(1, 2, 3)
    }

    "list settings can be read directly" {
        """
            strings = [a,b,c]
            numbers = [1,2,3]
        """.useAsHocon(testAutoKonfig)
        AutoKonfig.getList(StringSettingType, "strings") shouldContainExactly listOf("a", "b", "c")
        AutoKonfig.getSet(IntSettingType, "numbers") shouldContainExactly setOf(1, 2, 3)
    }

    "list settings with non-primitive types can be read" {
        """
            foo = [ 1 kB, 2 kilobytes, 3B ]
        """.useAsHocon(testAutoKonfig)
        val foo by ListSetting(BytesSettingType)
        foo shouldContainExactly listOf(1000L, 2000L, 3L)
    }

    "list settings with missing brackets throw exceptions" {
        """
            nested = [1,2,3]
        """.useAsHocon(testAutoKonfig)
        val exception = shouldThrowExactlyUnit<AutoKonfigException> {
            val nested by ListSetting(ListSettingType(IntSettingType))
        }
        exception shouldHaveMessage "Failed to parse setting \"nested\", the value is \"[1, 2, 3]\", but list element \"1\" is not a list"
    }

    "list setting with a malformed value throws an exception" {
        """
            list = [1,2,c]
        """.useAsHocon(testAutoKonfig)
        val exception = shouldThrowExactlyUnit<AutoKonfigException> {
            val list by ListSetting(IntSettingType)
        }
        exception shouldHaveMessage "Failed to parse setting \"list\", the value is \"[1, 2, c]\", but list element \"c\" must be an Int number"
    }

    "list setting with unexpected complex type throws an exception" {
        """
            list = [{a: 1}]
        """.useAsHocon(testAutoKonfig)
        val exception = shouldThrowExactlyUnit<AutoKonfigException> {
            val list by ListSetting(IntSettingType)
        }
        exception shouldHaveMessage "Failed to parse setting \"list\", the value is \"[{a=1}]\", but list element \"{a=1}\" is unexpectedly of type \"object\""
    }
})
