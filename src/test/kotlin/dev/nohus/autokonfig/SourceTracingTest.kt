package dev.nohus.autokonfig

import dev.nohus.autokonfig.utils.TestAutoKonfig
import dev.nohus.autokonfig.utils.useAsProperties
import io.kotest.core.spec.style.FreeSpec
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import java.util.Properties

/**
 * Created by Marcin Wisniowski (Nohus) on 08/02/2020.
 */

class SourceTracingTest : FreeSpec({

    val testAutoKonfig = TestAutoKonfig()
    listener(testAutoKonfig)

    "setting can be traced to a file" {
        """
            foo = 2
        """.useAsProperties(testAutoKonfig)
        AutoKonfig.clear().withConfig(testAutoKonfig.propertiesFile)
        assertEquals(
            "Key \"foo\" was read from config file at \"${testAutoKonfig.propertiesFile.normalize().absolutePath}\"",
            AutoKonfig.getKeySource("foo")
        )
    }

    "setting with a fuzzy matched key can be traced to a file" {
        """
            SERVER_PORT = 2
        """.useAsProperties(testAutoKonfig)
        AutoKonfig.clear().withConfig(testAutoKonfig.propertiesFile)
        assertEquals(
            "Key \"serverPort\" was read as \"SERVER_PORT\" from config file at \"${testAutoKonfig.propertiesFile.normalize().absolutePath}\"",
            AutoKonfig.getKeySource("serverPort")
        )
    }

    "setting can be traced to a resource file" {
        val config = AutoKonfig.clear().withResourceConfig("resource.properties")
        assertEquals(
            "Key \"setting\" was read from config file resource at \"resource.properties\"",
            config.getKeySource("setting")
        )
    }

    "setting can be traced to environment variables" {
        val key = "unit.test.environment.variable"
        val value = "value"
        testAutoKonfig.setEnvironmentVariable(key, value)
        testAutoKonfig.resetDefaultAutoKonfig()
        assertEquals(
            "Key \"unit.test.environment.variable\" was read from environment variables",
            AutoKonfig.getKeySource(key)
        )
    }

    "setting can be traced to system properties" {
        val key = "unit.test.system.property"
        val value = "value"
        System.setProperty(key, value)
        testAutoKonfig.resetDefaultAutoKonfig()
        assertEquals(
            "Key \"unit.test.system.property\" was read from system properties",
            AutoKonfig.getKeySource(key)
        )
    }

    "setting can be traced to command line arguments" {
        AutoKonfig.withCommandLineArguments(arrayOf("-a", "b"))
        assertEquals("Key \"a\" was read from command line parameters", AutoKonfig.getKeySource("a"))
    }

    "setting can be traced to manually inserted properties" {
        AutoKonfig.withProperties(Properties().apply { put("a", "b") })
        DefaultAutoKonfig.withProperties(Properties().apply { put("c", "d") })
        Assertions.assertTrue(AutoKonfig.getKeySource("a").startsWith("Key \"a\" was read from properties inserted by io.kotest"))
        Assertions.assertTrue(AutoKonfig.getKeySource("c").startsWith("Key \"c\" was read from properties inserted by io.kotest"))
    }

    "setting can be traced to manually inserted map" {
        AutoKonfig.withMap(mapOf("a" to "b"))
        DefaultAutoKonfig.withMap(mapOf("c" to "d"))
        Assertions.assertTrue(AutoKonfig.getKeySource("a").startsWith("Key \"a\" was read from a map inserted by io.kotest"))
        Assertions.assertTrue(AutoKonfig.getKeySource("c").startsWith("Key \"c\" was read from a map inserted by io.kotest"))
    }
})
