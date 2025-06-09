@file:Suppress("UnstableApiUsage")

plugins {
    kotlin("jvm") version "2.1.20"
    id("groovy")
    id("java-library")
}
kotlin {
    jvmToolchain(23)
}

group = "adrian.framework"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
java {
    modularity.inferModulePath.set(true)
    java.targetCompatibility = JavaVersion.VERSION_23
    java.sourceCompatibility = JavaVersion.VERSION_23
}

//tasks.named("compileJava", JavaCompile::class.java) {
//    options.compilerArgumentProviders.add(CommandLineArgumentProvider {
//        // Provide compiled Kotlin classes to javac – needed for Java/Kotlin mixed sources to work
//        listOf("--patch-module", "YOUR_MODULE_NAME=${sourceSets["core"].output.asPath}")
//    })
//}

sourceSets {
    getByName("main"){
        kotlin.srcDirs("src/main/kotlin")
        java  .srcDirs("src/main/java")
    }
    val core by creating {
        kotlin.srcDirs("src/core/kotlin")
        java  .srcDirs("src/core/java")
    }
    val demo by creating {
        kotlin.srcDirs("src/demo/kotlin")
        java  .srcDirs("src/demo/java")
    }
    getByName("test"){
        kotlin.srcDirs("src/test/kotlin")
        java  .srcDirs("src/test/java")
        groovy.srcDirs("src/test/groovy")
        compileClasspath += core.output
        compileClasspath += core.output
        runtimeClasspath += demo.output
        runtimeClasspath += demo.output

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
    testImplementation(sourceSets["demo"].output)

    //core
    add("coreImplementation", "net.jcip:jcip-annotations:1.0")
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

tasks.register<Jar>("coreJar") {
    archiveBaseName.set("${project.name}-core")
    from(sourceSets["core"].output)
}

tasks.register<Jar>("demoJar") {
    archiveBaseName.set("${project.name}-demo")
    from(sourceSets["demo"].output)
}

tasks.named("assemble") {
    dependsOn("coreJar", "demoJar")
}

artifacts {
    add("archives", tasks.named<Jar>("demoJar"))
    add("archives", tasks.named<Jar>("coreJar"))
}

