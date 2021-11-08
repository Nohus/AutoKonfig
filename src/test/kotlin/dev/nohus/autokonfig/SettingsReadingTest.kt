package dev.nohus.autokonfig

import dev.nohus.autokonfig.types.BigDecimalSetting
import dev.nohus.autokonfig.types.BigIntegerSetting
import dev.nohus.autokonfig.types.BooleanSetting
import dev.nohus.autokonfig.types.IntSetting
import dev.nohus.autokonfig.types.StringSetting
import dev.nohus.autokonfig.types.getInt
import dev.nohus.autokonfig.utils.TestAutoKonfig
import dev.nohus.autokonfig.utils.useAsProperties
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.assertions.throwables.shouldThrowExactlyUnit
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
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
        setting shouldBe "test"
    }

    "multiple settings can be read" {
        """
            foo = abc
            bar = def
            baz = ghi
        """.useAsProperties(testAutoKonfig)
        val foo by DefaultAutoKonfig.StringSetting()
        val baz by DefaultAutoKonfig.StringSetting()
        foo shouldBe "abc"
        baz shouldBe "ghi"
    }

    "multiple settings can be read from default config" {
        """
            foo = abc
            bar = def
            baz = ghi
        """.useAsProperties(testAutoKonfig)
        val foo by StringSetting()
        val baz by StringSetting()
        foo shouldBe "abc"
        baz shouldBe "ghi"
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
        foo shouldBe "abc"
        bar shouldBe "DEF"
        a shouldBe "abc"
        b shouldBe "DEF"
        c shouldBe "abc"
    }

    "keys with different casing types are matched" {
        """
            kebab-case = 1
            SNAKE_CASE = 2
            camelCase = 3
        """.useAsProperties(testAutoKonfig)
        AutoKonfig.getInt("kebab-case") shouldBe 1
        AutoKonfig.getInt("KEBAB_CASE") shouldBe 1
        AutoKonfig.getInt("kebabCase") shouldBe 1

        AutoKonfig.getInt("snake-case") shouldBe 2
        AutoKonfig.getInt("SNAKE_CASE") shouldBe 2
        AutoKonfig.getInt("snakeCase") shouldBe 2

        AutoKonfig.getInt("camel-case") shouldBe 3
        AutoKonfig.getInt("CAMEL_CASE") shouldBe 3
        AutoKonfig.getInt("camelCase") shouldBe 3
    }

    "keys can have custom names" {
        """
            foo = abc
        """.useAsProperties(testAutoKonfig)
        val bar by StringSetting(name = "foo")
        bar shouldBe "abc"
    }

    "multiple variables be delegated to the same setting" {
        """
            foo = abc
        """.useAsProperties(testAutoKonfig)
        val foo by StringSetting()
        val bar by StringSetting(name = "foo")
        foo shouldBe "abc"
        bar shouldBe "abc"
    }

    "setting can be read from system properties" {
        val key = "unit.test.system.property"
        val value = "value"
        System.setProperty(key, value)
        testAutoKonfig.resetDefaultAutoKonfig()
        val setting by StringSetting(name = key)
        setting shouldBe value
    }

    "setting can be read from environment variables" {
        val key = "unit.test.environment.variable"
        val value = "value"
        testAutoKonfig.setEnvironmentVariable(key, value)
        testAutoKonfig.resetDefaultAutoKonfig()
        val setting by StringSetting(name = key)
        setting shouldBe value
    }

    "setting can be read from config file in resources" {
        val config = AutoKonfig.withResourceConfig("resource.properties")
        val setting by config.StringSetting()
        setting shouldBe "resource"
    }

    "setting can be read from config file by URL" {
        """
            setting = test
        """.useAsProperties(testAutoKonfig)
        AutoKonfig.withURLConfig(testAutoKonfig.propertiesFile.toURI().toURL())
        val setting by StringSetting()
        setting shouldBe "test"
    }

    "setting can be read from config file by URL string" {
        """
            setting = test
        """.useAsProperties(testAutoKonfig)
        AutoKonfig.withURLConfig(testAutoKonfig.propertiesFile.toURI().toString())
        val setting by StringSetting()
        setting shouldBe "test"
    }

    "setting can be read from command line arguments" {
        val config = AutoKonfig.withCommandLineArguments(arrayOf("-a", "b", "-c"))
        val a by config.StringSetting()
        val c by config.BooleanSetting()
        a shouldBe "b"
        c.shouldBeTrue()
    }

    "settings can be read from multiple files" {
        val config = AutoKonfig.withConfigs(
            File("src/test/resources/test/multiple/application.properties"),
            File("src/test/resources/test/multiple/autokonfig.conf")
        )
        val foo by config.StringSetting()
        val bar by config.StringSetting()
        foo shouldBe "abc"
        bar shouldBe "def"
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
        foo shouldBe "abc"
        bar shouldBe "def"
    }

    "setting can be read in a group" {
        """
            groupA.subgroup.setting = test
        """.useAsProperties(testAutoKonfig)
        groupA.subgroup.setting shouldBe "test"
    }

    "setting can be read in a group with custom names" {
        """
            outer.inner.key = test
        """.useAsProperties(testAutoKonfig)
        groupB.subgroup.setting shouldBe "test"
    }

    "setting can be read in a group with some custom names" {
        """
            outer.subgroup.setting = test
        """.useAsProperties(testAutoKonfig)
        groupC.subgroup.setting shouldBe "test"
    }

    "getAll returns all settings" {
        """
            a = 1
            b = 2
        """.useAsProperties(testAutoKonfig)
        AutoKonfig.clear().withConfig(testAutoKonfig.propertiesFile)
        AutoKonfig.getAll() shouldContainExactly mapOf(
            "a" to "1",
            "b" to "2"
        )
    }

    "nonexistent setting throws an exception" {
        "".useAsProperties(testAutoKonfig)
        val exception = shouldThrowExactlyUnit<AutoKonfigException> {
            val nonexistent by StringSetting()
        }
        exception shouldHaveMessage "Required key \"nonexistent\" is missing"
    }

    "nonexistent config file throws an exception" {
        val file = File("nonexistent")
        val exception = shouldThrowExactlyUnit<AutoKonfigException> {
            AutoKonfig.withConfig(file)
        }
        exception shouldHaveMessage "Failed to read file: ${file.normalize().absolutePath}\nnonexistent: java.io.FileNotFoundException: nonexistent (No such file or directory)"
    }

    "nonexistent resources config file throws an exception" {
        val exception = shouldThrowExactlyUnit<AutoKonfigException> {
            AutoKonfig.withResourceConfig("nonexistent")
        }
        exception shouldHaveMessage "Failed to read resource: nonexistent\nnonexistent: java.io.IOException: resource not found on classpath: nonexistent"
    }

    "malformed URL config file throws an exception" {
        val exception = shouldThrowExactlyUnit<AutoKonfigException> {
            AutoKonfig.withURLConfig("fake://URL")
        }
        exception shouldHaveMessage "Failed to read malformed URL: fake://URL (unknown protocol: fake)"
    }

    "nonexistent URL config file throws an exception" {
        val exception = shouldThrowExactlyUnit<AutoKonfigException> {
            AutoKonfig.withURLConfig("file://nonexistent")
        }
        exception shouldHaveMessage "Failed to read URL: file://nonexistent\n: java.io.FileNotFoundException:  (No such file or directory)"
    }

    "wrong type of setting for value throws an exception" {
        """
            foo = test
        """.useAsProperties(testAutoKonfig)
        var exception = shouldThrowExactlyUnit<AutoKonfigException> {
            val a by IntSetting(name = "foo")
        }
        exception shouldHaveMessage "Failed to parse setting \"foo\", the value is \"test\", but must be an Int number"
        exception = shouldThrowExactlyUnit {
            val b by BigIntegerSetting(name = "foo")
        }
        exception shouldHaveMessage "Failed to parse setting \"foo\", the value is \"test\", but must be a BigInteger number"
        exception = shouldThrowExactlyUnit {
            val c by BigDecimalSetting(name = "foo")
        }
        exception shouldHaveMessage "Failed to parse setting \"foo\", the value is \"test\", but must be a BigDecimal number"
    }

    "invalid required include throws an exception" {
        shouldThrowExactly<AutoKonfigException> {
            AutoKonfig.withConfig(File("src/test/resources/test/include/invalid.conf"))
        }
    }
})
