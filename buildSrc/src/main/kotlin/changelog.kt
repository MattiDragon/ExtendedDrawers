import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider

fun Project.changelogText(): Provider<String> {
    val mcVersion = extensions.getByType(VersionCatalogsExtension::class.java)
        .named("libs")
        .findVersion("minecraft")
        .orElseThrow()
    val modVersion = property("mod_version")
    return providers.fileContents(layout.projectDirectory.file("changelog/$modVersion+$mcVersion.md")).asText
}
