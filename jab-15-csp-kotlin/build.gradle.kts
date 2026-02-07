plugins {
    kotlin("jvm") version "2.2.21"
}

group = "com.seweryn-tasior"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation(fileTree("libs") { include("*.jar") })
}

kotlin {
    jvmToolchain(24)
}

tasks.test {
    useJUnitPlatform()
}
