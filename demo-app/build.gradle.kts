plugins {
    id("java-src-groovy-test")
    kotlin("jvm") version "2.1.20"
}

sourceSets {
    getByName("main"){
        kotlin.srcDirs("src/main/kotlin")
    }
    getByName("test"){
        kotlin.srcDirs("src/test/kotlin")
    }
}

