package dev.nohus.autokonfig.utils

import java.io.File

/**
 * Created by Marcin Wisniowski (Nohus) on 05/01/2020.
 */

class ConfigFileLocator(private val basePath: String = ".") {

    companion object {
        private val VALID_EXTENSIONS = listOf("conf", "json", "properties")
        private val VALID_NAMES = listOf("autokonfig", "config", "app", "application")
    }

    fun getConfigFiles(): List<File> {
        return getReadableFiles()
            .filter { it.extension in VALID_EXTENSIONS }
            .filter { it.nameWithoutExtension in VALID_NAMES }
            .sortedBy { it.name }
    }

    private fun getReadableFiles(): List<File> {
        return File(basePath).listFiles()?.filter {
            it.isFile && it.canRead()
        } ?: emptyList()
    }
}
