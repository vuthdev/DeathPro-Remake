import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar.Companion.shadowJar

plugins {
    kotlin("jvm") version "2.4.0"
    id("com.gradleup.shadow") version "9.4.2"
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")

    implementation("org.jetbrains.exposed:exposed-core:1.3.0")
    runtimeOnly("org.jetbrains.exposed:exposed-jdbc:1.3.0")

    implementation("org.xerial:sqlite-jdbc:3.53.2.0")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.mojang:brigadier:1.0.500")
    implementation(kotlin("reflect"))
}

kotlin {
    jvmToolchain(25)
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        relocate("com.github.retrooper", "org.firestorm.deathproRemake.libs.packetevents")
        relocate("io.github.retrooper",  "org.firestorm.deathproRemake.libs.packetevents")
        relocate("org.jetbrains.exposed","org.firestorm.deathproRemake.libs.exposed")
        relocate("org.sqlite",           "org.firestorm.deathproRemake.libs.sqlite")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    build {
        dependsOn(shadowJar)
    }

    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("26.1.2")
        jvmArgs("-Xms2G", "-Xmx2G")
    }

    processResources {
        val props = mapOf("version" to version)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}
