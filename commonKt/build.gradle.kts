plugins {
    id("wutils.java-library")
    kotlin("jvm") version "2.1.20"
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
    compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
    api(project(":WUtils-common"))
}

version = project(":WUtils-common").version

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = findProperty("group").toString()
            artifactId = "WUtils-common-kotlin"
            version = findProperty("version").toString()

            from(components["java"])
        }
    }
}