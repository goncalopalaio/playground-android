plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
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
