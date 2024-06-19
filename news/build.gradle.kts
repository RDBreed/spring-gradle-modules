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
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation(project(":openapi"))
    implementation(project(":stateman-core"))
    implementation(project(":stateman-spring"))
    // needed for above!
    implementation("org.aspectj:aspectjweaver:1.9.22.1")
    implementation("org.springframework:spring-aop")

    implementation(project(":stateman-postgres"))
    // apache text utilities for StringSubstitutor
    testImplementation("org.apache.commons:commons-text:1.10.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test"){
        exclude("junit", "junit")
    }
    testImplementation("io.projectreactor:reactor-test")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(libs.bundles.wiremockSpring)
    // Json assertj assertions
    testImplementation("net.javacrumbs.json-unit:json-unit-assertj:3.0.0")

    // checking postgres testcontainers for stateman
    testImplementation(platform("org.testcontainers:testcontainers-bom:1.19.8")) //import bom
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:postgresql")
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