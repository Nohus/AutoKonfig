package dev.nohus.autokonfig

/**
 * Created by Marcin Wisniowski (Nohus) on 06/01/2020.
 */

class CommandLineParser {

    fun parse(args: Array<String>): Map<String, String?> {
        val map = mutableMapOf<String, String?>()
        val arguments = args.toList()
        var index = 0
        while (index <= arguments.lastIndex) {
            map[arguments[index].dropWhile { it == '-' }] = getValue(arguments, index)
            index = getNextIndex(arguments, index) ?: break
        }
        return map
    }

    private fun getNextIndex(args: List<String>, from: Int): Int? {
        val index =  args.drop(from + 1).indexOfFirst { it.startsWith("-") }
        return if (index != -1) index + from + 1
        else null
    }

    private fun getValue(args: List<String>, index: Int): String? {
        val values = args.drop(index + 1).takeWhile { !it.startsWith("-") }
        return if (values.isNotEmpty()) values[0]
        else null
    }
}
