package input.comprehensible.kover

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.nio.file.Files
import kotlin.io.path.isRegularFile
import kotlin.io.path.relativeTo

abstract class CompareKoverMarkdownSnapshotsTask : DefaultTask() {
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val generatedDir: DirectoryProperty

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val snapshotDir: DirectoryProperty

    @TaskAction
    fun verifySnapshots() {
        val generatedRoot = generatedDir.asFile.get().toPath()
        val snapshotRoot = snapshotDir.asFile.get().toPath()
        if (!Files.exists(snapshotRoot)) {
            throw GradleException("Snapshot directory does not exist at $snapshotRoot. Run koverMarkdownSnapshot.")
        }

        val generatedFiles = listFiles(generatedRoot)
        val snapshotFiles = listFiles(snapshotRoot)

        val missingInSnapshots = generatedFiles.keys - snapshotFiles.keys
        val missingInGenerated = snapshotFiles.keys - generatedFiles.keys
        val contentDifferences = generatedFiles.keys
            .intersect(snapshotFiles.keys)
            .filter { relativePath ->
                !Files.readString(generatedFiles.getValue(relativePath))
                    .contentEquals(Files.readString(snapshotFiles.getValue(relativePath)))
            }

        if (missingInSnapshots.isNotEmpty() || missingInGenerated.isNotEmpty() || contentDifferences.isNotEmpty()) {
            val message = buildString {
                appendLine("Kover markdown snapshots are out of date.")
                if (missingInSnapshots.isNotEmpty()) {
                    appendLine("Missing in snapshots:")
                    missingInSnapshots.sorted().forEach { appendLine(" - $it") }
                }
                if (missingInGenerated.isNotEmpty()) {
                    appendLine("Missing in generated output:")
                    missingInGenerated.sorted().forEach { appendLine(" - $it") }
                }
                if (contentDifferences.isNotEmpty()) {
                    appendLine("Changed content:")
                    contentDifferences.sorted().forEach { appendLine(" - $it") }
                }
                appendLine("Run koverMarkdownSnapshot to update snapshots.")
            }
            throw GradleException(message)
        }
    }

    private fun listFiles(root: java.nio.file.Path): Map<String, java.nio.file.Path> {
        if (!Files.exists(root)) {
            return emptyMap()
        }
        return Files.walk(root).use { stream ->
            stream
                .filter { it.isRegularFile() }
                .toList()
                .associateBy { it.relativeTo(root).toString().replace('\\', '/') }
        }
    }
}
