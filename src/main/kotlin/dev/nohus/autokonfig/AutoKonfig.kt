package dev.nohus.autokonfig

import dev.nohus.autokonfig.types.Group
import dev.nohus.autokonfig.types.SettingType
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Created by Marcin Wisniowski (Nohus) on 05/01/2020.
 */

class AutoKonfig {

    private val settings = SettingStore()

    internal fun addProperty(key: String, value: Value, source: String) {
        settings.addProperty(key, value, source)
    }

    internal fun addFlag(flag: String, source: String) {
        settings.addFlag(flag, source)
    }

    fun getKeySource(key: String): String {
        return settings.getSource(key)?.toString() ?: "Key \"$key\" not found"
    }

    fun clear() = apply {
        settings.clear()
    }

    fun getAll() = settings.getAll()

    private fun <T> getValueOrNull(key: String, type: SettingType<T>): T? {
        val value = settings.findValue(key) ?: return null
        return try {
            type.transform(value)
        } catch (e: SettingParseException) {
            throw AutoKonfigException("Failed to parse setting \"$key\", the value is \"$value\", but ${e.reason}")
        }
    }

    private fun <T> getValue(key: String, type: SettingType<T>, default: T?): T {
        return getValueOrNull(key, type) ?: default ?: throw AutoKonfigException("Required key \"$key\" is missing")
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

    open inner class NullableSettingProvider<T>(
        private val delegateProvider: (String) -> NullableSettingDelegate<T>,
        private val name: String?,
        private val group: Group?
    ) {

        operator fun provideDelegate(thisRef: Any?, prop: KProperty<*>): ReadOnlyProperty<Any?, T?> {
            val effectiveName = name ?: prop.name
            val fullName = if (group != null) "${group.fullName}.$effectiveName" else effectiveName
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

    inner class NullableSettingDelegate<T>(
        private val type: SettingType<T>,
        private val key: String,
    ) : ReadOnlyProperty<Any?, T?> {

        fun getValue() = getValueOrNull(key, type)
        override operator fun getValue(thisRef: Any?, property: KProperty<*>): T? = getValue()
    }

    internal fun <T> getSettingProvider(type: SettingType<T>, default: T?, name: String?, group: Group?): SettingProvider<T> {
        return SettingProvider({ SettingDelegate(type, it, default) }, default != null, name, group)
    }

    internal fun <T> getNullableSettingProvider(type: SettingType<T>, name: String?, group: Group?): NullableSettingProvider<T> {
        return NullableSettingProvider({ NullableSettingDelegate(type, it) }, name, group)
    }

    fun <T> get(type: SettingType<T>, key: String, default: T? = null): T = getValue(key, type, default)

    fun <T> getOrNull(type: SettingType<T>, key: String): T? = getValueOrNull(key, type)

    companion object
}
