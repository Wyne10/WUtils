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

version = "2.0.5"

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = findProperty("group").toString()
            artifactId = "WUtils-animation"
            version = findProperty("version").toString()

            from(components["java"])
        }
    }
}