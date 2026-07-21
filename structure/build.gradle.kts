plugins {
    id("wutils.java-library")
}

repositories {
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        url = uri("https://maven.enginehub.org/repo/")
    }
}

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
    compileOnlyApi("com.sk89q.worldedit:worldedit-bukkit:7.4.4")
    compileOnlyApi("com.sk89q.worldguard:worldguard-bukkit:7.0.5")
    api(project(":WUtils-common"))
    api(project(":WUtils-configurables"))
    implementation("org.javatuples:javatuples:1.2")
}

version = "1.0.0-SNAPSHOT"

mavenPublishing {
    coordinates(findProperty("centralGroup").toString(), "wutils-animation", version.toString())

    pom {
        name = "WUtils Structure"
        description = "A configurable API for placing schematic-based structures in Bukkit/Paper worlds using WorldEdit and WorldGuard."
        inceptionYear = "2026"
    }
}