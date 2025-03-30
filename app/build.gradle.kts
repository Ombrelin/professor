plugins {
    alias(libs.plugins.kotlin.jvm)
    application
    id("org.beryx.jlink") version "3.1.1"
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation("org.eclipse.jgit:org.eclipse.jgit:7.1.0.202411261347-r")
    //implementation("de.swiesend:secret-service:2.0.1-alpha")
    implementation(libs.libadwaita)
    implementation("org.kodein.di:kodein-di:7.22.0")
    // implementation("de.swiesend:secret-service:2.0.1-alpha")
    implementation("ch.qos.logback:logback-classic:1.5.15")
    // implementation("org.gitlab4j:gitlab4j-api:6.0.0-rc.8")
    implementation("org.xerial:sqlite-jdbc:3.47.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation(libs.junit.jupiter.engine)
    testImplementation("io.mockk:mockk:1.13.13")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.3")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(22)
    }
}

application {
    mainClass = "fr.arsenelapostolet.professor.AppKt"
    mainModule = "fr.arsenelapostolet.professor"
}


tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.register<Test>("testCi") {
    useJUnitPlatform()
    filter {
        excludeTestsMatching("fr.arsenelapostolet.professor.core.services.FreeDesktopSecretServiceTests")
    }
}

jlink {
    addOptions(
        "--strip-debug",
        "--no-header-files",
        "--no-man-pages",
        "--verbose"
    )
    launcher {
        name = "Professor"
    }
    jpackage {
        vendor = "Arsène Lapostolet"
        jvmArgs.addAll(
            listOf(
                "--enable-native-access=org.gnome.gtk",
                "--enable-native-access=org.gnome.glib",
                "--enable-native-access=org.gnome.gobject",
                "--enable-native-access=org.gnome.gio",
                "--enable-native-access=org.gnome.adw"
            )
        )
        installerType = "rpm"
    }
    forceMerge("kotlin")
}