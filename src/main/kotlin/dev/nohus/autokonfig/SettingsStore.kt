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
    private val sources = mutableMapOf<String, SettingSource>()

    fun addProperty(key: String, value: String, source: SettingSource) {
        properties[key] = value
        sources[key] = source
    }

    fun addFlag(flag: String, source: SettingSource) {
        flags += flag
        sources[flag] = source
    }

    fun clear() {
        properties.clear()
        flags.clear()
        sources.clear()
    }

    fun getSource(key: String): KeySource? {
        val matchingKey = findMatchingKey(key) ?: return null
        val source = sources[matchingKey] ?: return null
        return KeySource(key, matchingKey, source)
    }

    fun findValue(key: String): String? {
        return findMatchingKey(key)?.let { getValue(it) }
    }

    fun getAll(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        map += properties.entries.map { it.key.toString() to it.value.toString() }.toMap()
        map += flags.map { it to true.toString() }
        return map
    }

    private fun findMatchingKey(key: String): String? {
        if (containsKey(key)) return key
        val keys = getKeyRepresentations(key)
        return keys.asSequence().filter(::containsKey).firstOrNull()
            ?: keys.asSequence().map(::findMatchingKeyIgnoringCase).firstOrNull { it != null }
    }

    private fun findMatchingKeyIgnoringCase(key: String): String? {
        val lowerKey = key.toLowerCase(Locale.US)
        return properties.keys.firstOrNull { it.toString().toLowerCase(Locale.US) == lowerKey }
            ?.toString() ?: flags.firstOrNull { it.toLowerCase(Locale.US) == lowerKey }
    }

    private fun getKeyRepresentations(key: String): List<String> {
        return listOf(key, key.toSnakeCase(), key.toKebabCase())
    }

    private fun getValue(key: String): String? {
        return properties.getProperty(key) ?: if (flags.contains(key)) true.toString() else null
    }

    private fun containsKey(key: String): Boolean {
        return properties.containsKey(key) || flags.contains(key)
    }
}
