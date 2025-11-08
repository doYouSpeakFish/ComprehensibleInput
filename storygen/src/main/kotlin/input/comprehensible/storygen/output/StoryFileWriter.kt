package input.comprehensible.storygen.output

import input.comprehensible.storygen.core.GeneratedStory
import input.comprehensible.storygen.core.StoryNode
import java.nio.file.Files
import java.nio.file.Path
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class StoryFileWriter(
    private val outputDirectory: Path,
) {
    fun write(story: GeneratedStory): Path {
        if (!Files.exists(outputDirectory)) {
            Files.createDirectories(outputDirectory)
        }
        val timestamp = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"))
        val outputFile = outputDirectory.resolve("adventure-$timestamp.md")
        Files.writeString(outputFile, render(story))
        return outputFile
    }

    private fun render(story: GeneratedStory): String {
        return buildString {
            appendLine("# ${story.genre} Adventure")
            appendLine()
            appendLine("Inspiration words: ${story.inspirationWords.joinToString()}")
            appendLine()
            renderNode(story.root, 0)
        }
    }

    private fun StringBuilder.renderNode(node: StoryNode, indent: Int) {
        val prefix = "  ".repeat(indent)
        appendLine("${prefix}## ${node.title}")
        appendLine()
        appendLine("${prefix}${node.narrative}")
        appendLine()
        if (node.choices.isEmpty()) {
            appendLine("${prefix}_The story ends here._")
            appendLine()
        } else {
            appendLine("${prefix}Choices:")
            node.choices.forEachIndexed { index, choice ->
                appendLine("${prefix}${index + 1}. ${choice.prompt} — ${choice.summary}")
                val next = choice.next
                if (next != null) {
                    appendLine()
                    renderNode(next, indent + 1)
                }
            }
        }
    }
}
