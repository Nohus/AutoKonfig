package dev.nohus.autokonfig

import dev.nohus.autokonfig.CaseUtils.toKebabCase
import dev.nohus.autokonfig.CaseUtils.toSnakeCase
import java.util.*

/**
 * Created by Marcin Wisniowski (Nohus) on 07/01/2020.
 */

internal class SettingsStore {

    private val properties: Properties = Properties()
    private val flags = mutableSetOf<String>()

    fun addProperty(key: String, value: String) {
        properties[key] = value
    }

    fun addFlag(flag: String) {
        flags += flag
    }

    fun clear() {
        properties.clear()
        flags.clear()
    }

    fun findValue(key: String): String? {
        val keys = getKeyRepresentations(key)
        return keys.asSequence().map(::getValue).firstOrNull { it != null }
            ?: keys.asSequence().map(::getValueIgnoringCase).firstOrNull { it != null }
    }

    private fun getKeyRepresentations(key: String): List<String> {
        return listOf(key, key.toSnakeCase(), key.toKebabCase())
    }

    private fun getValue(key: String): String? {
        return properties.getProperty(key) ?: if (flags.contains(key)) "true" else null
    }

    private fun getValueIgnoringCase(key: String): String? {
        val lowerKey = key.toLowerCase(Locale.US)
        return properties.entries.firstOrNull {
            it.key.toString().toLowerCase(Locale.US) == lowerKey
        }?.value?.toString() ?: if (flags.any { it.toLowerCase(Locale.US) == lowerKey }) "true" else null
    }
}
