import gradle.kotlin.dsl.accessors._a5234c4e825e60729b8724bb81857266.versionCatalogs
import org.gradle.api.Project
import org.gradle.api.provider.Provider

fun Project.changelogText(): Provider<String?> {
    val mcVersion = versionCatalogs.named("libs").findVersion("minecraft").get()
    val modVersion = property("mod_version")
    return providers.fileContents(layout.projectDirectory.file("changelog/$modVersion+$mcVersion.md")).asText
}
