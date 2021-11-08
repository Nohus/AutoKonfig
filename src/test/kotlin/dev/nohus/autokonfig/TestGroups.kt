package dev.nohus.autokonfig

import dev.nohus.autokonfig.types.Group
import dev.nohus.autokonfig.types.IntSettingType
import dev.nohus.autokonfig.types.StringSettingType

object groupA : Group() {
    object subgroup : Group() {
        val setting by StringSetting("")
    }
}

object groupB : Group("outer") {
    object subgroup : Group("inner") {
        val setting by StringSetting(name = "key")
    }
}

object groupC : Group("outer") {
    object subgroup : Group() {
        val setting by StringSetting()
    }
}

object TypesGroup : Group() {
    val string by StringSetting()
    val int by IntSetting()
    val long by LongSetting()
    val float by FloatSetting()
    val double by DoubleSetting()
    val bigInteger by BigIntegerSetting()
    val bigDecimal by BigDecimalSetting()
    val bytes by BytesSetting()
    val boolean by BooleanSetting()
    val flag by FlagSetting()
}

enum class Letters {
    Alpha, Beta
}

object EnumGroup : Group() {
    val setting by EnumSetting(Letters::class)
    val settingJava by EnumSetting(Letters::class.java, name = "setting")
}

object Temporal : Group() {
    val instant by InstantSetting()
    val duration by DurationSetting()
    val period by PeriodSetting()
    val localTime by LocalTimeSetting()
    val localDate by LocalDateSetting()
    val localDateTime by LocalDateTimeSetting()
}

object Collections : Group() {
    val strings by ListSetting(StringSettingType)
    val numbers by SetSetting(IntSettingType)
}
