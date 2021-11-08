package dev.nohus.autokonfig

import dev.nohus.autokonfig.types.IntSetting
import dev.nohus.autokonfig.types.IntSettingType
import dev.nohus.autokonfig.types.ListSettingType
import dev.nohus.autokonfig.types.StringSettingType
import dev.nohus.autokonfig.types.getInt
import dev.nohus.autokonfig.types.getList
import dev.nohus.autokonfig.types.getString
import dev.nohus.autokonfig.utils.TestAutoKonfig
import dev.nohus.autokonfig.utils.useAsHocon
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import java.io.File

/**
 * Created by Marcin Wisniowski (Nohus) on 08/02/2020.
 */

class ParsingTest : FreeSpec({

    val testAutoKonfig = TestAutoKonfig()
    listener(testAutoKonfig)

    "parses json" {
        """
            {
                "foo" : {
                    "bar" : 10,
                    "baz" : [12]
                }
            }
        """.useAsHocon(testAutoKonfig)
        AutoKonfig.getList(IntSettingType, "foo.baz") shouldContainExactly listOf(12)
    }

    "parses hocon" {
        """
            foo {
                bar = 10
                baz = 12
            }
        """.useAsHocon(testAutoKonfig)
        AutoKonfig.getInt("foo.baz") shouldBe 12
    }

    "parses multiple keys in a single line" {
        """
            foo.bar=10, foo.baz=12
        """.useAsHocon(testAutoKonfig)
        AutoKonfig.getInt("foo.baz") shouldBe 12
    }

    "parses substitutions" {
        """
            foo = 15
            bar = ${'$'}{foo}
        """.useAsHocon(testAutoKonfig)
        AutoKonfig.getInt("bar") shouldBe 15
    }

    "parses arrays" {
        """
            foo = [1,2,3]
            bar = [1,2,3,]
            baz = [1, 2, 3, ]
        """.useAsHocon(testAutoKonfig)
        AutoKonfig.getList(IntSettingType, "foo") shouldContainExactly listOf(1, 2, 3)
        AutoKonfig.getList(IntSettingType, "bar") shouldContainExactly listOf(1, 2, 3)
        AutoKonfig.getList(IntSettingType, "baz") shouldContainExactly listOf(1, 2, 3)
    }

    "parses arrays with substitutions" {
        """
            a = 1
            b = 2
            c = 3
            d = 4
            foo = [ ${'$'}{a} ${'$'}{b}, ${'$'}{c} ${'$'}{d} ]
        """.useAsHocon(testAutoKonfig)
        AutoKonfig.getList(StringSettingType, "foo") shouldContainExactly listOf("1 2", "3 4")
    }

    "parses multi-line arrays" {
        """
            foo = [ 1
                    2
                    3 ]
        """.useAsHocon(testAutoKonfig)
        AutoKonfig.getList(IntSettingType, "foo") shouldContainExactly listOf(1, 2, 3)
    }

    "parses nested arrays" {
        """
            foo = [ [ 1 ] ]
            bar = [ [1,2], [3,4] ]
        """.useAsHocon(testAutoKonfig)
        AutoKonfig.getList(ListSettingType(IntSettingType), "foo") shouldContainExactly listOf(listOf(1))
        AutoKonfig.getList(ListSettingType(IntSettingType), "bar") shouldContainExactly listOf(listOf(1, 2), listOf(3, 4))
    }

    "parses plus-equals arrays" {
        """
            foo = [ 1 ]
            foo += 2
        """.useAsHocon(testAutoKonfig)
        AutoKonfig.getList(IntSettingType, "foo") shouldContainExactly listOf(1, 2)
    }

    "parses comments" {
        """
            // a
            # b
            foo = 15 // c
        """.useAsHocon(testAutoKonfig)
        AutoKonfig.getInt("foo") shouldBe 15
    }

    "fields overwrite previous fields with the same key" {
        """
            foo = 10
            foo = 20
        """.useAsHocon(testAutoKonfig)
        AutoKonfig.getInt("foo") shouldBe 20
    }

    "objects with the same key merge" {
        """
            foo { a:1, b:2 }
            foo { b:3, c:4 }
        """.useAsHocon(testAutoKonfig)
        AutoKonfig.getInt("foo.a") shouldBe 1
        AutoKonfig.getInt("foo.b") shouldBe 3
        AutoKonfig.getInt("foo.c") shouldBe 4
    }

    "parses multi-line strings" {
        """
            foo = ${"\"\"\""}a
            b${"\"\"\"\""}
        """.useAsHocon(testAutoKonfig)
        AutoKonfig.getString("foo") shouldBe "a\nb\""
    }

    "parses concatenated strings" {
        """
            foo = foo bar baz
            bar = 100 c
            key with spaces = 20
        """.useAsHocon(testAutoKonfig)
        AutoKonfig.getString("foo") shouldBe "foo bar baz"
        AutoKonfig.getString("bar") shouldBe "100 c"
        AutoKonfig.getInt("key with spaces") shouldBe 20
    }

    "parses concatenated objects" {
        """
            foo = { a:1 } { b:2 }
            bar = [ 1, 2 ] [ 3, 4 ]
        """.useAsHocon(testAutoKonfig)
        AutoKonfig.getInt("foo.a") shouldBe 1
        AutoKonfig.getInt("foo.b") shouldBe 2
        AutoKonfig.getList(IntSettingType, "bar") shouldBe listOf(1, 2, 3, 4)
    }

    "parses path expressions with quotes" {
        """
            foo."bar.baz" = 10
            bar."".c = 20
        """.useAsHocon(testAutoKonfig)
        AutoKonfig.getInt("foo.\"bar.baz\"") shouldBe 10
        AutoKonfig.getInt("bar.\"\".c") shouldBe 20
    }

    "parses includes" {
        AutoKonfig.withConfig(File("src/test/resources/test/include/main.conf"))
        val a by IntSetting(name = "root.a")
        a shouldBe 10
    }
})
