package dev.nohus.autokonfig

import dev.nohus.autokonfig.utils.CommandLineParser
import io.kotest.core.spec.style.FreeSpec
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

/**
 * Created by Marcin Wisniowski (Nohus) on 06/01/2020.
 */

class CommandLineParserTest : FreeSpec({

    "parses values" {
        val map = CommandLineParser().parse(arrayOf("-a", "b", "--cfg", "d", "-g", "fg"))
        assertEquals(
            mapOf(
                "a" to "b",
                "cfg" to "d",
                "g" to "fg"
            ),
            map
        )
    }

    "parses flags" {
        val map = CommandLineParser().parse(arrayOf("-a", "b", "-c", "--test"))
        assertEquals(
            mapOf(
                "a" to "b",
                "c" to null,
                "test" to null
            ),
            map
        )
    }

    "parses empty command line" {
        val map = CommandLineParser().parse(arrayOf())
        assertTrue(map.isEmpty())
    }

    "ignores values without keys" {
        val map = CommandLineParser().parse(arrayOf("-a", "foo", "bar", "--foo", "bar", "baz"))
        assertEquals(
            mapOf(
                "a" to "foo",
                "foo" to "bar"
            ),
            map
        )
    }

    "parses quoted values" {
        val map = CommandLineParser().parse(
            arrayOf("--a", "1", "2", "3", "--quoted", "this is quoted")
        )
        assertEquals(
            mapOf(
                "a" to "1",
                "quoted" to "this is quoted"
            ),
            map
        )
    }
})
