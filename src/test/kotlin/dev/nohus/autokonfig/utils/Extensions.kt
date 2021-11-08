package dev.nohus.autokonfig.utils

fun String.useAsProperties(testAutoKonfig: TestAutoKonfig) {
    testAutoKonfig.useAsProperties(trimIndent())
}

fun String.useAsHocon(testAutoKonfig: TestAutoKonfig) {
    testAutoKonfig.useAsHocon(trimIndent())
}
