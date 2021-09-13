package dev.nohus.autokonfig

import dev.nohus.autokonfig.utils.CommandLineParser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Created by Marcin Wisniowski (Nohus) on 06/01/2020.
 */

class CommandLineParserTest {

    @Test
    fun `parses values`() {
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

    @Test
    fun `parses flags`() {
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

    @Test
    fun `parses empty command line`() {
        val map = CommandLineParser().parse(arrayOf())
        assertTrue(map.isEmpty())
    }

    @Test
    fun `ignores values without keys`() {
        val map = CommandLineParser().parse(arrayOf("-a", "foo", "bar", "--foo", "bar", "baz"))
        assertEquals(
            mapOf(
                "a" to "foo",
                "foo" to "bar"
            ),
            map
        )
    }

    @Test
    fun `parses quoted values`() {
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
}
