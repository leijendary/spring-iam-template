plugins {
    id("org.springframework.boot") version "3.0.2"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.barfuin.gradle.jacocolog") version "2.0.0"
    kotlin("jvm") version "1.8.10"
    kotlin("kapt") version "1.8.10"
    kotlin("plugin.spring") version "1.8.10"
    kotlin("plugin.jpa") version "1.8.10"
}

group = "com.leijendary.spring"
version = "1.0.0"
description = "Spring Boot IAM Template for the Microservice Architecture or general purpose"
java.sourceCompatibility = JavaVersion.VERSION_19

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
    testCompileOnly {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(module = "mockito-core")
    }
}

repositories {
    mavenCentral()
}

kapt {
    arguments {
        arg("mapstruct.defaultComponentModel", "spring")
        arg("mapstruct.unmappedTargetPolicy", "IGNORE")
    }
    javacOptions {
        option("--enable-preview")
    }
}

dependencies {
    // Kotlin
    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib-jdk8"))
    testImplementation(kotlin("test"))

    // Kotlinx
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

    // Spring Boot Starter
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Spring Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // Spring Security
    implementation("org.springframework.security:spring-security-crypto")
    testImplementation("org.springframework.security:spring-security-test")

    // Spring Retry
    implementation("org.springframework.retry:spring-retry")

    // Spring Cloud
    implementation("org.springframework.cloud:spring-cloud-context")

    // Spring Kafka
    implementation("org.springframework.kafka:spring-kafka")
    testImplementation("org.springframework.kafka:spring-kafka-test")

    // AWS
    implementation("io.awspring.cloud:spring-cloud-aws-starter-s3")
    implementation("io.awspring.cloud:spring-cloud-aws-starter-ses")
    implementation("io.awspring.cloud:spring-cloud-aws-starter-sns")

    // Database
    implementation("org.postgresql:postgresql")
    implementation("org.liquibase:liquibase-core")

    // OpenAPI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2")

    // MapStruct
    implementation("org.mapstruct:mapstruct:1.5.3.Final")
    kapt("org.mapstruct:mapstruct-processor:1.5.3.Final")
    testImplementation("org.mapstruct:mapstruct-processor:1.5.3.Final")

    // Crypto
    implementation("org.bouncycastle:bcprov-jdk18on:1.72")

    // Devtools
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    kapt("org.springframework.boot:spring-boot-configuration-processor")

    // Tracing
    implementation("com.github.loki4j:loki-logback-appender:1.4.0-m1")
    implementation("io.github.openfeign:feign-micrometer")
    implementation("io.micrometer:micrometer-observation")
    implementation("io.micrometer:micrometer-tracing-bridge-otel")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.opentelemetry:opentelemetry-exporter-zipkin")
    implementation("net.ttddyy.observation:datasource-micrometer-spring-boot:1.0.1")

    // Test
    testImplementation("com.ninja-squad:springmockk:4.0.0")

    // Test Containers
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:kafka")
    testImplementation("org.testcontainers:postgresql")
}

dependencyManagement {
    imports {
        mavenBom("io.awspring.cloud:spring-cloud-aws-dependencies:3.0.0-RC1")
        mavenBom("io.micrometer:micrometer-tracing-bom:1.0.1")
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2022.0.1")
        mavenBom("org.testcontainers:testcontainers-bom:1.17.6")
    }
}

tasks {
    compileKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=all", "-Xjvm-enable-preview")
            jvmTarget = "19"
        }
    }

    compileJava {
        options.compilerArgs.add("--enable-preview")
    }

    bootJar {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    jar {
        enabled = false
    }

    test {
        jvmArgs = listOf("-XX:+AllowRedefinitionToAddDeleteMethods", "--enable-preview")
        useJUnitPlatform()
        finalizedBy(jacocoTestReport)
    }

    jacocoTestReport {
        dependsOn(test)
    }

    processResources {
        filesMatching("application.yaml") {
            expand(project.properties)
        }
    }
}