package input.comprehensible.storygen.core

data class StoryModelPrompt(
    val genre: String,
    val inspirationWords: List<String>,
    val pathSummary: List<BranchStep>,
    val depth: Int,
    val maxDepth: Int,
) {
    fun render(): String {
        val remaining = maxDepth - depth
        val pathText = if (pathSummary.isEmpty()) {
            "This is the opening of the adventure."
        } else {
            buildString {
                appendLine("The story has followed this path so far:")
                pathSummary.forEachIndexed { index, step ->
                    appendLine("${index + 1}. Choice: \"${step.choice}\" -> ${step.outcome}")
                }
            }
        }
        val closeness = buildString {
            append(depth)
            append(" of ")
            append(maxDepth)
            append(" segments have been written. ")
            append(remaining)
            append(" segments remain before the branch must end.")
        }
        val instructions = buildString {
            append("Respond with JSON shaped as ")
            append("{\"title\": string, \"narrative\": string, \"isEnding\": boolean, ")
            append("\"choices\": [{\"prompt\": string, \"summary\": string}]}.")
        }
        val endingRule = buildString {
            append("If \"isEnding\" is true the choices array must be empty. ")
            append("If \"isEnding\" is false you must provide between 2 and 3 choices.")
        }
        val depthRule = buildString {
            append("If depth ")
            append(depth)
            append(" equals the maximum ")
            append(maxDepth)
            append(", you must end the branch without choices.")
        }
        val genreLine = "Genre: $genre"
        val inspirationLine = "Inspiration words: ${inspirationWords.joinToString()}."
        return buildString {
            appendLine("You are a collaborative storyteller helping craft a branching adventure.")
            appendLine(genreLine)
            appendLine(inspirationLine)
            appendLine(pathText)
            appendLine("Focus on the next beat only. $closeness")
            appendLine(instructions)
            appendLine(endingRule)
            appendLine(depthRule)
            appendLine("Always answer with JSON only, without commentary.")
        }
    }
}

data class BranchStep(
    val choice: String,
    val outcome: String,
)
