import com.matthewprenger.cursegradle.CurseProject
import com.matthewprenger.cursegradle.CurseRelation
import com.matthewprenger.cursegradle.Options

plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
    id("fabric-loom") version "1.9-SNAPSHOT"
    id("com.modrinth.minotaur") version "2.+"
    id("com.matthewprenger.cursegradle") version "1.4.0"
    id("java")
}

group = "me.obsilabor"
version = "1.7.4+1.21.4"

repositories {
    mavenCentral()
    maven("https://maven.terraformersmc.com")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.isxander.dev/releases")
    maven("https://api.modrinth.com/maven")
}

dependencies {
    // kotlin
    implementation(kotlin("stdlib"))
    // event system
    include("me.obsilabor:alert:1.0.8")
    implementation("me.obsilabor:alert:1.0.8")
    // paper
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    // fabric
    minecraft("com.mojang:minecraft:1.21.4")
    mappings("net.fabricmc:yarn:1.21.4+build.4")
    modImplementation("net.fabricmc:fabric-loader:0.16.9")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.113.0+1.21.4")
    modImplementation("net.fabricmc:fabric-language-kotlin:1.13.0+kotlin.2.1.0")
    // modmenu
    modApi("maven.modrinth:modmenu:12.0.0")
    // yacl
    modApi("dev.isxander:yet-another-config-lib:3.6.2+1.21.4-fabric")
}

tasks {
    processResources {
        val properties = mapOf(
            "version" to project.version,
        )
        inputs.properties(properties)
        filesMatching("fabric.mod.json") {
            expand(properties)
        }
        filesMatching("plugin.yml") {
            expand(properties)
        }
    }
    named("curseforge") {
        onlyIf {
            System.getenv("CURSEFORGE_TOKEN") != null
        }
        dependsOn(remapJar)
    }
    compileJava {
        options.encoding = "UTF-8"
    }
}

kotlin {
    jvmToolchain(21)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("tps-hud")
    versionNumber.set(project.version.toString())
    versionType.set("release")
    gameVersions.addAll(listOf("1.21.4"))
    loaders.add("fabric")
    loaders.add("quilt")
    loaders.add("purpur")
    loaders.add("paper")
    loaders.add("spigot")
    loaders.add("bukkit")
    dependencies {
        required.project("fabric-api")
        required.project("fabric-language-kotlin")
        optional.project("yacl")
        optional.project("modmenu")
    }
    uploadFile.set(tasks.remapJar.get())
}

curseforge {
    project(closureOf<CurseProject> {
        apiKey = System.getenv("CURSEFORGE_TOKEN")
        mainArtifact(tasks.remapJar.get())

        id = "610618"
        releaseType = "release"
        addGameVersion("1.21.4")
        addGameVersion("Fabric")
        addGameVersion("Quilt")

        relations(closureOf<CurseRelation> {
            requiredDependency("fabric-api")
            requiredDependency("fabric-language-kotlin")
            optionalDependency("yacl")
            optionalDependency("modmenu")
        })
    })
    options(closureOf<Options> {
        forgeGradleIntegration = false
    })
}
