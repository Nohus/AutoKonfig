package dev.nohus.autokonfig

import dev.nohus.autokonfig.utils.ConfigFileLocator
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

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
        locator.getConfigFiles().first().name shouldBe "autokonfig.conf"
    }

    "finds multiple config files" {
        val locator = createLocator("multiple")
        locator.getConfigFiles().map { it.name } shouldContainExactly listOf(
            "application.conf", "application.json", "application.properties",
            "autokonfig.conf", "autokonfig.json", "autokonfig.properties",
            "config.conf", "config.json", "config.properties"
        )
    }

    "finds no files when no valid files exist" {
        val locator = createLocator("invalid")
        locator.getConfigFiles().shouldBeEmpty()
    }

    "finds no files when no files exist" {
        val locator = createLocator("empty")
        locator.getConfigFiles().shouldBeEmpty()
    }

    "find no files when base directory does not exist" {
        val locator = createLocator("nonexistent")
        locator.getConfigFiles().shouldBeEmpty()
    }

    "does not search in subdirectories" {
        val locator = createLocator("subdir")
        locator.getConfigFiles().shouldBeEmpty()
    }
})
