plugins {
    id("java")
    application
    checkstyle
    jacoco
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.freefair.lombok") version "8.10"
}

application {
    mainClass.set("hexlet.code.App")
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.javalin:javalin:6.3.0")
    implementation("io.javalin:javalin-bundle:6.3.0")
    implementation("io.javalin:javalin-rendering:6.3.0")
    implementation("org.slf4j:slf4j-simple:2.0.16")
    implementation("gg.jte:jte:3.1.12")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("com.h2database:h2:2.2.224")
    implementation("org.postgresql:postgresql:42.7.4")
    implementation("org.apache.commons:commons-lang3:3.0")
    implementation("com.konghq:unirest-java:3.14.5")
    implementation("org.jsoup:jsoup:1.18.1")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.26.3")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jacocoTestReport { reports { xml.required.set(true) } }