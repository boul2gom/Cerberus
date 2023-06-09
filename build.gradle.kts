plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
}

allprojects {
    apply(plugin = "idea")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    version = "1.0.0-SNAPSHOT"
    group = "fr.boul2gom.cerberus"

    repositories {
        mavenLocal()
        mavenCentral()

        maven("https://jitpack.io")

        maven {
            url = uri("https://maven.pkg.github.com/boul2gom/nullptr-tools")
            credentials {
                username = System.getenv("GITHUB_USERNAME")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }

    tasks.withType<Jar> {
        manifest {
            attributes["Main-Class"] = "fr.boul2gom.cerberus.common.Main"
        }

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = JavaVersion.VERSION_18.toString()
        targetCompatibility = JavaVersion.VERSION_18.toString()
        options.encoding = "UTF-8"
    }

    tasks.withType<Javadoc> {
        options.encoding = "UTF-8"
    }
}