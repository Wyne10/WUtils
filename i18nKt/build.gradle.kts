plugins {
    id("wutils.java-library")
    kotlin("jvm") version "2.1.20"
}

repositories {
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
    api(project(":WUtils-i18n"))
}

version = "1.0.0"

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = findProperty("group").toString()
            artifactId = "WUtils-i18n-kotlin"
            version = findProperty("version").toString()

            from(components["java"])
        }
    }
}