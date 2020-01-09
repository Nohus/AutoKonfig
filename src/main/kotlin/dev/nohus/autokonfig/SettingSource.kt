package dev.nohus.autokonfig

/**
 * Created by Marcin Wisniowski (Nohus) on 08/01/2020.
 */

data class SettingSource(val source: String) {

    companion object {
        fun reflective(partialSource: String): SettingSource {
            val frame = getExternalStackFrame()
            return SettingSource("$partialSource inserted by ${frame.className}#${frame.methodName} in ${frame.fileName}:${frame.lineNumber}")
        }

        private fun getExternalStackFrame(): StackTraceElement {
            val internalNames = listOf(SettingSource::class.java.packageName, "java.", "jdk.", "kotlin.")
            return Thread.currentThread().stackTrace.first {
                    frame -> !internalNames.any { frame.className.startsWith(it) }
            }
        }
    }
}
