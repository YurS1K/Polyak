plugins {
    id("java")
    id("pmd")
    id("jacoco")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

pmd {
    ruleSetFiles = files("config/pmd/ruleset.xml")
}

tasks.test {
    useJUnitPlatform()
}