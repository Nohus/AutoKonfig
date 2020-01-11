package dev.nohus.autokonfig

internal class SettingParseException(val reason: String, cause: Throwable) : RuntimeException(cause)
