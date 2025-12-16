plugins {
    id("wutils.java-library")
    kotlin("jvm")
}

kotlin {
    jvmToolchain(16)
}

repositories {
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib:2.3.0")
    compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
    compileOnly("net.kyori:adventure-text-minimessage:4.25.0")
    compileOnly("net.kyori:adventure-platform-bukkit:4.4.1")
    api(project(":WUtils-i18n"))
}

version = project(":WUtils-i18n").version

mavenPublishing {
    coordinates(findProperty("centralGroup").toString(), "wutils-i18n-kotlin", version.toString())

    pom {
        name = "WUtils Internationalization Kotlin"
        description = "Kotlin extensions and wrappers for the WUtils Internationalization"
        inceptionYear = "2025"
    }
}
