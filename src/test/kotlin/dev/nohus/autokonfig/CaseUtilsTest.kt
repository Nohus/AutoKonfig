package dev.nohus.autokonfig

import dev.nohus.autokonfig.utils.CaseUtils.toKebabCase
import dev.nohus.autokonfig.utils.CaseUtils.toSnakeCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Created by Marcin Wisniowski (Nohus) on 05/01/2020.
 */

class CaseUtilsTest {

    @Test
    fun `converts to snake case`() {
        assertEquals("a", "a".toSnakeCase())
        assertEquals("a_b", "aB".toSnakeCase())
        assertEquals("a_bc", "aBc".toSnakeCase())
        assertEquals("a_bc_d", "aBcD".toSnakeCase())
        assertEquals("abc", "Abc".toSnakeCase())
    }

    @Test
    fun `converts to kebab case`() {
        assertEquals("a", "a".toKebabCase())
        assertEquals("a-b", "aB".toKebabCase())
        assertEquals("a-bc", "aBc".toKebabCase())
        assertEquals("a-bc-d", "aBcD".toKebabCase())
        assertEquals("abc", "Abc".toKebabCase())
    }
}
