package dev.nohus.autokonfig

/**
 * Created by Marcin Wisniowski (Nohus) on 08/01/2020.
 */

data class KeySource(val key: String, val matchingKey: String, val source: String) {

    override fun toString(): String {
        return if (key == matchingKey) "Key \"$key\" was read from $source"
        else "Key \"$key\" was read as \"$matchingKey\" from $source"
    }
}
