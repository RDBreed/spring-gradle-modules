buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.23")
    }
}

plugins {
    id("java-gradle-plugin")
    kotlin("jvm") version "1.9.23"
}

group = "eu.phaf"
version = "1.0.0"

repositories {
    mavenCentral()
}
dependencies {
    implementation(gradleApi())
    implementation("org.openapitools:openapi-generator-gradle-plugin:7.0.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.23")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}
kotlin {
    jvmToolchain(21)
}
gradlePlugin {
    plugins {
        create("registerOpenApiTasksPlugin") {
            id = "eu.phaf.gradle.plugins"
            implementationClass = "eu.phaf.gradle.plugins.RegisterOpenApiTasksPlugin"
        }
    }
}

