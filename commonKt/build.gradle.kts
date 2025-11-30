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
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib:2.2.20")
    compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
    compileOnly("dev.jorel:commandapi-bukkit-core:9.4.2")
    api(project(":WUtils-common"))
}

version = project(":WUtils-common").version

mavenPublishing {
    coordinates(findProperty("centralGroup").toString(), "wutils-common-kotlin", version.toString())

    pom {
        name = "WUtils Common Kotlin"
        description = "Kotlin extensions and wrappers for the WUtils Common"
        inceptionYear = "2025"
    }
}