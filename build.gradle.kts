
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(ktorLibs.plugins.ktor)
    alias(libs.plugins.kotlin.serialization)
}

group = "com.example"
version = "1.0.0-SNAPSHOT"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

kotlin {
    jvmToolchain(21)
}
dependencies {
    implementation(ktorLibs.serialization.kotlinx.json)
    implementation(ktorLibs.server.auth)
    implementation(ktorLibs.server.auth.jwt)
    implementation(ktorLibs.server.callId)
    implementation(ktorLibs.server.callLogging)
    implementation(ktorLibs.server.compression)
    implementation(ktorLibs.server.config.yaml)
    implementation(ktorLibs.server.contentNegotiation)
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.cors)
    implementation(ktorLibs.server.defaultHeaders)
    implementation(ktorLibs.server.netty)
    implementation(ktorLibs.server.statusPages)
    implementation(libs.flaxoos.ktor.server.rateLimiting)
    implementation(libs.koin.ktor)
    implementation(libs.koin.loggerSlf4j)
    implementation(libs.logback.classic)
    implementation(libs.mongodb.bson)
    implementation(libs.mongodb.driverCore)
    implementation(ktorLibs.server.rateLimit)

    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:5.8.0")
    implementation("commons-codec:commons-codec:1.18.0")
    implementation("io.sentry:sentry:7.10.0")

    implementation(ktorLibs.client.core)
    implementation(ktorLibs.client.cio)
    implementation(ktorLibs.client.contentNegotiation)
    implementation(ktorLibs.client.logging)

    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")

    testImplementation(kotlin("test"))
    testImplementation(ktorLibs.server.testHost)

}
