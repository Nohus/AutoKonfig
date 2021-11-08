package dev.nohus.autokonfig

import dev.nohus.autokonfig.utils.CaseUtils.toKebabCase
import dev.nohus.autokonfig.utils.CaseUtils.toSnakeCase
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

/**
 * Created by Marcin Wisniowski (Nohus) on 05/01/2020.
 */

class CaseUtilsTest : FreeSpec({

    listOf(
        "a" to "a",
        "aB" to "a_b",
        "aBc" to "a_bc",
        "aBcD" to "a_bc_d",
        "Abc" to "abc"
    ).forEach { (from, expected) ->
        "converts $from to snake case $expected" {
            from.toSnakeCase() shouldBe expected
        }
    }

    listOf(
        "a" to "a",
        "aB" to "a-b",
        "aBc" to "a-bc",
        "aBcD" to "a-bc-d",
        "Abc" to "abc"
    ).forEach { (from, expected) ->
        "converts $from to kebab case $expected" {
            from.toKebabCase() shouldBe expected
        }
    }
})
