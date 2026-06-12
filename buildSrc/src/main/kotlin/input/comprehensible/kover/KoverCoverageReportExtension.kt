package input.comprehensible.kover

import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty

abstract class KoverCoverageReportExtension(project: Project) {
    abstract val reportFile: RegularFileProperty
    abstract val outputDir: DirectoryProperty
    abstract val snapshotDir: DirectoryProperty
    abstract val sourceRoots: ListProperty<Directory>

    init {
        reportFile.convention(project.layout.buildDirectory.file("reports/kover/report.xml"))
        outputDir.convention(project.layout.buildDirectory.dir("reports/kover/coverage"))
        snapshotDir.convention(project.layout.projectDirectory.dir("config/kover/coverage-snapshots"))
    }

    fun sourceProjects(vararg projects: Project) {
        val candidatePaths = listOf("src/main/kotlin", "src/main/java")
        sourceRoots.addAll(
            projects.flatMap { p ->
                candidatePaths
                    .map { path -> p.layout.projectDirectory.dir(path) }
                    .filter { it.asFile.exists() }
            }
        )
    }
}
