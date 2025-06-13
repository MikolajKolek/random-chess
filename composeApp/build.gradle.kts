import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

kotlin {
    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            implementation(projects.shared)
            implementation(projects.server)

            implementation(libs.logback)
            implementation(libs.kotlin.logging)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }

        @Suppress("unused")
        val desktopTest by getting {
            dependencies {
                implementation(compose.desktop.uiTestJUnit4)
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "pl.edu.uj.tcs.rchess.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Random Chess"
            description = "A chess app with functionality for saving, analyzing and playing games"
            packageVersion = "1.0.0"

            linux {
                iconFile.set(project.file("launcher_icon/launcher_icon.png"))
            }
            windows {
                iconFile.set(project.file("launcher_icon/launcher_icon.ico"))
                menu = true
                menuGroup = "random-chess"
                upgradeUuid = "8cbeacf9-2ce7-4262-b634-6a55b1e7f082"
            }
            macOS {
                iconFile.set(project.file("launcher_icon/launcher_icon.icns"))
            }

            modules("java.instrument", "java.naming", "jdk.unsupported", "java.sql")
        }
    }
}
