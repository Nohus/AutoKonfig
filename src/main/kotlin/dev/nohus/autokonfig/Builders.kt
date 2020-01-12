package dev.nohus.autokonfig

import dev.nohus.autokonfig.utils.CommandLineParser
import dev.nohus.autokonfig.utils.SourceUtil
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
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
        withConfigStream(
            file.reader(Charsets.UTF_8),
            "config file at \"${file.normalize().absolutePath}\""
        )
    } catch (e: IOException) {
        throw AutoKonfigException("Failed to read file: ${file.normalize().absolutePath}")
    }
}

fun AutoKonfig.withResourceConfig(resource: String) = apply {
    val stream = ClassLoader.getSystemClassLoader().getResourceAsStream(resource)
        ?: throw AutoKonfigException("Failed to read resource: $resource")
    withConfigStream(stream.reader(), "config file resource at \"$resource\"")
}

fun AutoKonfig.withURLConfig(url: URL) = apply {
    withConfigStream(url.openStream().reader(), "config file at URL: $url")
}

fun AutoKonfig.withURLConfig(url: String) = withURLConfig(URL(url))

private fun AutoKonfig.withConfigStream(config: InputStreamReader, source: String) = apply {
    Properties().apply {
        load(config)
        withProperties(this, source)
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
        if (value != null) addProperty(key, value, "command line parameters")
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
        addProperty(it.key, it.value, source)
    }
}
