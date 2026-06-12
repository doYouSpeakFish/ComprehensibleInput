package input.comprehensible.kover

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Sync
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register

class KoverCoverageReportPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create<KoverCoverageReportExtension>("koverCoverageReport", project)
        extension.sourceRoots.addAll(defaultSourceRoots(project))

        val reportTask = project.tasks.register<GenerateKoverCoverageReportTask>("koverCoverageReport") {
            group = "verification"
            description = "Generate custom coverage files from the Kover XML coverage output."
            reportFiles.from(extension.reportFile)
            outputDir.set(extension.outputDir)
            rootDir.set(project.rootProject.layout.projectDirectory)
            rootDirPath.set(project.rootProject.layout.projectDirectory.asFile.absolutePath)
            sourceRoots.set(extension.sourceRoots)
            dependsOn(project.tasks.named("koverXmlReport"))
        }
        val snapshotTask = project.tasks.register<Sync>("koverCoverageSnapshot") {
            group = "verification"
            description = "Update the stored Kover custom coverage snapshot reports."
            dependsOn(reportTask)
            from(extension.outputDir)
            into(extension.snapshotDir)
        }

        // Detekt analyses the whole project tree, which contains the snapshot directory this task
        // writes into. Order them explicitly so the two can be requested in a single invocation
        // (e.g. `./gradlew detekt lint koverCoverageSnapshot`) without Gradle flagging an undeclared
        // dependency between detekt's inputs and the snapshot output.
        project.tasks.matching { it.name.startsWith("detekt") }.configureEach {
            mustRunAfter(snapshotTask)
        }

        project.tasks.register<CompareKoverCoverageSnapshotsTask>("koverCoverageSnapshotCheck") {
            group = "verification"
            description = "Verify the Kover custom coverage snapshot reports match the stored snapshots."
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
        return candidatePaths
            .map { path -> project.layout.projectDirectory.dir(path) }
            .filter { it.asFile.exists() }
    }
}
