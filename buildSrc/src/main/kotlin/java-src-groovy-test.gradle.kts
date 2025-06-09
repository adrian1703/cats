plugins {
    id("groovy")
    id("java-library")
}

group = "adrian.framework"
version = "0.1-SNAPSHOT"

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
        java  .srcDirs("src/main/java")
    }
    getByName("test"){
        java  .srcDirs("src/test/java")
        groovy.srcDirs("src/test/groovy")
    }
}

dependencies {

    // mandatory dependencies for using Spock
    implementation (platform("org.apache.groovy:groovy-bom:4.0.26"))
    implementation ("org.apache.groovy:groovy")
    testImplementation (platform("org.spockframework:spock-bom:2.3-groovy-4.0"))
    testImplementation ("org.spockframework:spock-core")
    testImplementation ("org.spockframework:spock-junit4")  // you can remove this if your code does not rely on old JUnit 4 rules

    //core
    // optional dependencies for using Spock
    // testImplementation("org.hamcrest:hamcrest-core:3.0") // only necessary if Hamcrest matchers are used
    // testRuntimeOnly ("net.bytebuddy:byte-buddy:1.17.5") // allows mocking of classes (in addition to interfaces)
    // testRuntimeOnly ("org.objenesis:objenesis:3.4") // allows mocking of classes without default constructor (together with ByteBuddy or CGLIB)

}

tasks.jar {
    archiveBaseName.set("${rootProject.name}-${project.name}")
}

tasks.test {
    useJUnitPlatform()
}