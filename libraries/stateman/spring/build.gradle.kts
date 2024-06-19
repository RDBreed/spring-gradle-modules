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
    compileOnly("org.springframework.boot:spring-boot-autoconfigure:3.3.0")
    compileOnly("io.projectreactor:reactor-core:3.6.4")
    compileOnly("org.aspectj:aspectjweaver:1.9.22.1")
    compileOnly("jakarta.annotation:jakarta.annotation-api:3.0.0")
    // get annotated methods
    compileOnly("org.apache.commons:commons-lang3:3.14.0")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}