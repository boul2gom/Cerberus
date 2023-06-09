import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar;

plugins {
    id("com.github.johnrengelman.shadow")
}

repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.2.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.2.0-SNAPSHOT")

    implementation(project(":Common"))
}

tasks.withType<Jar> {
    archiveBaseName.set("Cerberus-Velocity")
}