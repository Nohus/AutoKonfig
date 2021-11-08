package dev.nohus.autokonfig

import dev.nohus.autokonfig.utils.ConfigFileLocator
import io.kotest.core.spec.style.FreeSpec
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

/**
 * Created by Marcin Wisniowski (Nohus) on 05/01/2020.
 */

class ConfigFileLocatorTest : FreeSpec({

    val directory = "src/test/resources/test"

    fun createLocator(testDirectory: String): ConfigFileLocator {
        return ConfigFileLocator("$directory/$testDirectory")
    }

    "finds single config file" {
        val locator = createLocator("single")
        assertEquals("autokonfig.conf", locator.getConfigFiles().first().name)
    }

    "finds multiple config files" {
        val locator = createLocator("multiple")
        assertEquals(
            listOf(
                "application.conf", "application.json", "application.properties",
                "autokonfig.conf", "autokonfig.json", "autokonfig.properties",
                "config.conf", "config.json", "config.properties"
            ),
            locator.getConfigFiles().map { it.name }
        )
    }

    "finds no files when no valid files exist" {
        val locator = createLocator("invalid")
        assertTrue(locator.getConfigFiles().isEmpty())
    }

    "finds no files when no files exist" {
        val locator = createLocator("empty")
        assertTrue(locator.getConfigFiles().isEmpty())
    }

    "find no files when base directory does not exist" {
        val locator = createLocator("nonexistent")
        assertTrue(locator.getConfigFiles().isEmpty())
    }

    "does not search in subdirectories" {
        val locator = createLocator("subdir")
        assertTrue(locator.getConfigFiles().isEmpty())
    }
})
