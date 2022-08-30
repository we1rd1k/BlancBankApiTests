import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.0"
    id("io.qameta.allure").version("2.9.6")
    application
}

group = "me.denni"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val logbackVersion = "1.2.3"
val aeonbitsVersion = "1.0.12"
val junitVersion = "5.8.2"
val fuelVersion = "2.3.1"
val reflectVersion = "1.4.32"
val generaxVersion = "1.0.2"
val jsonPathVersion = "2.0.0"
val jsoupVersion = "1.14.3"
val csvVersion = "1.9.0"
val jacksonVersion = "2.13.0"
val awaitilityVersion = "4.1.0"
val kotlinLogging = "1.12.5"
val allureVersion = "2.17.2"

dependencies {
    testImplementation(kotlin("test"))
    implementation(platform("org.junit:junit-bom:$junitVersion"))
    implementation("org.junit.jupiter:junit-jupiter")
    implementation("org.aeonbits.owner:owner:$aeonbitsVersion")
    implementation("io.github.microutils:kotlin-logging:$kotlinLogging")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.github.serpro69:kotlin-faker:1.8.0")
    implementation("com.github.javafaker:javafaker:1.0.2")
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.github.kittinunf.fuel:fuel:$fuelVersion")
    implementation("com.github.kittinunf.fuel:fuel-jackson:$fuelVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$reflectVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
    implementation("org.awaitility:awaitility-kotlin:$awaitilityVersion")
    implementation("com.github.mifmif:generex:$generaxVersion")
    implementation("com.nfeld.jsonpathkt:jsonpathkt:$jsonPathVersion")
    implementation("org.jsoup:jsoup:$jsoupVersion")
    implementation("org.apache.commons:commons-csv:$csvVersion")
    implementation("net.pwall.json:json-kotlin-schema:0.29")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    testImplementation("io.qameta.allure:allure-junit5:$allureVersion")
    implementation("io.qameta.allure:allure-java-commons:$allureVersion")
}

allure {
    version.set(allureVersion)
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "14"
}

application {
    mainClass.set("MainKt")
}

val runApiTestsTask = tasks.register<Test>("runApiTestSet") {
    useJUnitPlatform {
        includeTags("API")
    }
}