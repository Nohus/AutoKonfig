package dev.nohus.autokonfig

import java.io.File

/**
 * Created by Marcin Wisniowski (Nohus) on 05/01/2020.
 */

class ConfigFileLocator(private val basePath: String = ".") {

    companion object {
        private val VALID_EXTENSIONS = listOf("properties", "conf", "config", "ini", "txt")
    }

    fun getConfigFiles(): List<File> {
        return getReadableFiles().filter { it.extension in VALID_EXTENSIONS }
    }

    private fun getReadableFiles(): List<File> {
        return File(basePath).listFiles()?.filter {
            it.isFile && it.canRead()
        } ?: emptyList()
    }
}
