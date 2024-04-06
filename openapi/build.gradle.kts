plugins {
    id("java")
    id("eu.phaf.gradle.plugins")
}

group = "eu.phaf"
version = "1.0.0"

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

tasks.withType<JavaCompile>().configureEach{
    dependsOn(tasks.findByName("openApiGenerateAll"))
}