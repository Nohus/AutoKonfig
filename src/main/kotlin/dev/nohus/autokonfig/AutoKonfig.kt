package dev.nohus.autokonfig

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Created by Marcin Wisniowski (Nohus) on 05/01/2020.
 */

class AutoKonfig {

    private val settings = SettingsStore()

    internal fun addProperty(key: String, value: String, source: SettingSource) {
        settings.addProperty(key, value, source)
    }

    internal fun addFlag(flag: String, source: SettingSource) {
        settings.addFlag(flag, source)
    }

    fun getKeySource(key: String): String {
        return settings.getSource(key)?.toString() ?: "Key \"$key\" not found"
    }

    fun clear() = apply {
        settings.clear()
    }

    fun getAll() = settings.getAll()

    private fun <T> getValue(key: String, type: SettingType<T>, default: T?): T {
        val value = settings.findValue(key)
        return if (value != null) {
            try {
                type.transform(value)
            } catch (e: SettingParseException) {
                throw AutoKonfigException("Failed to parse setting \"$key\", the value is \"$value\", but ${e.reason}", e)
            }
        }
        else default ?: throw AutoKonfigException("Required key \"$key\" is missing")
    }

    open inner class SettingProvider<T>(
        private val delegateProvider: (String) -> SettingDelegate<T>,
        private val optional: Boolean,
        private val name: String?,
        private val group: Group?
    ) {

        private fun checkExists(key: String) {
            delegateProvider(key).getValue()
        }

        operator fun provideDelegate(thisRef: Any?, prop: KProperty<*>): ReadOnlyProperty<Any?, T> {
            val effectiveName = name ?: prop.name
            val fullName = if (group != null) "${group.fullName}.$effectiveName" else effectiveName
            if (!optional) checkExists(fullName)
            return delegateProvider(fullName)
        }
    }

    inner class SettingDelegate<T>(
        private val type: SettingType<T>,
        private val key: String,
        private val default: T?
    ) : ReadOnlyProperty<Any?, T> {

        fun getValue() = getValue(key, type, default)
        override operator fun getValue(thisRef: Any?, property: KProperty<*>): T = getValue()
    }

    internal fun <T> getSettingProvider(type: SettingType<T>, default: T?, name: String?, group: Group?): SettingProvider<T> {
        return SettingProvider({ SettingDelegate(type, it, default) }, default != null, name, group)
    }

    fun <T> get(type: SettingType<T>, key: String, default: T? = null): T = getValue(key, type, default)

    companion object {
        fun clear() = DefaultAutoKonfig.clear()
        fun getAll() = DefaultAutoKonfig.getAll()
        fun getKeySource(key: String) = DefaultAutoKonfig.getKeySource(key)
    }
}
