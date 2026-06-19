import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar.Companion.shadowJar

plugins {
    kotlin("jvm") version "2.4.0"
    id("com.gradleup.shadow") version "9.4.2"
    id("xyz.jpenilla.run-paper") version "3.0.2"
    kotlin("plugin.serialization") version "2.3.20"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-releases/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")

    implementation("com.github.retrooper:packetevents-spigot:2.12.2")
    implementation("org.jetbrains.exposed:exposed-core:1.3.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:1.3.0")
    implementation("org.jetbrains.exposed:exposed-migration-jdbc:1.3.0")

    implementation("org.xerial:sqlite-jdbc:3.53.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.mojang:brigadier:1.0.500")
    implementation(kotlin("reflect"))
}

version = "1.1.0"

kotlin {
    jvmToolchain(21)
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        relocate("com.github.retrooper", "org.firestorm.deathproRemake.libs.packetevents")
        relocate("io.github.retrooper",  "org.firestorm.deathproRemake.libs.packetevents")
        relocate("org.jetbrains.exposed.v1","org.firestorm.deathproRemake.libs.exposed")
        mergeServiceFiles()
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    processResources {
        val props = mapOf("version" to version)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    build {
        dependsOn(shadowJar)
    }

    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("1.21.1")
        jvmArgs("-Xms2G", "-Xmx2G")
    }

    processResources {
        val props = mapOf("version" to version)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}
