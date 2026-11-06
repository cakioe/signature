plugins {
    kotlin("jvm") version "2.0.10"
    id("org.jetbrains.dokka") version "1.9.20"
}

group = "io.github.cakioe"
version = "1.0.13"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.sonatype.central:central-publishing-maven-plugin:0.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("io.github.erdtman:java-json-canonicalization:1.1")
    implementation("junit:junit:4.12")

    testImplementation(kotlin("test"))
    testImplementation("junit:junit:4.13.2")
}

kotlin {
    jvmToolchain(17)
}
