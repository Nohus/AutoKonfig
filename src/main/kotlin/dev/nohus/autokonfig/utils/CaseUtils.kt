package dev.nohus.autokonfig.utils

/**
 * Created by Marcin Wisniowski (Nohus) on 07/01/2020.
 */

internal object CaseUtils {

    fun String.toSnakeCase() = toSeparatorCase(this, "_").lowercase()

    fun String.toKebabCase() = toSeparatorCase(this, "-").lowercase()

    fun String.toCamelCase(): String {
        var wasSeparator = false
        return windowed(2, partialWindows = true).joinToString("") { pair ->
            var isSeparator = false
            val newPair = if (wasSeparator) pair.take(1).uppercase()
            else if (pair[0] in listOf('-', '_')) "".also { isSeparator = true }
            else pair.take(1)
            wasSeparator = isSeparator
            newPair
        }
    }

    private fun toSeparatorCase(text: String, separator: String): String {
        val withSeparators = insertSeparatorsInCamelCase(text, separator)
        return withSeparators.replace("-", separator).replace("_", separator)
    }

    private fun insertSeparatorsInCamelCase(text: String, separator: String): String {
        var wasSeparator = false
        return text.windowed(2, partialWindows = true).joinToString("") { pair ->
            var isSeparator = false
            val newPair = if (wasSeparator) pair.take(1).lowercase()
            else if (pair.length < 2) pair
            else if (pair[0].isLowerCase() && pair[1].isUpperCase()) (pair[0] + separator).also { isSeparator = true }
            else pair.take(1)
            wasSeparator = isSeparator
            newPair
        }
    }
}
