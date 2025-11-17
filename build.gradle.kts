// Top-level Gradle build file

plugins {
    id("com.android.application") version "8.1.0" apply false
    kotlin("android") version "1.9.22" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
