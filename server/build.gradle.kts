import org.jooq.meta.jaxb.MatcherTransformType

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.jooqCodegen)
}

kotlin {
    sourceSets.main {
        languageSettings {
            optIn("kotlin.time.ExperimentalTime")
        }
    }
}

dependencies {
    implementation(projects.shared)

    implementation(libs.logback)
    implementation(libs.kotlin.logging)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.hoplite)
    implementation(libs.hoplite.yaml)

    implementation(libs.jooq)
    implementation(libs.jooq.meta)
    implementation(libs.jooq.codegen)
    implementation(libs.jooq.kotlin)
    implementation(libs.jooq.kotlin.coroutines)
    implementation(libs.r2dbc.spi)

    implementation(libs.jdbc.postgresql)
    implementation(libs.r2dbc.postgresql)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)

    testImplementation(libs.kotlin.test.junit)
}

dependencies {
    jooqCodegen(libs.jdbc.postgresql)
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
                packageName = "pl.edu.uj.tcs.rchess.generated.db"
                directory = "build/generated/db/kotlin"
            }
            generate {
                isKotlinNotNullPojoAttributes = true
                isKotlinNotNullRecordAttributes = true
                isKotlinNotNullInterfaceAttributes = true
            }
            strategy {
                matchers {
                    enums {
                        enum_ {
                            enumClass {
                                transform = MatcherTransformType.PASCAL
                                expression = "db_$0"
                            }
                        }
                    }
                }
            }
        }
    }
}

tasks["compileKotlin"].dependsOn("jooqCodegen")
// Disable test caching
tasks["test"].outputs.upToDateWhen { false }