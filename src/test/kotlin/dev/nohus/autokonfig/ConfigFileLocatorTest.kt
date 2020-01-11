package dev.nohus.autokonfig

import dev.nohus.autokonfig.utils.ConfigFileLocator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Created by Marcin Wisniowski (Nohus) on 05/01/2020.
 */

class ConfigFileLocatorTest {

    private val directory = "src/test/resources/test"

    private fun createLocator(testDirectory: String): ConfigFileLocator {
        return ConfigFileLocator("$directory/$testDirectory")
    }

    @Test
    fun `finds single config file`() {
        val locator = createLocator("single")
        assertEquals("1.conf", locator.getConfigFiles().first().name)
    }

    @Test
    fun `finds multiple config files`() {
        val locator = createLocator("multiple")
        assertEquals(listOf("6.conf", "5.properties"), locator.getConfigFiles().map { it.name })
    }

    @Test
    fun `finds no files when no valid files exist`() {
        val locator = createLocator("invalid")
        assertTrue(locator.getConfigFiles().isEmpty())
    }

    @Test
    fun `finds no files when no files exist`() {
        val locator = createLocator("empty")
        assertTrue(locator.getConfigFiles().isEmpty())
    }

    @Test
    fun `find no files when base directory does not exist`() {
        val locator = createLocator("nonexistent")
        assertTrue(locator.getConfigFiles().isEmpty())
    }

    @Test
    fun `does not search in subdirectories`() {
        val locator = createLocator("subdir")
        assertTrue(locator.getConfigFiles().isEmpty())
    }
}
