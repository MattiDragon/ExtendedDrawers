plugins {
    id("common")
}

val run = System.getenv("GITHUB_RUN_NUMBER")
val isDev = System.getenv("DEV_BUILD") == "true"

val mod_version: String by project

version = mod_version + (if (isDev) "-dev.$run" else "") + "+mc.${libs.versions.minecraft.get()}"
group = project.property("maven_group") as String
base.archivesName = project.property("archives_base_name") as String

configurations.consumable("datagenElements")

dependencies {
    libs.graphlib.core.let { api(it); include(it) }
    //modLocalRuntime(libs.graphlib.debugrender)
    //modImplementation(libs.patchouli)

    libs.configtoolkit.let { api(it); include(it); annotationProcessor(it) }
    libs.yacl.let { api(it); include(it) }

    api(libs.modmenu)
}

// Apply datagen at runtime
loom.mods.register("extended_drawers") {
    sourceSet(sourceSets["main"])
    sourceSet(sourceSets["client"])
    modFiles.from(file("src/main/generated"))
}

tasks.register<Jar>("datagenJar") {
    from(sourceSets["datagen"].output)
    archiveClassifier = "datagen"
    destinationDirectory = layout.buildDirectory.dir("devlibs")
}

artifacts {
    add("datagenElements", tasks["datagenJar"])
}

publishMods {
    modrinth {
        projectId = "AhtxbnpG"
        requires("fabric-api")
    }

    curseforge {
        projectId = "616602"
        requires("fabric-api")
    }

    github {
        repository = "MattiDragon/ExtendedDrawers"

        changelog = project.changelogText().flatMap { mainText ->
            project(":extensions").changelogText().map { extensionsText ->
                "## Main mod\n$mainText\n\n## Extensions\n$extensionsText"
            }
        }

        commitish.set(providers.environmentVariable("GITHUB_BRANCH"))
        tagName.set(version.map { it.replace('+', '-') })
    }
}
