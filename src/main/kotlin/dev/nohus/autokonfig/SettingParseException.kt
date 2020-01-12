package dev.nohus.autokonfig

/**
 * Created by Marcin Wisniowski (Nohus) on 12/01/2020.
 */

internal class SettingParseException(val reason: String, cause: Throwable? = null) : RuntimeException(cause)
