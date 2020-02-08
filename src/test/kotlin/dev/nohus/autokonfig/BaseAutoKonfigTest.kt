package dev.nohus.autokonfig

import dev.nohus.autokonfig.utils.ConfigFileLocator
import org.junit.jupiter.api.AfterEach
import java.io.File
import kotlin.reflect.jvm.isAccessible

/**
 * Created by Marcin Wisniowski (Nohus) on 08/02/2020.
 */

open class BaseAutoKonfigTest {

    protected val file = File("test.properties")
    private val hoconFile = File("test.conf")

    protected fun String.useAsProperties() {
        file.writeText(this)
        resetDefaultAutoKonfig()
    }

    protected fun String.useAsHocon() {
        hoconFile.writeText(this)
        resetDefaultAutoKonfig()
    }

    protected fun resetDefaultAutoKonfig() {
        DefaultAutoKonfig
            .clear()
            .withSystemProperties()
            .withEnvironmentVariables()
            .withConfigs(ConfigFileLocator().getConfigFiles())
    }

    protected fun setEnvironmentVariable(key: String, value: String) {
        val environment = System.getenv()
        val field = environment::class.members.first { it.name == "m" }
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        (field.call(environment) as MutableMap<String, String>)[key] = value
    }

    @AfterEach
    fun tearDown() {
        file.delete()
        hoconFile.delete()
    }
}
