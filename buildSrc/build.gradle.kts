plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    maven("https://maven.fabricmc.net")
}

dependencies {
    implementation((libs.plugins.fabric.loom).dependency)
    implementation((libs.plugins.mod.publish).dependency)
    // Mega hack to get version catalog in common.gradle.kts: https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
    implementation(files((libs).javaClass.superclass.protectionDomain.codeSource.location))
}

val Provider<PluginDependency>.dependency
    get() = map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" }