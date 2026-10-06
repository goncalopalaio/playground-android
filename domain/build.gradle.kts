import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

val jdkVersion = libs.versions.jdk.get()

kotlin {
    jvmToolchain(jdkVersion.toInt())
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(jdkVersion))
    }
}

dependencies {
    implementation(project(":module:api:logger"))
    implementation(project(":module:common"))
    implementation(project(":module:api:device"))
    implementation(project(":module:api:space"))
    implementation(project(":module:data"))

    implementation(libs.kotlinx.coroutines.core)
}
