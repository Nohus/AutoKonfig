package dev.nohus.autokonfig

import dev.nohus.autokonfig.types.*
import dev.nohus.autokonfig.utils.ConfigFileLocator
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
import java.time.Duration
import java.time.Period
import java.util.*
import kotlin.reflect.jvm.isAccessible

/**
 * Created by Marcin Wisniowski (Nohus) on 05/01/2020.
 */

class AutoKonfigTest {

    private val file = File("test.properties")
    private val hoconFile = File("test.conf")

    private fun String.useAsProperties() {
        file.writeText(this)
        resetDefaultAutoKonfig()
    }

    private fun String.useAsHocon() {
        hoconFile.writeText(this)
        resetDefaultAutoKonfig()
    }

    private fun resetDefaultAutoKonfig() {
        DefaultAutoKonfig
            .clear()
            .withSystemProperties()
            .withEnvironmentVariables()
            .withConfigs(ConfigFileLocator().getConfigFiles())
    }

    @AfterEach
    fun tearDown() {
        file.delete()
        hoconFile.delete()
    }

    @Test
    fun `setting can be read`() {
        "setting = test".useAsProperties()
        val setting by DefaultAutoKonfig.StringSetting()
        assertEquals("test", setting)
    }

    @Test
    fun `multiple settings can be read`() {
        """
            foo = abc
            bar = def
            baz = ghi
        """.trimIndent().useAsProperties()
        val foo by DefaultAutoKonfig.StringSetting()
        val baz by DefaultAutoKonfig.StringSetting()
        assertEquals("abc", foo)
        assertEquals("ghi", baz)
    }

    @Test
    fun `multiple settings can be read from default config`() {
        """
            foo = abc
            bar = def
            baz = ghi
        """.trimIndent().useAsProperties()
        val foo by StringSetting()
        val baz by StringSetting()
        assertEquals("abc", foo)
        assertEquals("ghi", baz)
    }

    @Test
    fun `keys are case-insensitive`() {
        """
            FOO = abc
            bar = DEF
        """.trimIndent().useAsProperties()
        val foo by StringSetting()
        val bar by StringSetting()
        val a by StringSetting(name = "foo")
        val b by StringSetting(name = "Bar")
        val c by StringSetting(name = "fOo")
        assertEquals("abc", foo)
        assertEquals("DEF", bar)
        assertEquals("abc", a)
        assertEquals("DEF", b)
        assertEquals("abc", c)
    }

    @Test
    fun `keys with different casing types are matched`() {
        """
            foo-bar = 5
            TEST_DATA = 4
        """.trimIndent().useAsProperties()
        val a by IntSetting(name = "foo-bar")
        val fooBar by IntSetting()
        val b by IntSetting(name = "TEST_DATA")
        val test_data by IntSetting()
        val testData by IntSetting()
        assertEquals(5, a)
        assertEquals(5, fooBar)
        assertEquals(4, b)
        assertEquals(4, test_data)
        assertEquals(4, testData)
    }

    @Test
    fun `keys can have custom names`() {
        """
            foo = abc
        """.trimIndent().useAsProperties()
        val bar by StringSetting(name = "foo")
        assertEquals("abc", bar)
    }

    @Test
    fun `multiple variables be delegated to the same setting`() {
        """
            foo = abc
        """.trimIndent().useAsProperties()
        val foo by StringSetting()
        val bar by StringSetting(name = "foo")
        assertEquals("abc", foo)
        assertEquals("abc", bar)
    }

    @Test
    fun `setting can be read from system properties`() {
        val key = "unit.test.system.property"
        val value = "value"
        System.setProperty(key, value)
        resetDefaultAutoKonfig()
        val setting by StringSetting(name = key)
        assertEquals(value, setting)
    }

    @Test
    fun `setting can be read from environment variables`() {
        val key = "unit.test.environment.variable"
        val value = "value"
        setEnvironmentVariable(key, value)
        resetDefaultAutoKonfig()
        val setting by StringSetting(name = key)
        assertEquals(value, setting)
    }

