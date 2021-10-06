
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

object LibraryVersions {
    const val kotlin = "1.5.30"
    const val typeSafeConfig = "1.4.1"
}

buildscript {
    repositories {
        maven("https://plugins.gradle.org/m2/")
    }
    dependencies {
    }
}

plugins {
    kotlin("jvm") version "1.5.30"
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    id("com.diffplug.spotless") version "5.15.0"
    id("org.jetbrains.dokka") version "1.5.0"
}

apply {
    from("${rootDir}/publish-root.gradle")
    from("${rootDir}/publish-module.gradle")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib", LibraryVersions.kotlin))
    implementation(kotlin("reflect", LibraryVersions.kotlin))
    implementation("com.typesafe:config:${LibraryVersions.typeSafeConfig}")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.test {
    useJUnitPlatform()
}

spotless {
    kotlin {
        ktlint("0.42.1")
    }
}
