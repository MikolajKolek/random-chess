plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    jvm()

    sourceSets {
        jvmMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)

            implementation(libs.logback)
            implementation(libs.kotlin.logging)
        }
        jvmMain.languageSettings {
            optIn("kotlin.time.ExperimentalTime")
        }
        jvmTest.dependencies {
            implementation(libs.kotlin.test.junit)
        }
    }
}

// Disable test caching
tasks["jvmTest"].outputs.upToDateWhen { false }