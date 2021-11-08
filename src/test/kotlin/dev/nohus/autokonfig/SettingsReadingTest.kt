package dev.nohus.autokonfig

import dev.nohus.autokonfig.types.BigDecimalSetting
import dev.nohus.autokonfig.types.BigIntegerSetting
import dev.nohus.autokonfig.types.BooleanSetting
import dev.nohus.autokonfig.types.IntSetting
import dev.nohus.autokonfig.types.StringSetting
import dev.nohus.autokonfig.utils.TestAutoKonfig
import dev.nohus.autokonfig.utils.useAsProperties
import io.kotest.core.spec.style.FreeSpec
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertThrows
import java.io.File

/**
 * Created by Marcin Wisniowski (Nohus) on 08/02/2020.
 */

class SettingsReadingTest : FreeSpec({

    val testAutoKonfig = TestAutoKonfig()
    listener(testAutoKonfig)

    "setting can be read" {
        "setting = test".useAsProperties(testAutoKonfig)
        val setting by DefaultAutoKonfig.StringSetting()
        assertEquals("test", setting)
    }

    "multiple settings can be read" {
        """
            foo = abc
            bar = def
            baz = ghi
        """.useAsProperties(testAutoKonfig)
        val foo by DefaultAutoKonfig.StringSetting()
        val baz by DefaultAutoKonfig.StringSetting()
        assertEquals("abc", foo)
        assertEquals("ghi", baz)
    }

    "multiple settings can be read from default config" {
        """
            foo = abc
            bar = def
            baz = ghi
        """.useAsProperties(testAutoKonfig)
        val foo by StringSetting()
        val baz by StringSetting()
        assertEquals("abc", foo)
        assertEquals("ghi", baz)
    }

    "keys are case-insensitive" {
        """
            FOO = abc
            bar = DEF
        """.useAsProperties(testAutoKonfig)
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

    "keys with different casing types are matched" {
        """
            foo-bar = 5
            TEST_DATA = 4
        """.useAsProperties(testAutoKonfig)
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

    "keys can have custom names" {
        """
            foo = abc
        """.useAsProperties(testAutoKonfig)
        val bar by StringSetting(name = "foo")
        assertEquals("abc", bar)
    }

    "multiple variables be delegated to the same setting" {
        """
            foo = abc
        """.useAsProperties(testAutoKonfig)
        val foo by StringSetting()
        val bar by StringSetting(name = "foo")
        assertEquals("abc", foo)
        assertEquals("abc", bar)
    }

    "setting can be read from system properties" {
        val key = "unit.test.system.property"
        val value = "value"
        System.setProperty(key, value)
        testAutoKonfig.resetDefaultAutoKonfig()
        val setting by StringSetting(name = key)
        assertEquals(value, setting)
    }

    "setting can be read from environment variables" {
        val key = "unit.test.environment.variable"
        val value = "value"
        testAutoKonfig.setEnvironmentVariable(key, value)
        testAutoKonfig.resetDefaultAutoKonfig()
        val setting by StringSetting(name = key)
        assertEquals(value, setting)
    }

    "setting can be read from config file in resources" {
        val config = AutoKonfig.withResourceConfig("resource.properties")
        val setting by config.StringSetting()
        assertEquals("resource", setting)
    }

    "setting can be read from config file by URL" {
        """
            setting = test
        """.useAsProperties(testAutoKonfig)
        AutoKonfig.withURLConfig(testAutoKonfig.propertiesFile.toURI().toURL())
        val setting by StringSetting()
        assertEquals("test", setting)
    }

    "setting can be read from config file by URL string" {
        """
            setting = test
        """.useAsProperties(testAutoKonfig)
        AutoKonfig.withURLConfig(testAutoKonfig.propertiesFile.toURI().toString())
        val setting by StringSetting()
        assertEquals("test", setting)
    }

    "setting can be read from command line arguments" {
        val config = AutoKonfig.withCommandLineArguments(arrayOf("-a", "b", "-c"))
        val a by config.StringSetting()
        val c by config.BooleanSetting()
        assertEquals("b", a)
        assertTrue(c)
    }

    "settings can be read from multiple files" {
        val config = AutoKonfig.withConfigs(
            File("src/test/resources/test/multiple/application.properties"),
            File("src/test/resources/test/multiple/autokonfig.conf")
        )
        val foo by config.StringSetting()
        val bar by config.StringSetting()
        assertEquals("abc", foo)
        assertEquals("def", bar)
    }

    "settings can be read from a list of files" {
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

    "setting can be read in a group" {
        """
            groupA.subgroup.setting = test
        """.useAsProperties(testAutoKonfig)
        assertEquals("test", groupA.subgroup.setting)
    }

    "setting can be read in a group with custom names" {
        """
            outer.inner.key = test
        """.useAsProperties(testAutoKonfig)
        assertEquals("test", groupB.subgroup.setting)
    }

    "setting can be read in a group with some custom names" {
        """
            outer.subgroup.setting = test
        """.useAsProperties(testAutoKonfig)
        assertEquals("test", groupC.subgroup.setting)
    }

    "getAll returns all settings" {
        """
            a = 1
            b = 2
        """.useAsProperties(testAutoKonfig)
        AutoKonfig.clear().withConfig(testAutoKonfig.propertiesFile)
        assertEquals(
            mapOf(
                "a" to "1",
                "b" to "2"
            ),
            AutoKonfig.getAll()
        )
    }

    "nonexistent setting throws an exception" {
        "".useAsProperties(testAutoKonfig)
        val exception = assertThrows<AutoKonfigException> {
            val nonexistent by StringSetting()
        }
        assertEquals("Required key \"nonexistent\" is missing", exception.message)
    }

    "nonexistent config file throws an exception" {
        val file = File("nonexistent")
        val exception = assertThrows<AutoKonfigException> {
            AutoKonfig.withConfig(file)
        }
        assertEquals(
            "Failed to read file: ${file.normalize().absolutePath}\nnonexistent: java.io.FileNotFoundException: nonexistent (No such file or directory)",
            exception.message
        )
    }

    "nonexistent resources config file throws an exception" {
        val exception = assertThrows<AutoKonfigException> {
            AutoKonfig.withResourceConfig("nonexistent")
        }
        assertEquals(
            "Failed to read resource: nonexistent\nnonexistent: java.io.IOException: resource not found on classpath: nonexistent",
            exception.message
        )
    }

    "malformed URL config file throws an exception" {
        val exception = assertThrows<AutoKonfigException> {
            AutoKonfig.withURLConfig("fake://URL")
        }
        assertEquals("Failed to read malformed URL: fake://URL (unknown protocol: fake)", exception.message)
    }

    "nonexistent URL config file throws an exception" {
        val exception = assertThrows<AutoKonfigException> {
            AutoKonfig.withURLConfig("file://nonexistent")
        }
        assertEquals(
            "Failed to read URL: file://nonexistent\n: java.io.FileNotFoundException:  (No such file or directory)",
            exception.message
        )
    }

    "wrong type of setting for value throws an exception" {
        """
            foo = test
        """.useAsProperties(testAutoKonfig)
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

    "invalid required include throws an exception" {
        assertThrows<AutoKonfigException> {
            AutoKonfig.withConfig(File("src/test/resources/test/include/invalid.conf"))
        }
    }
})
