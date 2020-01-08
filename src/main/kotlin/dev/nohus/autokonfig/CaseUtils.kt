package dev.nohus.autokonfig

/**
 * Created by Marcin Wisniowski (Nohus) on 07/01/2020.
 */

internal object CaseUtils {

    fun String.toSnakeCase() = toSeparatorCase("_")

    fun String.toKebabCase() = toSeparatorCase("-")

    private fun String.toSeparatorCase(separator: String): String {
        return mapIndexed { index, char ->
            if (index != 0 && char.isUpperCase()) "${separator}${char.toLowerCase()}"
            else "${char.toLowerCase()}"
        }.joinToString("")
    }
}
