plugins {
    kotlin("jvm") version "2.1.20"
}

group = "adrian.framework"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

sourceSets {
    getByName("main"){
        kotlin.srcDirs("src/main/kotlin")
    }
    getByName("test"){
        kotlin.srcDirs("src/test/kotlin")
    }
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
}