import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.kotlinToolingVersion

val kotlinLoggingVersion: String by project
val springCloudVersion: String by project
val postgresqlVersion: String by project
val konsistVersion: String by project
val arrowVersion: String by project
val wiremockTestcontainersVersion: String by project
val kotestVersion: String by project
val archunitVersion: String by project
val mockkVersion: String by project
val springMockkVersion: String by project
val testcontainersVersion: String by project
val kotlinxSerializationVersion: String by project
val uuidCreatorVersion: String by project
val kotestAssertionsArrowVersion: String by project

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
//    id("org.jetbrains.kotlin.jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("io.kotest")
}

group = "fun.vs.fw"
version = "0.0.1-SNAPSHOT"

kotlin {
    jvmToolchain(25)
    compilerOptions {
        jvmTarget = JvmTarget.JVM_25
        freeCompilerArgs.add("-Xcontext-parameters")
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(25)
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.springframework.boot:spring-boot-starter-kafka")
    implementation("org.postgresql:postgresql:$postgresqlVersion")

    implementation(kotlin("reflect"))
    implementation("io.github.oshai:kotlin-logging-jvm:$kotlinLoggingVersion")
    implementation("io.arrow-kt:arrow-core:$arrowVersion")
    implementation("io.arrow-kt:arrow-core-high-arity:$arrowVersion")
    implementation("io.arrow-kt:arrow-fx-coroutines:$arrowVersion")

    implementation("com.github.f4b6a3:uuid-creator:$uuidCreatorVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:kafka")
    testImplementation("org.wiremock.integrations.testcontainers:wiremock-testcontainers-module:$wiremockTestcontainersVersion")
    
    // Kotest dependencies
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-framework-engine:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-extensions-spring:$kotestVersion")
    testImplementation("io.kotest.extensions:kotest-assertions-arrow:$kotestAssertionsArrowVersion")
    testImplementation("io.kotest:kotest-extensions-testcontainers:$kotestVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("com.ninja-squad:springmockk:$springMockkVersion")
    testImplementation("com.lemonappdev:konsist:$konsistVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinToolingVersion")
    testImplementation("com.tngtech.archunit:archunit-junit5:$archunitVersion")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
        mavenBom("org.testcontainers:testcontainers-bom:$testcontainersVersion")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
