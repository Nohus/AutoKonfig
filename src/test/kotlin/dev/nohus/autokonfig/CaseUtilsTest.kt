package dev.nohus.autokonfig

import dev.nohus.autokonfig.utils.CaseUtils.toCamelCase
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
        "Abc" to "abc",
        "a_b" to "a_b",
        "a_bc" to "a_bc",
        "a_bc_d" to "a_bc_d",
        "a-b" to "a_b",
        "a-bc" to "a_bc",
        "a-bc-d" to "a_bc_d",
        "ABCD" to "abcd",
        "AbCD" to "ab_cd"
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
        "Abc" to "abc",
        "a_b" to "a-b",
        "a_bc" to "a-bc",
        "a_bc_d" to "a-bc-d",
        "a-b" to "a-b",
        "a-bc" to "a-bc",
        "a-bc-d" to "a-bc-d",
        "ABCD" to "abcd",
        "AbCD" to "ab-cd"
    ).forEach { (from, expected) ->
        "converts $from to kebab case $expected" {
            from.toKebabCase() shouldBe expected
        }
    }

    listOf(
        "a" to "a",
        "aB" to "aB",
        "aBc" to "aBc",
        "aBcD" to "aBcD",
        "Abc" to "Abc",
        "a_b" to "aB",
        "a_bc" to "aBc",
        "a_bc_d" to "aBcD",
        "a-b" to "aB",
        "a-bc" to "aBc",
        "a-bc-d" to "aBcD",
        "ABCD" to "ABCD",
        "AbCD" to "AbCD"
    ).forEach { (from, expected) ->
        "converts $from to camel case $expected" {
            from.toCamelCase() shouldBe expected
        }
    }
})
