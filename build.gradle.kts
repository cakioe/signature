plugins {
    kotlin("jvm") version "2.0.10"
    id("org.jetbrains.dokka") version "1.9.20"
}

group = "io.github.cakioe"
version = "1.0.8"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.sonatype.central:central-publishing-maven-plugin:0.5.0")
    implementation("com.google.code.gson:gson:2.10.1")

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}
