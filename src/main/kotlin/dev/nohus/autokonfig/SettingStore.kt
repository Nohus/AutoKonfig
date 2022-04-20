package dev.nohus.autokonfig

import dev.nohus.autokonfig.Value.SimpleValue
import dev.nohus.autokonfig.utils.CaseUtils.toCamelCase
import dev.nohus.autokonfig.utils.CaseUtils.toKebabCase
import dev.nohus.autokonfig.utils.CaseUtils.toSnakeCase
import java.util.Locale

/**
 * Created by Marcin Wisniowski (Nohus) on 07/01/2020.
 */

internal class SettingStore {

    private val properties = mutableMapOf<String, Value>()
    private val flags = mutableSetOf<String>()
    private val sources = mutableMapOf<String, String>()

    fun addProperty(key: String, value: Value, source: String) {
        properties[key] = value
        sources[key] = source
    }

    fun addFlag(flag: String, source: String) {
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

    fun findValue(key: String): Value? {
        return findMatchingKey(key)?.let { getValue(it) }
    }

    fun getAll(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        map += properties.entries.associate { it.key to it.value.toString() }
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
        val lowerKey = key.lowercase(Locale.US)
        return properties.keys.firstOrNull { it.lowercase(Locale.US) == lowerKey }
            ?.toString() ?: flags.firstOrNull { it.lowercase(Locale.US) == lowerKey }
    }

    private fun getKeyRepresentations(key: String): List<String> {
        return listOf(key, key.toSnakeCase(), key.toKebabCase(), key.toCamelCase())
    }

    private fun getValue(key: String): Value? {
        return properties[key] ?: if (flags.contains(key)) SimpleValue(true.toString()) else null
    }

    private fun containsKey(key: String): Boolean {
        return properties.containsKey(key) || flags.contains(key)
    }
}
