plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
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
