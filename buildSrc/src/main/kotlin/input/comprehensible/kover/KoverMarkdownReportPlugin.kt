package input.comprehensible.kover

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import org.gradle.api.tasks.Sync

class KoverMarkdownReportPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create<KoverMarkdownReportExtension>("koverMarkdownReport", project)
        extension.sourceRoots.convention(defaultSourceRoots(project))

        val reportTask = project.tasks.register<GenerateKoverMarkdownReportTask>("koverMarkdownReport") {
            group = "verification"
            description = "Generate markdown report from the Kover XML coverage output."
            reportFile.set(extension.reportFile)
            outputDir.set(extension.outputDir)
            rootDir.set(project.layout.projectDirectory)
            sourceRoots.set(extension.sourceRoots)
            contextLines.set(extension.contextLines)
        }
        project.tasks.register<Sync>("koverMarkdownSnapshot") {
            group = "verification"
            description = "Update the stored Kover markdown snapshot reports."
            dependsOn(reportTask)
            from(extension.outputDir)
            into(extension.snapshotDir)
        }

        project.tasks.register<CompareKoverMarkdownSnapshotsTask>("koverMarkdownSnapshotCheck") {
            group = "verification"
            description = "Verify the Kover markdown snapshot reports match the stored snapshots."
            dependsOn(reportTask)
            generatedDir.set(extension.outputDir)
            snapshotDir.set(extension.snapshotDir)
        }
    }

    private fun defaultSourceRoots(project: Project): List<org.gradle.api.file.Directory> {
        val candidatePaths = listOf(
            "src/main/kotlin",
            "src/main/java"
        )
        return (listOf(project) + project.subprojects)
            .flatMap { subproject ->
                candidatePaths.map { path -> subproject.layout.projectDirectory.dir(path) }
            }
            .filter { it.asFile.exists() }
    }
}
