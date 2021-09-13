package dev.nohus.autokonfig

import dev.nohus.autokonfig.types.BigDecimalSetting
import dev.nohus.autokonfig.types.BigIntegerSetting
import dev.nohus.autokonfig.types.BooleanSetting
import dev.nohus.autokonfig.types.Group
import dev.nohus.autokonfig.types.IntSetting
import dev.nohus.autokonfig.types.StringSetting
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File

/**
 * Created by Marcin Wisniowski (Nohus) on 08/02/2020.
 */

class SettingsReadingTest : BaseAutoKonfigTest() {

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

    @Test
    fun `setting can be read from config file in resources`() {
        val config = AutoKonfig.withResourceConfig("resource.properties")
        val setting by config.StringSetting()
        assertEquals("resource", setting)
    }

    @Test
    fun `setting can be read from config file by URL`() {
        """
            setting = test
        """.trimIndent().useAsProperties()
        AutoKonfig.withURLConfig(file.toURI().toURL())
        val setting by StringSetting()
        assertEquals("test", setting)
    }

    @Test
    fun `setting can be read from config file by URL string`() {
        """
            setting = test
        """.trimIndent().useAsProperties()
        AutoKonfig.withURLConfig(file.toURI().toString())
        val setting by StringSetting()
        assertEquals("test", setting)
    }

    @Test
    fun `setting can be read from command line arguments`() {
        val config = AutoKonfig.withCommandLineArguments(arrayOf("-a", "b", "-c"))
        val a by config.StringSetting()
        val c by config.BooleanSetting()
        assertEquals("b", a)
        assertTrue(c)
    }

    @Test
    fun `settings can be read from multiple files`() {
        val config = AutoKonfig.withConfigs(
            File("src/test/resources/test/multiple/application.properties"),
            File("src/test/resources/test/multiple/autokonfig.conf")
        )
        val foo by config.StringSetting()
        val bar by config.StringSetting()
        assertEquals("abc", foo)
        assertEquals("def", bar)
    }

    @Test
    fun `settings can be read from a list of files`() {
        val config = AutoKonfig.withConfigs(
            listOf(
                File("src/test/resources/test/multiple/application.properties"),
                File("src/test/resources/test/multiple/autokonfig.conf")
            )
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
    fun `getAll returns all settings`() {
        """
            a = 1
            b = 2
        """.trimIndent().useAsProperties()
        AutoKonfig.clear().withConfig(file)
        assertEquals(
            mapOf(
                "a" to "1",
                "b" to "2"
            ),
            AutoKonfig.getAll()
        )
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
            AutoKonfig.withConfig(file)
        }
        assertEquals(
            "Failed to read file: ${file.normalize().absolutePath}\nnonexistent: java.io.FileNotFoundException: nonexistent (No such file or directory)",
            exception.message
        )
    }

    @Test
    fun `nonexistent resources config file throws an exception`() {
        val exception = assertThrows<AutoKonfigException> {
            AutoKonfig.withResourceConfig("nonexistent")
        }
        assertEquals(
            "Failed to read resource: nonexistent\nnonexistent: java.io.IOException: resource not found on classpath: nonexistent",
            exception.message
        )
    }

    @Test
    fun `malformed URL config file throws an exception`() {
        val exception = assertThrows<AutoKonfigException> {
            AutoKonfig.withURLConfig("fake://URL")
        }
        assertEquals("Failed to read malformed URL: fake://URL (unknown protocol: fake)", exception.message)
    }

    @Test
    fun `nonexistent URL config file throws an exception`() {
        val exception = assertThrows<AutoKonfigException> {
            AutoKonfig.withURLConfig("file://nonexistent")
        }
        assertEquals(
            "Failed to read URL: file://nonexistent\n: java.io.FileNotFoundException:  (No such file or directory)",
            exception.message
        )
    }

    @Test
    fun `wrong type of setting for value throws an exception`() {
        """
            foo = test
        """.trimIndent().useAsProperties()
        var exception: AutoKonfigException = assertThrows {
            val a by IntSetting(name = "foo")
        }
        assertEquals(
            "Failed to parse setting \"foo\", the value is \"test\", but must be an Int number",
            exception.message
        )
        exception = assertThrows {
            val b by BigIntegerSetting(name = "foo")
        }
        assertEquals(
            "Failed to parse setting \"foo\", the value is \"test\", but must be a BigInteger number",
            exception.message
        )
        exception = assertThrows {
            val c by BigDecimalSetting(name = "foo")
        }
        assertEquals(
            "Failed to parse setting \"foo\", the value is \"test\", but must be a BigDecimal number",
            exception.message
        )
    }

    @Test
    fun `invalid required include throws an exception`() {
        assertThrows<AutoKonfigException> {
            AutoKonfig.withConfig(File("src/test/resources/test/include/invalid.conf"))
        }
    }
}
