package dev.nohus.autokonfig

import java.io.File
import java.io.IOException
import java.util.*

/**
 * Created by Marcin Wisniowski (Nohus) on 06/01/2020.
 */

fun AutoKonfig.withConfigs(vararg files: File) = apply {
    withConfigs(files.toList())
}

fun AutoKonfig.withConfigs(files: List<File>) = apply {
    files.forEach { withConfig(it) }
}

fun AutoKonfig.withConfig(file: File) = apply {
    try {
        Properties().apply {
            load(file.reader(Charsets.UTF_8))
            withProperties(this)
        }
    } catch (e: IOException) {
        throw AutoKonfigException("Failed to read file: ${file.normalize().absolutePath}")
    }
}

fun AutoKonfig.withResourceConfig(resource: String) = apply {
    val stream = ClassLoader.getSystemClassLoader().getResourceAsStream(resource)
        ?: throw AutoKonfigException("Failed to read resource: $resource")
    withProperties(Properties().apply { load(stream) })
}

fun AutoKonfig.withEnvironmentVariables() = apply {
    withMap(System.getenv())
}

fun AutoKonfig.withSystemProperties() = apply {
    withProperties(System.getProperties())
}

fun AutoKonfig.withCommandLineArguments(args: Array<String>) = apply {
    CommandLineParser().parse(args).forEach { (key, value) ->
        if (value != null) addProperty(key, value)
        else addFlag(key)
    }
}

fun AutoKonfig.withProperties(properties: Properties) = apply {
    withMap(properties.map { it.key.toString() to it.value.toString() }.toMap())
}

fun AutoKonfig.withMap(map: Map<String, String>) = apply {
    map.entries.forEach {
        addProperty(it.key, it.value)
    }
}
