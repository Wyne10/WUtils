plugins {
    id("wutils.java-library")
}

repositories {
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
    implementation("org.javatuples:javatuples:1.2")
}

version = "2.1.0"

mavenPublishing {
    coordinates(findProperty("centralGroup").toString(), "wutils-animation", version.toString())

    pom {
        name = "WUtils Animation"
        description = "A lightweight API for creating and orchestrating sequential or parallel animations in Bukkit/Paper plugins."
        inceptionYear = "2025"
    }
}