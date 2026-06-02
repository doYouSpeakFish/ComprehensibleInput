package input.comprehensible.kover

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.w3c.dom.Element
import java.nio.file.Files
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.invariantSeparatorsPathString
import kotlin.io.path.isRegularFile

abstract class GenerateKoverCoverageReportTask : DefaultTask() {
    @get:InputFiles
    @get:Optional
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val reportFiles: ConfigurableFileCollection

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val sourceRoots: ListProperty<org.gradle.api.file.Directory>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val rootDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val report = reportFiles.singleFile
        if (!report.exists()) {
            throw GradleException("Kover XML report not found at ${report.absolutePath}")
        }

        val outputDirectory = outputDir.asFile.get()
        if (outputDirectory.exists()) {
            outputDirectory.deleteRecursively()
        }
        outputDirectory.mkdirs()

        val sourceRootPaths = sourceRoots.get().map { it.asFile.toPath() }
        if (sourceRootPaths.isEmpty()) {
            throw GradleException("No source roots configured for kover coverage report.")
        }

        val document = DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(report)
        document.documentElement.normalize()

        val fileReports = mutableListOf<FileReport>()
        val packageNodes = document.getElementsByTagName("package")
        for (index in 0 until packageNodes.length) {
            val packageElement = packageNodes.item(index) as? Element ?: continue
            val packageName = packageElement.getAttribute("name")
            val sourceFileNodes = packageElement.getElementsByTagName("sourcefile")
            for (sourceIndex in 0 until sourceFileNodes.length) {
                val sourceElement = sourceFileNodes.item(sourceIndex) as? Element ?: continue
                val sourceFileName = sourceElement.getAttribute("name")
                val lineCoverage = parseLineCoverage(sourceElement)
                val sourcePath = findSourceFile(sourceRootPaths, packageName, sourceFileName)
                if (sourcePath == null) {
                    logger.warn("Unable to locate source file for $packageName/$sourceFileName")
                    continue
                }

                val lines = Files.readAllLines(sourcePath)
                val coverageStatuses = buildCoverageStatuses(lines.size, lineCoverage)
                val hasMissingCoverage = coverageStatuses.values.any {
                    it == CoverageStatus.MISSED || it == CoverageStatus.PARTIAL
                }
                if (!hasMissingCoverage) {
                    continue
                }

                val relativePath = rootDir.asFile.get().toPath()
                    .relativize(sourcePath)
                    .invariantSeparatorsPathString
                val content = buildCoverageFileContent(
                    lines = lines,
                    coverageStatuses = coverageStatuses
                )

                fileReports.add(
                    FileReport(
                        relativePath = relativePath,
                        content = content
                    )
                )
            }
        }

        writeReports(outputDirectory, fileReports.sortedBy { it.relativePath })
    }

    private fun parseLineCoverage(sourceElement: Element): Map<Int, LineCoverage> {
        val lineNodes = sourceElement.getElementsByTagName("line")
        val coverageMap = mutableMapOf<Int, LineCoverage>()
        for (lineIndex in 0 until lineNodes.length) {
            val lineElement = lineNodes.item(lineIndex) as? Element ?: continue
            val lineNumber = lineElement.getAttribute("nr").toInt()
            val missedInstructions = lineElement.getAttribute("mi").toInt()
            val coveredInstructions = lineElement.getAttribute("ci").toInt()
            val missedBranches = lineElement.getAttribute("mb").toInt()
            val coveredBranches = lineElement.getAttribute("cb").toInt()
            coverageMap[lineNumber] = LineCoverage(
                lineNumber = lineNumber,
                missedInstructions = missedInstructions,
                coveredInstructions = coveredInstructions,
                missedBranches = missedBranches,
                coveredBranches = coveredBranches
            )
        }
        return coverageMap
    }

    private fun findSourceFile(
        roots: List<java.nio.file.Path>,
        packageName: String,
        sourceFileName: String
    ): java.nio.file.Path? {
        val packagePath = packageName.replace('.', '/')
        return roots.asSequence()
            .map { it.resolve(Path(packagePath)).resolve(sourceFileName) }
            .firstOrNull { it.exists() && it.isRegularFile() }
    }

    private fun buildCoverageStatuses(
        totalLines: Int,
        lineCoverage: Map<Int, LineCoverage>
    ): Map<Int, CoverageStatus> {
        val statuses = mutableMapOf<Int, CoverageStatus>()
        for (lineNumber in 1..totalLines) {
            val coverage = lineCoverage[lineNumber]
            statuses[lineNumber] = coverage?.status() ?: CoverageStatus.EXCLUDED
        }
        return statuses
    }

    private fun buildCoverageFileContent(
        lines: List<String>,
        coverageStatuses: Map<Int, CoverageStatus>
    ): String {
        return lines.indices.joinToString("\n", postfix = "\n") { index ->
            val lineNumber = index + 1
            val status = coverageStatuses[lineNumber] ?: CoverageStatus.EXCLUDED
            "${status.symbol}   ${lines[index].replace("\t", "    ")}"
        }.replace("\r\n", "\n")
    }

    private fun writeReports(outputDirectory: java.io.File, fileReports: List<FileReport>) {
        fileReports.forEach { report ->
            val reportFile = outputDirectory.resolve("${report.relativePath}.coverage")
            reportFile.parentFile.mkdirs()
            reportFile.writeText(report.content)
        }
    }
}

private data class LineCoverage(
    val lineNumber: Int,
    val missedInstructions: Int,
    val coveredInstructions: Int,
    val missedBranches: Int,
    val coveredBranches: Int
) {
    fun status(): CoverageStatus {
        return when {
            missedBranches > 0 && coveredBranches > 0 -> CoverageStatus.PARTIAL
            missedBranches > 0 && coveredBranches == 0 -> CoverageStatus.MISSED
            missedInstructions > 0 && coveredInstructions > 0 -> CoverageStatus.PARTIAL
            missedInstructions > 0 && coveredInstructions == 0 -> CoverageStatus.MISSED
            coveredInstructions > 0 -> CoverageStatus.COVERED
            else -> CoverageStatus.EXCLUDED
        }
    }
}

private enum class CoverageStatus(val symbol: String) {
    COVERED("🟢"),
    MISSED("🔴"),
    PARTIAL("🟡"),
    EXCLUDED("⚪")
}

private data class FileReport(
    val relativePath: String,
    val content: String
)
