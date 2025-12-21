package input.storygen

class StoryGenerationRepository(
    private val partsDataSource: StoryPartsDataSource,
    private val documentsDataSource: StoryDocumentsDataSource,
) {
    suspend fun generate(plan: StoryPlan): StoryGenerationResult {
        val traversal = TraversalContext(
            plan = plan,
            collectedParts = linkedMapOf(),
            featuredImageHints = mutableListOf(),
        )

        traverseBranch(
            targetPartId = plan.startPartId,
            parentChoice = null,
            depth = 1,
            traversal = traversal,
        )

        val featuredImagePath = resolveFeaturedImagePath(plan, traversal.featuredImageHints, traversal.collectedParts.values)

        val document = StoryDocument(
            id = plan.storyId,
            title = plan.title,
            startPartId = plan.startPartId,
            featuredImagePath = featuredImagePath,
            parts = traversal.collectedParts.values.toList(),
        )

        val savedPath = documentsDataSource.save(document, plan.language)
        return StoryGenerationResult(document = document, savedPath = savedPath)
    }

    private suspend fun traverseBranch(
        targetPartId: String,
        parentChoice: StoryBranch?,
        depth: Int,
        traversal: TraversalContext,
    ) {
        val stepsRemaining = (traversal.plan.maxDepth - depth).coerceAtLeast(0)
        val request = StoryModelRequest(
            storyId = traversal.plan.storyId,
            title = traversal.plan.title,
            language = traversal.plan.language,
            targetPartId = targetPartId,
            parentChoice = parentChoice,
            currentDepth = depth,
            maxDepth = traversal.plan.maxDepth,
            stepsRemaining = stepsRemaining,
        )

        val modelResult = partsDataSource.generatePart(request)
        require(modelResult.part.id == targetPartId) {
            "Expected part '${targetPartId}' but model returned '${modelResult.part.id}'"
        }
        require(traversal.collectedParts[targetPartId] == null) {
            "Story already has a part with id '$targetPartId'"
        }

        modelResult.part.featuredImagePath?.takeIf { it.isNotBlank() }?.let(traversal.featuredImageHints::add)

        val storyPart = StoryPart(
            id = modelResult.part.id,
            content = modelResult.part.content,
            choice = parentChoice?.let { StoryChoice(text = it.choiceText, parentPartId = it.parentPartId) },
        )
        traversal.collectedParts[storyPart.id] = storyPart

        if (stepsRemaining == 0) {
            return
        }

        modelResult.choices.forEach { choice ->
            traverseBranch(
                targetPartId = choice.nextPartId,
                parentChoice = StoryBranch(parentPartId = storyPart.id, choiceText = choice.text),
                depth = depth + 1,
                traversal = traversal,
            )
        }
    }

    private fun resolveFeaturedImagePath(
        plan: StoryPlan,
        featuredImageHints: List<String>,
        parts: Collection<StoryPart>,
    ): String {
        if (!plan.featuredImagePath.isNullOrBlank()) {
            return plan.featuredImagePath
        }
        val fromModel = featuredImageHints.firstOrNull { it.isNotBlank() }
        if (fromModel != null) {
            return fromModel
        }
        val fromContent = parts.asSequence()
            .mapNotNull { part -> part.content.filterIsInstance<StoryImage>().firstOrNull()?.path }
            .firstOrNull()
        return fromContent ?: ""
    }

    private data class TraversalContext(
        val plan: StoryPlan,
        val collectedParts: LinkedHashMap<String, StoryPart>,
        val featuredImageHints: MutableList<String>,
    )
}
