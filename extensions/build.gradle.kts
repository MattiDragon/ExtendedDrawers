plugins {
    id("common")
}

version = rootProject.version
group = rootProject.group
base.archivesName.set(rootProject.base.archivesName.map { it + "Extensions" })

dependencies {
    implementation(project(path = ":", configuration = "namedElements"))
    datagenImplementation(project(path = ":", configuration = "datagenElements"))
}

// Apply datagen at runtime
loom.mods.register("extended_drawers_extensions") {
    sourceSet(sourceSets["main"])
    sourceSet(sourceSets["client"])
    modFiles.from(file("src/main/generated"))
}

publishMods {
    modrinth {
        accessToken.set(providers.environmentVariable("MODRINTH_TOKEN"))
        projectId = "gDfTLxwP"

        requires("extended-drawers")
    }

    curseforge {
        accessToken.set(providers.environmentVariable("CURSEFORGE_TOKEN"))
        projectId = "1371952"

        requires("extended-drawers")
    }

    github {
        accessToken.set(providers.environmentVariable("GITHUB_TOKEN"))
        repository = "MattiDragon/ExtendedDrawers"

        parent(project(":").tasks.named("publishGithub"))
    }
}
