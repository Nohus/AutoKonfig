package dev.nohus.autokonfig

import dev.nohus.autokonfig.types.IntSetting
import dev.nohus.autokonfig.types.IntSettingType
import dev.nohus.autokonfig.types.ListSettingType
import dev.nohus.autokonfig.types.StringSettingType
import dev.nohus.autokonfig.types.getInt
import dev.nohus.autokonfig.types.getList
import dev.nohus.autokonfig.types.getString
import java.io.File
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Created by Marcin Wisniowski (Nohus) on 08/02/2020.
 */

class ParsingTest : BaseAutoKonfigTest() {

    @Test
    fun `parses json`() {
        """
            {
                "foo" : {
                    "bar" : 10,
                    "baz" : [12]
                }
            }
        """.trimIndent().useAsHocon()
        assertEquals(listOf(12), AutoKonfig.getList(IntSettingType, "foo.baz"))
    }

    @Test
    fun `parses hocon`() {
        """
            foo {
                bar = 10
                baz = 12
            }
        """.trimIndent().useAsHocon()
        assertEquals(12, AutoKonfig.getInt("foo.baz"))
    }

    @Test
    fun `parses multiple keys in a single line`() {
        """
            foo.bar=10, foo.baz=12
        """.trimIndent().useAsHocon()
        assertEquals(12, AutoKonfig.getInt("foo.baz"))
    }

    @Test
    fun `parses substitutions`() {
        """
            foo = 15
            bar = ${'$'}{foo}
        """.trimIndent().useAsHocon()
        assertEquals(15, AutoKonfig.getInt("bar"))
    }

    @Test
    fun `parses arrays`() {
        """
            foo = [1,2,3]
            bar = [1,2,3,]
            baz = [1, 2, 3, ]
        """.trimIndent().useAsHocon()
        assertEquals(listOf(1, 2, 3), AutoKonfig.getList(IntSettingType, "foo"))
        assertEquals(listOf(1, 2, 3), AutoKonfig.getList(IntSettingType, "bar"))
        assertEquals(listOf(1, 2, 3), AutoKonfig.getList(IntSettingType, "baz"))
    }

    @Test
    fun `parses arrays with substitutions`() {
        """
            a = 1
            b = 2
            c = 3
            d = 4
            foo = [ ${'$'}{a} ${'$'}{b}, ${'$'}{c} ${'$'}{d} ]
        """.trimIndent().useAsHocon()
        assertEquals(listOf("1 2", "3 4"), AutoKonfig.getList(StringSettingType, "foo"))
    }

    @Test
    fun `parses multi-line arrays`() {
        """
            foo = [ 1
                    2
                    3 ]
        """.trimIndent().useAsHocon()
        assertEquals(listOf(1, 2, 3), AutoKonfig.getList(IntSettingType, "foo"))
    }

    @Test
    fun `parses nested arrays`() {
        """
            foo = [ [ 1 ] ]
            bar = [ [1,2], [3,4] ]
        """.trimIndent().useAsHocon()
        assertEquals(listOf(listOf(1)), AutoKonfig.getList(ListSettingType(IntSettingType), "foo"))
        assertEquals(listOf(listOf(1, 2), listOf(3, 4)), AutoKonfig.getList(ListSettingType(IntSettingType), "bar"))
    }

    @Test
    fun `parses plus-equals arrays`() {
        """
            foo = [ 1 ]
            foo += 2
        """.trimIndent().useAsHocon()
        assertEquals(listOf(1, 2), AutoKonfig.getList(IntSettingType, "foo"))
    }

    @Test
    fun `parses comments`() {
        """
            // a
            # b
            foo = 15 // c
        """.trimIndent().useAsHocon()
        assertEquals(15, AutoKonfig.getInt("foo"))
    }

    @Test
    fun `fields overwrite previous fields with the same key`() {
        """
            foo = 10
            foo = 20
        """.trimIndent().useAsHocon()
        assertEquals(20, AutoKonfig.getInt("foo"))
    }

    @Test
    fun `objects with the same key merge`() {
        """
            foo { a:1, b:2 }
            foo { b:3, c:4 }
        """.trimIndent().useAsHocon()
        assertEquals(1, AutoKonfig.getInt("foo.a"))
        assertEquals(3, AutoKonfig.getInt("foo.b"))
        assertEquals(4, AutoKonfig.getInt("foo.c"))
    }

    @Test
    fun `parses multi-line strings`() {
        """
            foo = ${"\"\"\""}a
            b${"\"\"\"\""}
        """.trimIndent().useAsHocon()
        assertEquals("a\nb\"", AutoKonfig.getString("foo"))
    }

    @Test
    fun `parses concatenated strings`() {
        """
            foo = foo bar baz
            bar = 100 c
            key with spaces = 20
        """.trimIndent().useAsHocon()
        assertEquals("foo bar baz", AutoKonfig.getString("foo"))
        assertEquals("100 c", AutoKonfig.getString("bar"))
        assertEquals(20, AutoKonfig.getInt("key with spaces"))
    }

    @Test
    fun `parses concatenated objects`() {
        """
            foo = { a:1 } { b:2 }
            bar = [ 1, 2 ] [ 3, 4 ]
        """.trimIndent().useAsHocon()
        assertEquals(1, AutoKonfig.getInt("foo.a"))
        assertEquals(2, AutoKonfig.getInt("foo.b"))
        assertEquals(listOf(1, 2, 3, 4), AutoKonfig.getList(IntSettingType, "bar"))
    }

    @Test
    fun `parses path expressions with quotes`() {
        """
            foo."bar.baz" = 10
            bar."".c = 20
        """.trimIndent().useAsHocon()
        assertEquals(10, AutoKonfig.getInt("foo.\"bar.baz\""))
        assertEquals(20, AutoKonfig.getInt("bar.\"\".c"))
    }

    @Test
    fun `parses includes`() {
        AutoKonfig.withConfig(File("src/test/resources/test/include/main.conf"))
        val a by IntSetting(name = "root.a")
        assertEquals(10, a)
    }
}
