import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("java")
    id("eu.phaf.gradle.plugins")
    alias(libs.plugins.springboot)
    alias(libs.plugins.springbootDependency)
}

group = "eu.phaf"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation (libs.bundles.openapi)
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile>().configureEach{
    dependsOn(tasks.findByName("openApiGenerateAll"))
}

tasks.getByName<BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}