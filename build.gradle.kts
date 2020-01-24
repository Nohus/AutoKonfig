
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

object LibraryVersions {
    const val kotlin = "1.3.61"
    const val typeSafeConfig = "1.4.0"
}

plugins {
    kotlin("jvm") version "1.3.61"
    `maven-publish`
}

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib", LibraryVersions.kotlin))
    implementation(kotlin("reflect", LibraryVersions.kotlin))
    implementation("com.typesafe:config:${LibraryVersions.typeSafeConfig}")
    testImplementation("org.junit.jupiter:junit-jupiter:5.5.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.test {
    useJUnitPlatform()
}

object Setting {
    const val packageName = "dev.nohus.autokonfig"
    const val group = "dev.nohus"
    const val artifact = "AutoKonfig"
    const val version = "0.0.1"
}

group = Setting.group
version = Setting.version

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = Setting.group
            artifactId = Setting.artifact
            version = Setting.version

            from(components["java"])

            pom {
                name.set("AutoKonfig")
                description.set("Kotlin configuration library")
                url.set("https://gitlab.com/Nohus/AutoKonfig")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                scm {
                    connection.set("scm:git:git://gitlab.com/Nohus/AutoKonfig.git")
                    developerConnection.set("scm:git:git://gitlab.com/Nohus/AutoKonfig.git")
                    url.set("https://gitlab.com/Nohus/AutoKonfig")
                }
                developers {
                    developer {
                        id.set("Nohus")
                        name.set("Marcin Wisniowski")
                        url.set("https://nohus.dev/")
                    }
                }
            }
        }
    }
}

tasks.register<Jar>("sourcesJar") {
    from(sourceSets["main"].allSource)
    classifier = "sources"
}

tasks.register("copyPom") {
    outputs.upToDateWhen { false }
    doLast {
        copy {
            from("build/publications/maven") {
                include("pom-default.xml")
            }
            into("build/libs/")
            rename("pom-default.xml", "${Setting.artifact}-${Setting.version}.pom")
        }
    }
}

tasks.register("buildArtifacts") {
    this.finalizedBy(tasks.findByPath("jar"))
    this.finalizedBy(tasks.findByPath("sourcesJar"))
    this.finalizedBy(tasks.findByPath("generatePomFileForMavenPublication"))
    this.finalizedBy(tasks.findByPath("copyPom"))
}
