package dev.nohus.autokonfig

import dev.nohus.autokonfig.utils.CommandLineParser
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldContainExactly

/**
 * Created by Marcin Wisniowski (Nohus) on 06/01/2020.
 */

class CommandLineParserTest : FreeSpec({

    "parses values" {
        val map = CommandLineParser().parse(arrayOf("-a", "b", "--cfg", "d", "-g", "fg"))
        map shouldContainExactly mapOf(
            "a" to "b",
            "cfg" to "d",
            "g" to "fg"
        )
    }

    "parses flags" {
        val map = CommandLineParser().parse(arrayOf("-a", "b", "-c", "--test"))
        map shouldContainExactly mapOf(
            "a" to "b",
            "c" to null,
            "test" to null
        )
    }

    "parses empty command line" {
        val map = CommandLineParser().parse(arrayOf())
        map.shouldBeEmpty()
    }

    "ignores values without keys" {
        val map = CommandLineParser().parse(arrayOf("-a", "foo", "bar", "--foo", "bar", "baz"))
        map shouldContainExactly mapOf(
            "a" to "foo",
            "foo" to "bar"
        )
    }

    "parses quoted values" {
        val map = CommandLineParser().parse(
            arrayOf("--a", "1", "2", "3", "--quoted", "this is quoted")
        )
        map shouldContainExactly mapOf(
            "a" to "1",
            "quoted" to "this is quoted"
        )
    }
})
