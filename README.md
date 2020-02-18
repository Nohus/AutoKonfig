# AutoKonfig

[![AutoKonfig](https://autokonfig.nohus.dev/images/AutoKonfig.png)](https://autokonfig.nohus.dev/)

Kotlin configuration library with batteries included.

[![License](https://img.shields.io/badge/license-Apache%202%20-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)
[![Coverage](https://img.shields.io/badge/coverage-100%25-brightgreen)](https://github.com/Nohus/AutoKonfig/tree/master/src/test/kotlin/dev/nohus/autokonfig)
[![Version](https://img.shields.io/bintray/v/nohus/Nohus/AutoKonfig)]()

## Website
[https://autokonfig.nohus.dev/](https://autokonfig.nohus.dev/)

## Features overview
- Support for JSON, [HOCON](https://autokonfig.nohus.dev/hocon) and Java properties config files
- Loading config files from resources and remote URLs
- Reading configuration from system properties and environment variables
- Parsing command-line parameters
- Merging properties loaded from multiple sources
- Automatically finding config files
- Type-safe properties
- Many useful [property types](https://autokonfig.nohus.dev/types), including dates (`2020-02-02`), times (`10:15:30`), durations (`20s`) and memory sizes (`256 MB`)
- Type-specific parsing, a value of `1` can be the string `"1"`, the integer `1`, or the boolean `true`
depending on which type is asked for
- Collection types
- 100% unit test coverage

## Quick start

#### Gradle
``` Groovy
implementation "dev.nohus:AutoKonfig:1.0.0"
```

#### Maven
``` XML
<dependency>
    <groupId>dev.nohus</groupId>
    <artifactId>AutoKonfig</artifactId>
    <version>1.0.0</version>
</dependency>
```

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

That's it! AutoKonfig automatically loaded your config file, because it had a [well-known name](https://autokonfig.nohus.dev/#supported-file-types). It knew which properties to load based on the variable names, and it mapped them to types based on the specified `StringSetting` and `IntSetting` delegates.

To see more, [continue reading on the website](https://autokonfig.nohus.dev/).
