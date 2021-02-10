plugins {
    kotlin("js") version "1.4.30"
}

group = "me.jamie"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test-js"))
}

kotlin {
    js(LEGACY) {
        moduleName = "graphics"

        browser {
            webpackTask {
                outputFileName = "graphics.js"
                output.libraryTarget = "commonjs2"
            }
        }
        binaries.executable()
    }
}