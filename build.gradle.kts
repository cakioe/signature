plugins {
    kotlin("jvm") version "2.0.10"
    id("org.jetbrains.dokka") version "1.9.20"
}

group = "io.github.cakioe"
version = "1.0.12"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.sonatype.central:central-publishing-maven-plugin:0.5.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("junit:junit:4.12")

    testImplementation(kotlin("test"))
    testImplementation("junit:junit:4.13.2")
    implementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}
