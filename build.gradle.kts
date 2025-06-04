@file:Suppress("UnstableApiUsage")

plugins {
    kotlin("jvm") version "2.1.20"
    id("groovy")
}
kotlin {
    jvmToolchain(23)
}

group = "adrian.framework"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

sourceSets {
    getByName("main"){
        kotlin.srcDirs("src/main/kotlin")
        java  .srcDirs("src/main/java")
    }
    getByName("test"){
        kotlin.srcDirs("src/test/kotlin")
        java  .srcDirs("src/test/java")
        groovy.srcDirs("src/test/groovy")
    }
    val sample by creating {
        kotlin.srcDirs("src/sample/kotlin")
        java  .srcDirs("src/sample/java")
    }
}

configurations {
    named("sampleImplementation") {
        extendsFrom(configurations["implementation"])
    }
    named("sampleRuntimeOnly") {
        extendsFrom(configurations["runtimeOnly"])
    }
}


dependencies {
    testImplementation(kotlin("test"))

    // mandatory dependencies for using Spock
    implementation (platform("org.apache.groovy:groovy-bom:4.0.26"))
    implementation ("org.apache.groovy:groovy")
    testImplementation (platform("org.spockframework:spock-bom:2.3-groovy-4.0"))
    testImplementation ("org.spockframework:spock-core")
    testImplementation ("org.spockframework:spock-junit4")  // you can remove this if your code does not rely on old JUnit 4 rules

    // optional dependencies for using Spock
    // testImplementation("org.hamcrest:hamcrest-core:3.0") // only necessary if Hamcrest matchers are used
    // testRuntimeOnly ("net.bytebuddy:byte-buddy:1.17.5") // allows mocking of classes (in addition to interfaces)
    // testRuntimeOnly ("org.objenesis:objenesis:3.4") // allows mocking of classes without default constructor (together with ByteBuddy or CGLIB)

    // dependencies used by examples in this project
    // testRuntimeOnly ("com.h2database:h2:2.2.224")
    // implementation ("org.apache.groovy:groovy-sql")
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<Jar>("sampleJar") {
    archiveClassifier.set("sample")
    from(sourceSets["sample"].output)
}