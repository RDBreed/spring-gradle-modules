import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("java")
    alias(libs.plugins.springboot)
    alias(libs.plugins.springbootDependency)
}

group = "eu.phaf"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.getByName<BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}