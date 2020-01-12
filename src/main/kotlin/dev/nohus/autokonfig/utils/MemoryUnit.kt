package dev.nohus.autokonfig.utils;

import java.math.BigInteger

/**
 * Created by Marcin Wisniowski (Nohus) on 12/01/2020.
 */

/**
 * Adapted from Typesafe Config
 */
internal enum class MemoryUnit(val prefix: String, val powerOf: Int, val power: Int) {
    BYTES("", 1024, 0),
    KILOBYTES("kilo", 1000, 1),
    MEGABYTES("mega", 1000, 2),
    GIGABYTES("giga", 1000, 3),
    TERABYTES("tera", 1000, 4),
    PETABYTES("peta", 1000, 5),
    EXABYTES("exa", 1000, 6),
    ZETTABYTES("zetta", 1000, 7),
    YOTTABYTES("yotta", 1000, 8),
    KIBIBYTES("kibi", 1024, 1),
    MEBIBYTES("mebi", 1024, 2),
    GIBIBYTES("gibi", 1024, 3),
    TEBIBYTES("tebi", 1024, 4),
    PEBIBYTES("pebi", 1024, 5),
    EXBIBYTES("exbi", 1024, 6),
    ZEBIBYTES("zebi", 1024, 7),
    YOBIBYTES("yobi", 1024, 8);

    val bytes: BigInteger = BigInteger.valueOf(powerOf.toLong()).pow(power)

    companion object {
        val unitsMap by lazy { createUnitsMap() }

        private fun createUnitsMap(): Map<List<String>, MemoryUnit> {
            return values().map { unit ->
                val units = mutableListOf<String>()
                units += unit.prefix + "byte"
                units += unit.prefix + "bytes"
                if (unit.prefix.isEmpty()) {
                    units += "b"
                    units += "B"
                    units += ""
                } else {
                    val first = unit.prefix.substring(0, 1)
                    val firstUpper = first.toUpperCase()
                    when (unit.powerOf) {
                        1024 -> {
                            units += first // 512m
                            units += firstUpper // 512M
                            units += firstUpper + "i" // 512Mi
                            units += firstUpper + "iB" // 512MiB
                        }
                        1000 -> {
                            units += if (unit.power == 1) {
                                first + "B" // 512kB
                            } else {
                                firstUpper + "B" // 512MB
                            }
                        }
                    }
                }
                units to unit
            }.toMap()
        }
    }
}
