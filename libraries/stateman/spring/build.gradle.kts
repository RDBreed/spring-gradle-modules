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
    compileOnly("org.springframework:spring-context:6.1.8")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}