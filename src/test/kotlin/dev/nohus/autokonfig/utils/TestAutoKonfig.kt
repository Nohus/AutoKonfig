package dev.nohus.autokonfig.utils

import dev.nohus.autokonfig.AutoKonfig
import dev.nohus.autokonfig.clear
import dev.nohus.autokonfig.withConfigs
import dev.nohus.autokonfig.withEnvironmentVariables
import dev.nohus.autokonfig.withSystemProperties
import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import java.io.File
import kotlin.reflect.jvm.isAccessible

class TestAutoKonfig : TestListener {

    val propertiesFile = File("autokonfig.properties")
    val hoconFile = File("autokonfig.conf")

    fun useAsProperties(text: String) {
        propertiesFile.writeText(text)
        resetDefaultAutoKonfig()
    }

    fun useAsHocon(text: String) {
        hoconFile.writeText(text)
        resetDefaultAutoKonfig()
    }

    fun resetDefaultAutoKonfig() {
        with(AutoKonfig) {
            clear()
            withSystemProperties()
            withEnvironmentVariables()
            withConfigs(ConfigFileLocator().getConfigFiles())
        }
    }

    fun setEnvironmentVariable(key: String, value: String) {
        val environment = System.getenv()
        val field = environment::class.members.first { it.name == "m" }
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        (field.call(environment) as MutableMap<String, String>)[key] = value
    }

    override suspend fun afterEach(testCase: TestCase, result: TestResult) {
        propertiesFile.delete()
        hoconFile.delete()
    }
}