    private fun setEnvironmentVariable(key: String, value: String) {
        val environment = System.getenv()
        val field = environment::class.members.first { it.name == "m" }
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        (field.call(environment) as MutableMap<String, String>)[key] = value
    }

    @Test
    fun `setting can be read from config file in resources`() {
        val config = AutoKonfig().withResourceConfig("resource.properties")
        val setting by config.StringSetting()
        assertEquals("resource", setting)
    }

    @Test
    fun `setting can be read from config file by URL`() {
        """
            setting = test
        """.trimIndent().useAsProperties()
        AutoKonfig.clear().withURLConfig(file.toURI().toString())
        val setting by StringSetting()
        assertEquals("test", setting)
    }

    @Test
    fun `setting can be read from command line arguments`() {
        val config = AutoKonfig().withCommandLineArguments(arrayOf("-a", "b", "-c"))
        val a by config.StringSetting()
        val c by config.BooleanSetting()
        assertEquals("b", a)
        assertTrue(c)
    }

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

    @Test
    fun `settings of all types can be read`() {
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
    fun `settings of all types can be read in a group`() {
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
    fun `settings of all types can be read directly`() {
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

    @Test
    fun `nonexistent setting throws an exception`() {
        "".useAsProperties()
        val exception = assertThrows<AutoKonfigException> {
            val nonexistent by StringSetting()
        }
        assertEquals("Required key \"nonexistent\" is missing", exception.message)
    }

    @Test
    fun `nonexistent config file throws an exception`() {
        val file = File("nonexistent")
        val exception = assertThrows<AutoKonfigException> {
            AutoKonfig().withConfig(file)
        }
        assertEquals("Failed to read file: ${file.normalize().absolutePath} (nonexistent: java.io.FileNotFoundException: nonexistent (No such file or directory))", exception.message)
    }

    @Test
    fun `nonexistent resources config file throws an exception`() {
        val exception = assertThrows<AutoKonfigException> {
            AutoKonfig().withResourceConfig("nonexistent")
        }
        assertEquals("Failed to read resource: nonexistent (nonexistent: java.io.IOException: resource not found on classpath: nonexistent)", exception.message)
    }

    @Test
    fun `malformed URL config file throws an exception`() {
        val exception = assertThrows<AutoKonfigException> {
            AutoKonfig().withURLConfig("fake://URL")
        }
        assertEquals("Failed to read malformed URL: fake://URL (unknown protocol: fake)", exception.message)
    }

    @Test
    fun `nonexistent URL config file throws an exception`() {
        val exception = assertThrows<AutoKonfigException> {
            AutoKonfig().withURLConfig("file://nonexistent")
        }
        assertEquals("Failed to read URL: file://nonexistent (: java.io.FileNotFoundException:  (No such file or directory))", exception.message)
    }

    @Test
    fun `settings can be read from multiple files`() {
        val config = AutoKonfig().withConfigs(
            File("src/test/resources/test/multiple/5.properties"),
            File("src/test/resources/test/multiple/6.conf")
        )
        val foo by config.StringSetting()
        val bar by config.StringSetting()
        assertEquals("abc", foo)
        assertEquals("def", bar)
    }

    object groupA : Group() {
        object subgroup : Group() {
            val setting by StringSetting("")
        }
    }

    @Test
    fun `setting can be read in a group`() {
        """
            groupA.subgroup.setting = test
        """.trimIndent().useAsProperties()
        assertEquals("test", groupA.subgroup.setting)
    }

    object groupB : Group("outer") {
        object subgroup : Group("inner") {
            val setting by StringSetting(name = "key")
        }
    }

    @Test
    fun `setting can be read in a group with custom names`() {
        """
            outer.inner.key = test
        """.trimIndent().useAsProperties()
        assertEquals("test", groupB.subgroup.setting)
    }

    object groupC : Group("outer") {
        object subgroup : Group() {
            val setting by StringSetting()
        }
    }

    @Test
    fun `setting can be read in a group with some custom names`() {
        """
            outer.subgroup.setting = test
        """.trimIndent().useAsProperties()
        assertEquals("test", groupC.subgroup.setting)
    }

    @Test
    fun `wrong type of setting for value throws an exception`() {
        """
            foo = test
        """.trimIndent().useAsProperties()
        val exception = assertThrows<AutoKonfigException> {
            val a by IntSetting(name = "foo")
        }
        assertEquals("Failed to parse setting \"foo\", the value is \"test\", but must be an Int number", exception.message)
    }

    @Test
    fun `getAll returns all settings`() {
        """
            a = 1
            b = 2
        """.trimIndent().useAsProperties()
        AutoKonfig.clear().withConfig(file)
        assertEquals(mapOf(
            "a" to "1",
            "b" to "2"
        ), AutoKonfig.getAll())
    }

    @Test
    fun `setting can be traced to a file`() {
        """
            foo = 2
        """.trimIndent().useAsProperties()
        AutoKonfig.clear().withConfig(file)
        assertEquals("Key \"foo\" was read from config file at \"${file.normalize().absolutePath}\"", AutoKonfig.getKeySource("foo"))
    }

    @Test
    fun `setting with a fuzzy matched key can be traced to a file`() {
        """
            SERVER_PORT = 2
        """.trimIndent().useAsProperties()
        AutoKonfig.clear().withConfig(file)
        assertEquals("Key \"serverPort\" was read as \"SERVER_PORT\" from config file at \"${file.normalize().absolutePath}\"",
            AutoKonfig.getKeySource("serverPort"))
    }

    @Test
    fun `setting can be traced to a resource file`() {
        val config = AutoKonfig.clear().withResourceConfig("resource.properties")
        assertEquals("Key \"setting\" was read from config file resource at \"resource.properties\"", config.getKeySource("setting"))
    }

    @Test
    fun `setting can be traced to environment variables`() {
        val key = "unit.test.environment.variable"
        val value = "value"
        setEnvironmentVariable(key, value)
        resetDefaultAutoKonfig()
        assertEquals("Key \"unit.test.environment.variable\" was read from environment variables", AutoKonfig.getKeySource(key))
    }

    @Test
    fun `setting can be traced to system properties`() {
        val key = "unit.test.system.property"
        val value = "value"
        System.setProperty(key, value)
        resetDefaultAutoKonfig()
        assertEquals("Key \"unit.test.system.property\" was read from system properties", AutoKonfig.getKeySource(key))
    }

    @Test
    fun `setting can be traced to command line arguments`() {
        AutoKonfig.clear().withCommandLineArguments(arrayOf("-a", "b"))
        assertEquals("Key \"a\" was read from command line parameters", AutoKonfig.getKeySource("a"))
    }

    @Test
    fun `setting can be traced to manually inserted properties`() {
        AutoKonfig.clear().withProperties(Properties().apply { put("a", "b") })
        assertTrue(AutoKonfig.getKeySource("a").startsWith("Key \"a\" was read from properties inserted by org.junit"))
    }

    @Test
    fun `setting can be traced to manually inserted map`() {
        AutoKonfig.clear().withMap(mapOf("a" to "b"))
        assertTrue(AutoKonfig.getKeySource("a").startsWith("Key \"a\" was read from a map inserted by org.junit"))
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
        assertEquals("Failed to parse setting \"setting\", the value is \"Gamma\", but possible values are [Alpha, Beta]", exception.message)
        assertEquals("Failed to parse setting \"setting\", the value is \"Gamma\", but possible values are [Alpha, Beta]", exceptionJava.message)
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
        assertEquals("Failed to parse setting \"instant\", the value is \"invalid\", but must be an Instant", exception.message)
        exception = assertThrows {
            val duration by DurationSetting()
        }
        assertEquals("Failed to parse setting \"duration\", the value is \"invalid\", but it is missing a number", exception.message)
        exception = assertThrows {
            val localTime by LocalTimeSetting()
        }
        assertEquals("Failed to parse setting \"localTime\", the value is \"invalid\", but must be a LocalTime", exception.message)
        exception = assertThrows {
            val localDate by LocalDateSetting()
        }
        assertEquals("Failed to parse setting \"localDate\", the value is \"invalid\", but must be a LocalDate", exception.message)
        exception = assertThrows {
            val localDateTime by LocalDateTimeSetting()
        }
        assertEquals("Failed to parse setting \"localDateTime\", the value is \"invalid\", but must be a LocalDateTime", exception.message)
    }

    @Test
    fun `natural duration settings are parsed correctly`() {
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
    fun `invalid natural duration values throw exceptions`() {
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
        assertEquals("Failed to parse setting \"missing\", the value is \"\", but it is missing a number", exception.message)
        exception = assertThrows {
            AutoKonfig.getDuration("onlyUnit")
        }
        assertEquals("Failed to parse setting \"onlyUnit\", the value is \"days\", but it is missing a number", exception.message)
        exception = assertThrows {
            AutoKonfig.getDuration("invalidUnit")
        }
        assertEquals("Failed to parse setting \"invalidUnit\", the value is \"5 whiles\", but the unit \"whiles\" must be one of [\"\", \"ms\", \"millis\", \"milliseconds\", \"us\", \"micros\", \"microseconds\", \"ns\", \"nanos\", \"nanoseconds\", \"s\", \"second\", \"seconds\", \"m\", \"minute\", \"minutes\", \"h\", \"hour\", \"hours\", \"d\", \"day\", \"days\"]", exception.message)
        exception = assertThrows {
            AutoKonfig.getDuration("invalidNumber")
        }
        assertEquals("Failed to parse setting \"invalidNumber\", the value is \"5.5.5 seconds\", but \"5.5.5\" is not a number", exception.message)

        exception = assertThrows {
            AutoKonfig.getDuration("unitFirst")
        }
        assertEquals("Failed to parse setting \"unitFirst\", the value is \"seconds 10\", but it is missing a number", exception.message)
    }

    @Test
    fun `natural period settings are parsed correctly`() {
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
    fun `invalid natural period values throw exceptions`() {
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
        assertEquals("Failed to parse setting \"missing\", the value is \"\", but it is missing a number", exception.message)
        exception = assertThrows {
            AutoKonfig.getPeriod("onlyUnit")
        }
        assertEquals("Failed to parse setting \"onlyUnit\", the value is \"days\", but it is missing a number", exception.message)
        exception = assertThrows {
            AutoKonfig.getPeriod("invalidUnit")
        }
        assertEquals("Failed to parse setting \"invalidUnit\", the value is \"5 whiles\", but the unit \"whiles\" must be one of [\"\", \"d\", \"day\", \"days\", \"w\", \"week\", \"weeks\", \"m\", \"month\", \"months\", \"y\", \"year\", \"years\"]", exception.message)
        exception = assertThrows {
            AutoKonfig.getPeriod("invalidNumber")
        }
        assertEquals("Failed to parse setting \"invalidNumber\", the value is \"5.5.5 days\", but \"5.5.5\" is not a number", exception.message)

        exception = assertThrows {
            AutoKonfig.getPeriod("unitFirst")
        }
        assertEquals("Failed to parse setting \"unitFirst\", the value is \"days 10\", but it is missing a number", exception.message)
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
        val strings2 by ListSetting(StringSettingType, ",", name = "strings")
        val strings3 by ListSetting(StringSettingType, Regex(","), name = "strings")
        val numbers by SetSetting(IntSettingType)
        val numbers2 by SetSetting(IntSettingType, ",", name = "numbers")
        val numbers3 by SetSetting(IntSettingType, Regex(","), name = "numbers")
    }

    @Test
    fun `list settings can be read in a group`() {
        """
            collections.strings = a,b,c
            collections.numbers = 1,2,3,2,1
        """.trimIndent().useAsProperties()
        assertEquals(listOf("a", "b", "c"), Collections.strings)
        assertEquals(listOf("a", "b", "c"), Collections.strings2)
        assertEquals(listOf("a", "b", "c"), Collections.strings3)
        assertEquals(setOf(1, 2, 3), Collections.numbers)
        assertEquals(setOf(1, 2, 3), Collections.numbers2)
        assertEquals(setOf(1, 2, 3), Collections.numbers3)
    }

    @Test
    fun `list settings can be read`() {
        """
            strings = a,b,c
            numbers = 1,2,3
        """.trimIndent().useAsProperties()
        val strings by ListSetting(StringSettingType)
        val numbers by SetSetting(IntSettingType)
        assertEquals(listOf("a", "b", "c"), strings)
        assertEquals(setOf(1, 2, 3), numbers)
    }

    @Test
    fun `list settings can be read directly`() {
        """
            strings = a,b,c
            numbers = 1,2,3
        """.trimIndent().useAsProperties()
        assertEquals(listOf("a", "b", "c"), AutoKonfig.getList(StringSettingType, "strings"))
        assertEquals(listOf("a", "b", "c"), AutoKonfig.getList(StringSettingType, ",", "strings"))
        assertEquals(listOf("a", "b", "c"), AutoKonfig.getList(StringSettingType, Regex(","), "strings"))
        assertEquals(setOf(1, 2, 3), AutoKonfig.getSet(IntSettingType, "numbers"))
        assertEquals(setOf(1, 2, 3), AutoKonfig.getSet(IntSettingType, ",", "numbers"))
        assertEquals(setOf(1, 2, 3), AutoKonfig.getSet(IntSettingType, Regex(","),"numbers"))
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
        assertArrayEquals(listOf(1, 2, 3).toTypedArray(), commas.toTypedArray())
        assertArrayEquals(listOf(1, 2, 3).toTypedArray(), commasAndWhitespace.toTypedArray())
        assertArrayEquals(setOf(1, 2, 3).toTypedArray(), dots.toTypedArray())
        assertArrayEquals(setOf(1, 2, 3).toTypedArray(), complex.toTypedArray())
    }

    @Test
    fun `parses json`() {
        """
            {
                "foo" : {
                    "bar" : 10,
                    "baz" : 12
                }
            }
        """.trimIndent().useAsHocon()
        assertEquals(12, AutoKonfig.getInt("foo.baz"))
    }

    @Test
    fun `parses hocon`() {
        """
            foo {
                bar = 10
                baz = 12
            }
        """.trimIndent().useAsHocon()
        assertEquals(12, AutoKonfig.getInt("foo.baz"))
    }

    @Test
    fun `parses hocon single line`() {
        """
            foo.bar=10, foo.baz=12
        """.trimIndent().useAsHocon()
        assertEquals(12, AutoKonfig.getInt("foo.baz"))
    }

    @Test
    fun `parses hocon with substitutions`() {
        """
            foo = 15
            bar = ${'$'}{foo}
        """.trimIndent().useAsHocon()
        assertEquals(15, AutoKonfig.getInt("bar"))
    }

    @Test
    fun `parses hocon with unquoted strings`() {
        """
            foo : 20 10 10
        """.trimIndent().useAsHocon()
        assertEquals(setOf(20, 10), AutoKonfig.getSet(IntSettingType, " ", "foo"))
    }

    @Test
    fun `parses hocon with arrays`() {
        """
            foo = [1,2,3]
        """.trimIndent().useAsHocon()
        assertEquals(listOf(1, 2, 3), AutoKonfig.getList(IntSettingType, "foo"))
    }
}
