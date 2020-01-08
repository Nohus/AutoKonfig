package dev.nohus.autokonfig

/**
 * Created by Marcin Wisniowski (Nohus) on 05/01/2020.
 */

internal val DefaultAutoKonfig by lazy {
    AutoKonfig()
        .withSystemProperties()
        .withEnvironmentVariables()
        .withConfigs(ConfigFileLocator().getConfigFiles())
}
