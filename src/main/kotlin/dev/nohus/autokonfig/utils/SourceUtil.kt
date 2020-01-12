package dev.nohus.autokonfig.utils

import dev.nohus.autokonfig.AutoKonfig

/**
 * Created by Marcin Wisniowski (Nohus) on 12/01/2020.
 */

object SourceUtil {

    fun getReflectiveSource(partialSource: String): String {
        val frame = getExternalStackFrame()
        return "$partialSource inserted by ${frame.className}#${frame.methodName} in ${frame.fileName}:${frame.lineNumber}"
    }

    private fun getExternalStackFrame(): StackTraceElement {
        val internalNames = listOf(AutoKonfig::class.java.packageName, "java.", "jdk.", "kotlin.")
        return Thread.currentThread().stackTrace.first {
                frame -> !internalNames.any { frame.className.startsWith(it) }
        }
    }
}
