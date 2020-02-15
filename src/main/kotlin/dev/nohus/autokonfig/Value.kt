package dev.nohus.autokonfig

import com.typesafe.config.ConfigValue
import com.typesafe.config.ConfigValueType.BOOLEAN
import com.typesafe.config.ConfigValueType.LIST
import com.typesafe.config.ConfigValueType.NULL
import com.typesafe.config.ConfigValueType.NUMBER
import com.typesafe.config.ConfigValueType.OBJECT
import com.typesafe.config.ConfigValueType.STRING

/**
 * Created by Marcin Wisniowski (Nohus) on 09/02/2020.
 */

sealed class Value {
    class SimpleValue(val value: String) : Value()
    class ComplexValue(val value: ConfigValue, val type: ComplexType) : Value()

    override fun toString(): String {
        return if (this is SimpleValue) value else (this as ComplexValue).value.unwrapped().toString()
    }

    companion object {

        fun wrap(value: ConfigValue): Value {
            return when (value.valueType()) {
                OBJECT -> ComplexValue(value, ComplexType.OBJECT)
                LIST -> ComplexValue(value, ComplexType.LIST)
                NUMBER, BOOLEAN, NULL, STRING, null -> SimpleValue(value.unwrapped().toString())
            }
        }
    }
}
