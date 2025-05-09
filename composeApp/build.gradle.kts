import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.jooqCodegen)
}

kotlin {
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            implementation(libs.kotlinx.serialization.json)

            implementation(libs.hoplite)
            implementation(libs.hoplite.yaml)

            implementation(libs.jooq)
            implementation(libs.jooq.meta)
            implementation(libs.jooq.codegen)
            implementation(libs.postgresql)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
        val desktopTest by getting {
            dependencies {
                implementation(compose.desktop.uiTestJUnit4)
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

dependencies {
    jooqCodegen(libs.postgresql)
}

jooq {
    configuration {
        jdbc {
            driver = "org.postgresql.Driver"
            url = "jdbc:postgresql://localhost:5432/random_chess"
            user = "random_chess"
            password = "random_chess"
        }
        generator {
            name = "org.jooq.codegen.KotlinGenerator"
            database {
                name = "org.jooq.meta.postgres.PostgresDatabase"
                inputSchema = "public"
                includes = ".*"
            }
            target {
                packageName = "pl.edu.uj.tcs.rchess.db"
                directory = "src/desktopMain/kotlin"
            }
            generate {
                isKotlinNotNullPojoAttributes = true
                isKotlinNotNullRecordAttributes = true
                isKotlinNotNullInterfaceAttributes = true
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "pl.edu.uj.tcs.rchess.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "pl.edu.uj.tcs.rchess"
            packageVersion = "1.0.0"
        }
    }
}

tasks["compileKotlinDesktop"].dependsOn("jooqCodegen")
