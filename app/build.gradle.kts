plugins {
    java
    application
    kotlin("jvm")
    id("com.apollographql.apollo") version "4.2.0"
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation("org.eclipse.jgit:org.eclipse.jgit:7.1.0.202411261347-r")
    implementation("de.swiesend:secret-service:2.0.1-alpha")
    implementation("io.github.jwharm.javagi:adw:0.11.2")
    implementation("org.kodein.di:kodein-di:7.22.0")
    implementation("de.swiesend:secret-service:2.0.1-alpha")
    implementation("ch.qos.logback:logback-classic:1.5.15")
    implementation("org.gitlab4j:gitlab4j-api:6.0.0-rc.10")
    implementation("org.xerial:sqlite-jdbc:3.47.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    implementation(kotlin("stdlib-jdk8"))

    implementation("com.apollographql.apollo:apollo-runtime:4.2.0")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.3")
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
    mainClass = "fr.arsenelapostolet.professor.App"
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

apollo {

    service("service") {

        packageName.set("fr.arsenelapostolet.professor.gitlabgraphql")
        introspection {
            endpointUrl.set("https://gitlab.com/api/graphql")
            schemaFile.set(file("src/main/graphql/schema.graphqls"))
        }
    }

}