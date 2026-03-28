import java.util.*

plugins {
    // Can't use catalog here, hack isn't good enough
    id("net.fabricmc.fabric-loom")
    id("me.modmuss50.mod-publish-plugin")
    id("maven-publish")
}

repositories {
    maven("https://maven.kneelawk.com/releases/")
    maven("https://maven.alexiil.uk/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://maven.blamejared.com")
    maven("https://maven.isxander.dev/releases")
    maven("https://maven.terraformersmc.com")
    maven("https://maven.quiltmc.org/repository/release/")
    maven("https://maven.cafeteria.dev/releases/")
    maven("https://jitpack.io")
    maven("https://maven.nucleoid.xyz/releases") {
        content {
            includeGroupAndSubgroups("com.kneelawk")
            includeGroupAndSubgroups("eu.pb4")
        }
    }
//    mavenLocal()
}

loom.splitEnvironmentSourceSets()

val mod_version: String by project
val libs = the<org.gradle.accessors.dm.LibrariesForLibs>()

dependencies {
    minecraft(libs.minecraft)
    implementation(libs.fabric.loader)
    implementation(libs.fabric.api)

    // Add generated data to runtime classpath (slightly hacky)
    runtimeOnly(files("src/main/generated"))
}

fabricApi.configureDataGeneration {
    client = true
    createSourceSet = true
    addToResources = false
}

loom.runs.configureEach {
    ideConfigGenerated(true)

    // If we're running datagen and other runs at the same time, datagen must run first to make gradle happy
    if (name != "datagen") {
        tasks.named("run${name.replaceFirstChar { it.uppercaseChar() }}") {
            mustRunAfter(tasks["runDatagen"])
        }
    }
}

// Delete datagen on clean
tasks.clean {
    delete("src/main/generated")
}

tasks.processResources {
    inputs.property("version", version)
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(mapOf("version" to version))
    }
    filesMatching("**/assets/*/lang/*.json") {
        expand(mapOf("version" to mod_version))
    }
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${base.archivesName}" }
    }

    // Copy datagen to jar
    dependsOn(tasks["runDatagen"])
    from("src/main/generated") {
        exclude("README.md")
        exclude(".cache")
    }
}

java {
    withSourcesJar()
}

publishing {
    publications.create<MavenPublication>("mavenJava") {
        from(components["java"])
    }
    repositories {}
}


publishMods {
    file.set(tasks.jar.get().archiveFile)
    additionalFiles.from(tasks["sourcesJar"])

    displayName = "v${mod_version} [${libs.versions.minecraft.get()}]"
    changelog = changelogText()

    type.set(providers.environmentVariable("RELEASE_TYPE").map { me.modmuss50.mpp.ReleaseType.of(it) })
    modLoaders.add("fabric")

    dryRun = providers.gradleProperty("publish_dry_run").isPresent

    modrinth {
        accessToken.set(providers.environmentVariable("MODRINTH_TOKEN"))
        projectDescription = providers.environmentVariable("SYNC_DESCRIPTION")
            .filter { it.lowercase(Locale.ROOT) == "true" }
            .flatMap { providers.fileContents(layout.projectDirectory.file("README.md")).asText }
        minecraftVersions.add(libs.versions.minecraft.get())
    }

    curseforge {
        accessToken.set(providers.environmentVariable("CURSEFORGE_TOKEN"))
        minecraftVersions.add(libs.versions.minecraft.get())
    }

    github {
        accessToken.set(providers.environmentVariable("GITHUB_TOKEN"))
    }
}
