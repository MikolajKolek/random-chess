plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
}

tasks.register("allTests") {
    group = "verification"
    description = "Runs all tests in all projects"

    // Find and depend on all test tasks from subprojects
    subprojects.forEach { subproject ->
        dependsOn(subproject.tasks.matching { task ->
            task is org.gradle.api.tasks.testing.Test
        })
    }
}
