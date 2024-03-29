package dev.nohus.autokonfig

import java.util.Locale

/**
 * Created by Marcin Wisniowski (Nohus) on 15/02/2020.
 */

enum class ComplexType {
    OBJECT, LIST;

    override fun toString(): String {
        return super.toString().lowercase(Locale.US)
    }
}
