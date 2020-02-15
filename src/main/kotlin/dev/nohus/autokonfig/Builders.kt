package dev.nohus.autokonfig

import com.typesafe.config.*
import com.typesafe.config.ConfigValueType.*
import dev.nohus.autokonfig.Value.ComplexValue
import dev.nohus.autokonfig.Value.SimpleValue
import dev.nohus.autokonfig.utils.CommandLineParser
import dev.nohus.autokonfig.utils.ConfigFileLocator
import dev.nohus.autokonfig.utils.SourceUtil
import java.io.File
import java.net.MalformedURLException
import java.net.URL
import java.util.*

/**
 * Created by Marcin Wisniowski (Nohus) on 06/01/2020.
 */

fun AutoKonfig.withDefaults() = apply {
    withSystemProperties()
    withEnvironmentVariables()
    withDiscoveredConfigs()
}

fun AutoKonfig.withDiscoveredConfigs() = apply {
    withConfigs(ConfigFileLocator().getConfigFiles())
}

fun AutoKonfig.withConfigs(vararg files: File) = apply {
    withConfigs(files.toList())
}

fun AutoKonfig.withConfigs(files: List<File>) = apply {
    files.reversed().forEach { withConfig(it) }
}

fun AutoKonfig.withConfig(file: File) = apply {
    try {
        val config = ConfigFactory.parseFile(file, ConfigParseOptions.defaults().setAllowMissing(false))
        withConfig(config, "config file at \"${file.normalize().absolutePath}\"")
    } catch (e: ConfigException) {
        throw AutoKonfigException("Failed to read file: ${file.normalize().absolutePath}\n${e.message}")
    }
}

fun AutoKonfig.withResourceConfig(resource: String) = apply {
    try {
        val config = ConfigFactory.parseResources(resource, ConfigParseOptions.defaults().setAllowMissing(false))
        withConfig(config, "config file resource at \"$resource\"")
    } catch (e: ConfigException) {
        throw AutoKonfigException("Failed to read resource: $resource\n${e.message}")
    }
}

fun AutoKonfig.withURLConfig(url: URL) = apply {
    try {
        val config = ConfigFactory.parseURL(url, ConfigParseOptions.defaults().setAllowMissing(false))
        withConfig(config, "config file at URL: $url")
    } catch (e: ConfigException) {
        throw AutoKonfigException("Failed to read URL: $url\n${e.message}")
    }
}

fun AutoKonfig.withURLConfig(url: String) {
    try {
        withURLConfig(URL(url))
    } catch (e: MalformedURLException) {
        throw AutoKonfigException("Failed to read malformed URL: $url (${e.message})")
    }
}

private fun AutoKonfig.withConfig(config: Config, source: String) = apply {
    val resolved = config.resolve(ConfigResolveOptions.noSystem())
    resolved.entrySet()
        .map {
            val cleanedKey = if (it.key.startsWith("\"") && it.key.endsWith("\"")) {
                it.key.removePrefix("\"").removeSuffix("\"")
            } else it.key

            val configValue = resolved.getValue(it.key)
            val value = when (configValue.valueType()) {
                OBJECT -> ComplexValue(configValue, ComplexType.OBJECT)
                LIST -> ComplexValue(configValue, ComplexType.LIST)
                NUMBER, BOOLEAN, NULL, STRING, null -> SimpleValue(resolved.getString(it.key))
            }

            cleanedKey to value
        }
        .forEach { (key, value) ->
            addProperty(key, value, source)
        }
}

fun AutoKonfig.withEnvironmentVariables() = apply {
    withMap(System.getenv(), "environment variables")
}

fun AutoKonfig.withSystemProperties() = apply {
    withProperties(System.getProperties(), "system properties")
}

fun AutoKonfig.withCommandLineArguments(args: Array<String>) = apply {
    CommandLineParser().parse(args).forEach { (key, value) ->
        if (value != null) addProperty(key, SimpleValue(value), "command line parameters")
        else addFlag(key, "command line parameters")
    }
}

fun AutoKonfig.withProperties(
    properties: Properties,
    source: String = SourceUtil.getReflectiveSource("properties")
) = apply {
    withMap(properties.map { it.key.toString() to it.value.toString() }.toMap(), source)
}

fun AutoKonfig.withMap(
    map: Map<String, String>,
    source: String = SourceUtil.getReflectiveSource("a map")
) = apply {
    map.entries.forEach {
        addProperty(it.key, SimpleValue(it.value), source)
    }
}
