val springCloudVersion: String by project
val springKafkaVersion: String by project
val postgresqlVersion: String by project
val wiremockTestcontainersVersion: String by project
val junitPlatformLauncherVersion: String by project
val mockitoCoreVersion: String by project
val archunitVersion: String by project
val openFeignVersion: String by project
val springBootTestcontainersVersion: String by project
val testcontainersVersion: String by project

plugins {
	java
	id("org.springframework.boot")
	id("io.spring.dependency-management")
}

group = "fun.vs.fw"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
	implementation("org.springframework.kafka:spring-kafka:")
	implementation("org.postgresql:postgresql:$postgresqlVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:kafka")
    testImplementation("org.wiremock.integrations.testcontainers:wiremock-testcontainers-module:$wiremockTestcontainersVersion")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("org.mockito:mockito-core:$mockitoCoreVersion")
	testImplementation("com.tngtech.archunit:archunit:$archunitVersion")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
