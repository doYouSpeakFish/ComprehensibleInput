package input.comprehensible.kover

import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property

abstract class KoverMarkdownReportExtension(project: Project) {
    abstract val reportFile: RegularFileProperty
    abstract val outputDir: DirectoryProperty
    abstract val snapshotDir: DirectoryProperty
    abstract val sourceRoots: ListProperty<Directory>
    abstract val contextLines: Property<Int>

    init {
        reportFile.convention(project.layout.buildDirectory.file("reports/kover/report.xml"))
        outputDir.convention(project.layout.buildDirectory.dir("reports/kover/markdown"))
        snapshotDir.convention(project.layout.projectDirectory.dir("config/kover/markdown-snapshots"))
        contextLines.convention(2)
    }
}
