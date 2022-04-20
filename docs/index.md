# AutoKonfig

![AutoKonfig](https://autokonfig.nohus.dev/images/AutoKonfig.png)

Kotlin configuration library with batteries included.

[![License](https://img.shields.io/badge/license-Apache%202%20-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)
[![Coverage](https://img.shields.io/badge/coverage-100%25-brightgreen)](https://github.com/Nohus/AutoKonfig/tree/master/src/test/kotlin/dev/nohus/autokonfig)
[![Version](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepo1.maven.org%2Fmaven2%2Fdev%2Fnohus%2FAutoKonfig%2Fmaven-metadata.xml)](https://search.maven.org/artifact/dev.nohus/AutoKonfig)

## Features overview
- Support for JSON, [HOCON](/hocon) and Java properties config files
- Loading config files from resources and remote URLs
- Reading configuration from system properties and environment variables
- Parsing command-line parameters
- Merging properties loaded from multiple sources
- Automatically finding config files
- Type-safe properties
- Many useful [property types](/types), including dates (`2020-02-02`), times (`10:15:30`), durations (`20s`) and memory sizes (`256 MB`)
- Type-specific parsing, a value of `1` can be the string `"1"`, the integer `1`, or the boolean `true`
depending on which type is asked for
- Collection types
- 100% unit test coverage

## Quick start

#### Gradle
``` Groovy
implementation "dev.nohus:AutoKonfig:1.0.4"
```

#### Maven
``` XML
<dependency>
    <groupId>dev.nohus</groupId>
    <artifactId>AutoKonfig</artifactId>
    <version>1.0.4</version>
</dependency>
```

The artifacts are available on Maven Central.

## Simple example

Create a config file:

#### app.conf
``` Lighttpd
host = nohus.dev
port = 80
```

Create variables for your properties:

#### Main.kt
``` Kotlin
fun main() {
    val host by StringSetting()
    val port by IntSetting()
    println("Host: $host, port: $port")
}
```

That's it! AutoKonfig automatically loaded your config file, because it had a [well-known name](#supported-file-types). It knew which properties to load based on the variable names, and it mapped them to types based on the specified `StringSetting` and `IntSetting` delegates.

!!! tip
    If you need simple global access to your configuration, declare an `object Settings` with your properties inside, and then use `Settings.host` or similar anywhere you need.

## Property names

Property names are case insensitive, AutoKonfig will also figure out mappings between `camelCase`, `snake_case`, and `kebab-case`.

``` Kotlin
val httpPort by IntSetting()
```

All of the following keys would be matched to this property:

``` Lighttpd
httpPort = 80
http-port = 80
http_port = 80
HTTP_PORT = 80
```

!!! info
    If a config file actually contains multiple case versions of the same key, the exact match will be chosen first.

You can explicitly specify the key name if you don't want to rely on the variable name:

``` Kotlin
val port by IntSetting(name = "http-port")
```

It is also possible to read a property by name without declaring a variable:

``` Kotlin
AutoKonfig.getInt("http-port") // Returns 80
```

Similar `get` methods exist for all [types](/types).

!!! info
    It is possible to retrieve all known properties without knowing their names by calling `AutoKonfig.getAll()`, which returns a `Map<String, String>`. This can be used to pass settings "as is" to other systems, but keep in mind that it is not type-safe.

## Missing properties

Trying to read a property which is missing will result in an `AutoKonfigException` being thrown with an appropriate
message. Declaring a variable delegated to a missing property will fail-fast. The variable is guaranteed to work at the point it's used.

## Default values

Missing properties can be allowed by specifying default values:

``` Kotlin
val port by IntSetting(default = 8080)
```

If the `port` property does not exist, the variable will return `8080` instead of throwing an exception.

## Setting groups

Properties can be organised in hierachical groups:

``` Lighttpd
http {
    host = nohus.dev
    port = 80
}
```

They can be accessed with dot notation:

``` Kotlin
val port by IntSetting(name = "http.port")
```

The dot notation can also be used in config files. The following file is equivalent:

``` Lighttpd
http.host = nohus.dev
http.port = 80
```

Variables can also be placed in `Group` objects, and AutoKonfig will figure out their location:

``` Kotlin
object Http : Group() {
    val host by StringSetting()
    val port by StringSetting()
}
```

You can then access `Http.port` in your code, and it will contain the expected value. The group objects can be freely nested, to represent the structure of your config files.

!!! info
    Groups can be explicitly named: `object Network : Group("http")`.

## Loading configs

By default, config properties will be loaded from [discovered files](#supported-file-types), Java system properties, and environment variables.

You can manually load more properties using the `with` family of methods:

``` Kotlin
AutoKonfig
    .withConfigs("app.conf", "server.conf") // Files by name
    .withConfig(file) // File by handle
    .withResourceConfig("internal.conf") // Resource file by name
    .withURLConfig(url) // Remote config file
    .withEnvironmentVariables() // Environment variables
    .withSystemProperties() // System properties
```

The methods can be chained, and properties from all sources will be merged together, with the latter overriding the former in case the keys are the same. There are also `withProperties` and `withMap` methods, that allow to you to manually insert a Java `Properties` object or a simple key-value map (`Map<String, String>`). This allows you to easily use the library with an external configuration source, like a database.

!!! tip
    If you don't want to use properties loaded by default, call `AutoKonfig.clear()`. It can be chained just before your `with` methods.

## Supported file types

AutoKonfig supports the [HOCON](/hocon) format. JSON, Java properties and ini-style formats are valid HOCON, and so are also supported.

The autodiscovery of config files will attempt to read all files with the combination of names `autokonfig`, `config`, `app`, `application`, and extensions `.conf`, `.json`, `.properties` in the working directory of your program. For example: `app.conf`, `config.json`, etc. Of course, files with any extensions can be [loaded manually](#loading-configs) as long as they have valid contents.

## Command-line parameters

The library can also parse command-line parameters, just pass it the `args` array from your main method:

``` Kotlin
fun main(args: Array<String>) {
    AutoKonfig.withCommandLineArguments(args)
}
```

Short style (`-a`) and long style (`--version`) arguments are supported, both containing values and used as flags. For example:

```
[program] --verbose --duration 10s -l 12.5
```

``` Kotlin
val verbose by FlagSetting()
val duration by DurationSetting()
val length by FloatSetting(name = "l")
```

!!! info
    All [types](/types) work as normal on the command line, but remember that you need to quote values containing spaces in most shells: `--body "Baltic Sea"`.

## Property source tracing

You can verify where a key was loaded from by calling `AutoKonfig.getKeySource("httpPort")`, which will return a description of it's origin.

Examples:

```
Key "httpPort" was read as "HTTP_PORT" from config file at "path/to/file.conf"
Key "home" was read as "HOME" from environment variables
Key "verbose" was read from command line parameters
Key "missing" not found
```

## AutoKonfig objects

The `AutoKonfig` class stores loaded properties and provides methods to access them. The library automatically creates a default one, and it is the one you are actually using when calling static methods on `AutoKonfig`. It is also implicitly used when declaring delegated variables. While this is convenient for most use cases, it is also possible to create your own instances of `AutoKonfig`:

``` Kotlin
val config = AutoKonfig()
```

It will start empty until you [load some properties](#loading-configs). To initialize it similarly to the default one, call `withDefaults()`, or `withDiscoveredConfigs()` to use just the [config file autodiscovery](#supported-file-types).

Then you can use it as follows:

``` Kotlin
val httpPort by config.IntSetting()
```

or

``` Kotlin
val port = config.getInt("httpPort")
```

!!! tip
    This is especially useful if you have multiple config files with the same structure, for example to configure multiple instances of an entity.
