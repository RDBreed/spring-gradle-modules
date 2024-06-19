plugins {
    id("java")
}

group = "eu.phaf"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":stateman-core"))
    compileOnly("org.slf4j:slf4j-api:2.0.13")
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.0")
    testImplementation(platform("org.testcontainers:testcontainers-bom:1.19.8")) //import bom
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testCompileOnly(project(":stateman-core"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:postgresql")
}

tasks.test {
    useJUnitPlatform()
}