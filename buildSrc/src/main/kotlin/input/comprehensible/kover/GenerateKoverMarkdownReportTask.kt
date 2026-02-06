package input.comprehensible.kover

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
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
import kotlin.io.path.isRegularFile

abstract class GenerateKoverMarkdownReportTask : DefaultTask() {
    @get:InputFile
    @get:Optional
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val reportFile: RegularFileProperty

    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val sourceRoots: ListProperty<org.gradle.api.file.Directory>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val rootDir: DirectoryProperty

    @get:Input
    abstract val contextLines: Property<Int>

    @TaskAction
    fun generate() {
        val report = reportFile.asFile.get()
        if (!report.exists()) {
            throw GradleException("Kover XML report not found at ${report.absolutePath}")
        }

        val outputDirectory = outputDir.asFile.get()
        outputDirectory.mkdirs()

        val sourceRootPaths = sourceRoots.get().map { it.asFile.toPath() }
        if (sourceRootPaths.isEmpty()) {
            throw GradleException("No source roots configured for kover markdown report.")
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
                val missingLines = coverageStatuses
                    .filterValues { it == CoverageStatus.MISSED || it == CoverageStatus.PARTIAL }
                    .keys
                    .sorted()

                if (missingLines.isEmpty()) {
                    continue
                }

                val relativePath = rootDir.asFile.get().toPath().relativize(sourcePath).toString()
                val snippets = buildSnippets(
                    lines = lines,
                    coverageStatuses = coverageStatuses,
                    missingLines = missingLines,
                    context = contextLines.get()
                )
                if (snippets.isEmpty()) {
                    continue
                }

                val summary = CoverageSummary(
                    missedLines = coverageStatuses.values.count { it == CoverageStatus.MISSED },
                    partialLines = coverageStatuses.values.count { it == CoverageStatus.PARTIAL }
                )

                fileReports.add(
                    FileReport(
                        relativePath = relativePath,
                        snippets = snippets,
                        summary = summary
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

    private fun buildSnippets(
        lines: List<String>,
        coverageStatuses: Map<Int, CoverageStatus>,
        missingLines: List<Int>,
        context: Int
    ): List<Snippet> {
        val totalLines = lines.size
        val windows = mutableListOf<IntRange>()
        for (missingLine in missingLines) {
            val start = (missingLine - context).coerceAtLeast(1)
            val end = (missingLine + context).coerceAtMost(totalLines)
            if (windows.isEmpty()) {
                windows.add(start..end)
            } else {
                val last = windows.last()
                if (start <= last.last + 1) {
                    windows[windows.lastIndex] = last.first..maxOf(last.last, end)
                } else {
                    windows.add(start..end)
                }
            }
        }

        return windows.mapNotNull { window ->
            val hasMissing = (window.first..window.last).any { line ->
                coverageStatuses[line] == CoverageStatus.MISSED || coverageStatuses[line] == CoverageStatus.PARTIAL
            }
            if (!hasMissing) {
                return@mapNotNull null
            }
            val snippetLines = window.first..window.last
            val formattedLines = snippetLines.joinToString("\n") { lineNumber ->
                val status = coverageStatuses[lineNumber] ?: CoverageStatus.EXCLUDED
                val symbol = status.symbol
                val content = lines[lineNumber - 1].replace("\t", "    ")
                String.format("%s %4d | %s", symbol, lineNumber, content)
            }
            Snippet(
                startLine = window.first,
                endLine = window.last,
                content = formattedLines
            )
        }
    }

    private fun writeReports(outputDirectory: java.io.File, fileReports: List<FileReport>) {
        val filesDir = outputDirectory.resolve("files")
        filesDir.mkdirs()

        val indexContent = buildString {
            appendLine("# Kover Markdown Coverage Report")
            appendLine()
            appendLine(symbolKey())
            appendLine()
            appendLine("## Files with missing coverage")
            if (fileReports.isEmpty()) {
                appendLine()
                appendLine("All reported lines are covered.")
            } else {
                appendLine()
                fileReports.forEach { report ->
                    val reportFileName = report.relativePath.replace('/', '_').replace('\\', '_') + ".md"
                    val summary = "missed ${report.summary.missedLines}, partial ${report.summary.partialLines}"
                    appendLine("- [${report.relativePath}](files/$reportFileName) ($summary)")
                }
            }
        }
        outputDirectory.resolve("index.md").writeText(indexContent)

        fileReports.forEach { report ->
            val reportFileName = report.relativePath.replace('/', '_').replace('\\', '_') + ".md"
            val reportContent = buildString {
                appendLine("# ${report.relativePath}")
                appendLine()
                appendLine(symbolKey())
                appendLine()
                report.snippets.forEach { snippet ->
                    appendLine("## Lines ${snippet.startLine}-${snippet.endLine}")
                    appendLine()
                    appendLine("Location: `${report.relativePath}:${snippet.startLine}-${snippet.endLine}`")
                    appendLine()
                    appendLine("```kotlin")
                    appendLine(snippet.content)
                    appendLine("```")
                    appendLine()
                }
            }
            filesDir.resolve(reportFileName).writeText(reportContent)
        }
    }

    private fun symbolKey(): String = buildString {
        appendLine("**Key**")
        appendLine()
        appendLine("- 🟢 Covered")
        appendLine("- 🔴 Missed")
        appendLine("- 🟡 Partially covered (missing branches or instructions)")
        appendLine("- ⚪ Excluded or not reported")
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

private data class Snippet(
    val startLine: Int,
    val endLine: Int,
    val content: String
)

private data class FileReport(
    val relativePath: String,
    val snippets: List<Snippet>,
    val summary: CoverageSummary
)

private data class CoverageSummary(
    val missedLines: Int,
    val partialLines: Int
)
