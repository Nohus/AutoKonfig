package dev.nohus.autokonfig

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Created by Marcin Wisniowski (Nohus) on 05/01/2020.
 */

class AutoKonfig {

    private val settings = SettingsStore()

    fun addProperty(key: String, value: String) {
        settings.addProperty(key, value)
    }

    fun addFlag(flag: String) {
        settings.addFlag(flag)
    }

    fun clear() = apply {
        settings.clear()
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
        private val key: String,
        private val transform: (String) -> T,
        private val default: T?
    ) : ReadOnlyProperty<Any?, T> {

        fun getValue(): T {
            val value = settings.findValue(key)
            return if (value != null) {
                try {
                    transform(value)
                } catch (e: Exception) {
                    throw AutoKonfigException("Failed to parse setting \"foo\", the value is: test", e)
                }
            }
            else default ?: throw AutoKonfigException("Required key \"$key\" is missing")
        }

        override operator fun getValue(thisRef: Any?, property: KProperty<*>): T = getValue()
    }

    internal inner class StringSettingProvider(default: String?, name: String?, group: Group?)
        : SettingProvider<String>({ SettingDelegate(it, ::mapString, default) }, default != null, name, group)
    internal inner class BooleanSettingProvider(default: Boolean?, name: String?, group: Group?)
        : SettingProvider<Boolean>({ SettingDelegate(it, ::mapBoolean, default) }, default != null, name, group)
    internal inner class IntSettingProvider(default: Int?, name: String?, group: Group?)
        : SettingProvider<Int>({ SettingDelegate(it, ::mapInt, default) }, default != null, name, group)
    internal inner class LongSettingProvider(default: Long?, name: String?, group: Group?)
        : SettingProvider<Long>({ SettingDelegate(it, ::mapLong, default) }, default != null, name, group)
    internal inner class FloatSettingProvider(default: Float?, name: String?, group: Group?)
        : SettingProvider<Float>({ SettingDelegate(it, ::mapFloat, default) }, default != null, name, group)
    internal inner class DoubleSettingProvider(default: Double?, name: String?, group: Group?)
        : SettingProvider<Double>({ SettingDelegate(it, ::mapDouble, default) }, default != null, name, group)

    private fun mapString(value: String) = value
    private fun mapBoolean(value: String) = value in listOf("true", "yes", "1")
    private fun mapInt(value: String) = value.toInt()
    private fun mapLong(value: String) = value.toLong()
    private fun mapFloat(value: String) = value.toFloat()
    private fun mapDouble(value: String) = value.toDouble()
}
