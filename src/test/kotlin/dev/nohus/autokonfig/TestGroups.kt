package dev.nohus.autokonfig

import dev.nohus.autokonfig.types.Group
import dev.nohus.autokonfig.types.IntSettingType
import dev.nohus.autokonfig.types.StringSettingType

object groupA : Group() {
    object subgroup : Group() {
        val setting by StringSetting()
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

object TypesGroupWithDefaults : Group() {
    val string by StringSetting("hello")
    val int by IntSetting(10)
    val long by LongSetting(3000000000)
    val float by FloatSetting(3.14f)
    val double by DoubleSetting(3.1415)
    val bigInteger by BigIntegerSetting("100000000000000000005".toBigInteger())
    val bigDecimal by BigDecimalSetting("100000000000000000005.00000002".toBigDecimal())
    val bytes by BytesSetting(512000)
    val boolean by BooleanSetting(false)
}

object OptionalTypesGroup : Group() {
    val string by OptionalStringSetting()
    val int by OptionalIntSetting()
    val long by OptionalLongSetting()
    val float by OptionalFloatSetting()
    val double by OptionalDoubleSetting()
    val bigInteger by OptionalBigIntegerSetting()
    val bigDecimal by OptionalBigDecimalSetting()
    val bytes by OptionalBytesSetting()
    val boolean by OptionalBooleanSetting()
}

enum class Letters {
    Alpha, Beta
}

object EnumGroup : Group() {
    val setting by EnumSetting(Letters::class)
    val settingJava by EnumSetting(Letters::class.java, name = "setting")
}

object OptionalEnumGroup : Group() {
    val setting by OptionalEnumSetting(Letters::class)
    val settingJava by OptionalEnumSetting(Letters::class.java, name = "setting")
}

object Temporal : Group() {
    val instant by InstantSetting()
    val duration by DurationSetting()
    val period by PeriodSetting()
    val localTime by LocalTimeSetting()
    val localDate by LocalDateSetting()
    val localDateTime by LocalDateTimeSetting()
}

object OptionalTemporal : Group() {
    val instant by OptionalInstantSetting()
    val duration by OptionalDurationSetting()
    val period by OptionalPeriodSetting()
    val localTime by OptionalLocalTimeSetting()
    val localDate by OptionalLocalDateSetting()
    val localDateTime by OptionalLocalDateTimeSetting()
}

object Collections : Group() {
    val strings by ListSetting(StringSettingType)
    val numbers by SetSetting(IntSettingType)
}

object OptionalCollections : Group() {
    val strings by OptionalListSetting(StringSettingType)
    val numbers by OptionalSetSetting(IntSettingType)
}
