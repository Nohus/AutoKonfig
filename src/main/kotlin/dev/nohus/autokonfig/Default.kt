package dev.nohus.autokonfig

import dev.nohus.autokonfig.utils.SourceUtil
import java.io.File
import java.net.URL
import java.util.*

/**
 * Created by Marcin Wisniowski (Nohus) on 05/01/2020.
 */

internal val DefaultAutoKonfig by lazy {
    AutoKonfig().withDefaults()
}

fun AutoKonfig.Companion.clear() = DefaultAutoKonfig.clear()
fun AutoKonfig.Companion.getAll() = DefaultAutoKonfig.getAll()
fun AutoKonfig.Companion.getKeySource(key: String) = DefaultAutoKonfig.getKeySource(key)
fun AutoKonfig.Companion.withConfigs(vararg files: File) = DefaultAutoKonfig.withConfigs(*files)
fun AutoKonfig.Companion.withConfigs(files: List<File>) = DefaultAutoKonfig.withConfigs(files)
fun AutoKonfig.Companion.withConfig(file: File) = DefaultAutoKonfig.withConfig(file)
fun AutoKonfig.Companion.withResourceConfig(resource: String) = DefaultAutoKonfig.withResourceConfig(resource)
fun AutoKonfig.Companion.withURLConfig(url: URL) = DefaultAutoKonfig.withURLConfig(url)
fun AutoKonfig.Companion.withURLConfig(url: String) = DefaultAutoKonfig.withURLConfig(url)
fun AutoKonfig.Companion.withEnvironmentVariables() = DefaultAutoKonfig.withEnvironmentVariables()
fun AutoKonfig.Companion.withSystemProperties() = DefaultAutoKonfig.withSystemProperties()
fun AutoKonfig.Companion.withCommandLineArguments(args: Array<String>) = DefaultAutoKonfig.withCommandLineArguments(args)
fun AutoKonfig.Companion.withProperties(
    properties: Properties,
    source: String = SourceUtil.getReflectiveSource("properties")
) = DefaultAutoKonfig.withProperties(properties, source)
fun AutoKonfig.Companion.withMap(
    map: Map<String, String>,
    source: String = SourceUtil.getReflectiveSource("a map")
) = DefaultAutoKonfig.withMap(map, source)
