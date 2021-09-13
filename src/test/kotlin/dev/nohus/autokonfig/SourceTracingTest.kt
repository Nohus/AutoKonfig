package dev.nohus.autokonfig

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.Properties

/**
 * Created by Marcin Wisniowski (Nohus) on 08/02/2020.
 */

class SourceTracingTest : BaseAutoKonfigTest() {

    @Test
    fun `setting can be traced to a file`() {
        """
            foo = 2
        """.trimIndent().useAsProperties()
        AutoKonfig.clear().withConfig(file)
        assertEquals(
            "Key \"foo\" was read from config file at \"${file.normalize().absolutePath}\"",
            AutoKonfig.getKeySource("foo")
        )
    }

    @Test
    fun `setting with a fuzzy matched key can be traced to a file`() {
        """
            SERVER_PORT = 2
        """.trimIndent().useAsProperties()
        AutoKonfig.clear().withConfig(file)
        assertEquals(
            "Key \"serverPort\" was read as \"SERVER_PORT\" from config file at \"${file.normalize().absolutePath}\"",
            AutoKonfig.getKeySource("serverPort")
        )
    }

    @Test
    fun `setting can be traced to a resource file`() {
        val config = AutoKonfig.clear().withResourceConfig("resource.properties")
        assertEquals(
            "Key \"setting\" was read from config file resource at \"resource.properties\"",
            config.getKeySource("setting")
        )
    }

    @Test
    fun `setting can be traced to environment variables`() {
        val key = "unit.test.environment.variable"
        val value = "value"
        setEnvironmentVariable(key, value)
        resetDefaultAutoKonfig()
        assertEquals(
            "Key \"unit.test.environment.variable\" was read from environment variables",
            AutoKonfig.getKeySource(key)
        )
    }

    @Test
    fun `setting can be traced to system properties`() {
        val key = "unit.test.system.property"
        val value = "value"
        System.setProperty(key, value)
        resetDefaultAutoKonfig()
        assertEquals(
            "Key \"unit.test.system.property\" was read from system properties",
            AutoKonfig.getKeySource(key)
        )
    }

    @Test
    fun `setting can be traced to command line arguments`() {
        AutoKonfig.withCommandLineArguments(arrayOf("-a", "b"))
        assertEquals("Key \"a\" was read from command line parameters", AutoKonfig.getKeySource("a"))
    }

    @Test
    fun `setting can be traced to manually inserted properties`() {
        AutoKonfig.withProperties(Properties().apply { put("a", "b") })
        DefaultAutoKonfig.withProperties(Properties().apply { put("c", "d") })
        Assertions.assertTrue(AutoKonfig.getKeySource("a").startsWith("Key \"a\" was read from properties inserted by org.junit"))
        Assertions.assertTrue(AutoKonfig.getKeySource("c").startsWith("Key \"c\" was read from properties inserted by org.junit"))
    }

    @Test
    fun `setting can be traced to manually inserted map`() {
        AutoKonfig.withMap(mapOf("a" to "b"))
        DefaultAutoKonfig.withMap(mapOf("c" to "d"))
        Assertions.assertTrue(AutoKonfig.getKeySource("a").startsWith("Key \"a\" was read from a map inserted by org.junit"))
        Assertions.assertTrue(AutoKonfig.getKeySource("c").startsWith("Key \"c\" was read from a map inserted by org.junit"))
    }
}
