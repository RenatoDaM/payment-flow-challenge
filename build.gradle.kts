plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.payment"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

subprojects {
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "kotlin")
    group = "com.cinema"
    version = "0.0.1-SNAPSHOT"
    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
        implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
        implementation("org.springframework.boot:spring-boot-starter-webflux")
        implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
        implementation("org.springframework.kafka:spring-kafka:3.3.2")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.2")
        implementation("org.springframework.hateoas:spring-hateoas:3.0.0-M1")
        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
        annotationProcessor("org.projectlombok:lombok")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("io.projectreactor:reactor-test")
        testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
        testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
        testImplementation("org.springframework.boot:spring-boot-testcontainers:3.4.2")
        testImplementation("org.testcontainers:postgresql:1.20.4")
        testImplementation("org.testcontainers:r2dbc:1.20.4")
        testImplementation("org.testcontainers:junit-jupiter:1.20.4")
        testImplementation("org.testcontainers:kafka:1.20.4")
        testImplementation("org.springframework.kafka:spring-kafka-test:3.3.2")
        implementation("com.maciejwalkowiak.spring:wiremock-spring-boot:2.1.3")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }
}


kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
