plugins {
    alias(libs.plugins.kotlin.jvm)
    id("app.cash.sqldelight") version "2.0.2"
    application
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    // Use the Kotlin JUnit 5 integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    // Use the JUnit 5 integration.
    testImplementation(libs.junit.jupiter.engine)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // This dependency is used by the application.
    implementation(libs.guava)
    implementation(libs.libadwaita)
    implementation(libs.sqldelight)
    implementation("app.cash.sqldelight:sqlite-driver:2.0.2")
    testImplementation("io.mockk:mockk:1.13.13")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("app.cash.sqldelight:coroutines-extensions:2.0.2")
    implementation("org.kodein.di:kodein-di:7.22.0")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(22)
    }
}

application {
    // Define the main class for the application.
    mainClass = "fr.arsenelapostolet.professor.AppKt"
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("fr.arsenelapostolet.professor")
        }
    }
}
