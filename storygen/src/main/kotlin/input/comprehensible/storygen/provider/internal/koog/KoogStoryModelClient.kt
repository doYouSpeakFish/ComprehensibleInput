package input.comprehensible.storygen.provider.internal.koog

import ai.koog.agents.core.agent.AIAgent
import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.executor.llms.all.simpleGoogleAIExecutor
import ai.koog.prompt.llm.LLModel
import input.comprehensible.storygen.core.StoryModelPrompt
import input.comprehensible.storygen.provider.StoryModelClient
import input.comprehensible.storygen.provider.StoryModelClientException
import input.comprehensible.storygen.provider.StoryModelResponseParser
import input.comprehensible.storygen.provider.StoryModelSegment

internal class KoogStoryModelClient(
    apiKey: String,
    model: LLModel = GoogleModels.Gemini2_5Pro,
    private val parser: StoryModelResponseParser = StoryModelResponseParser(),
) : StoryModelClient {
    private val executor = simpleGoogleAIExecutor(apiKey)
    private val resolvedModel = model

    override suspend fun requestSegment(prompt: StoryModelPrompt): StoryModelSegment {
        val rendered = prompt.render()
        val agent = AIAgent(
            promptExecutor = executor,
            systemPrompt = "You collaborate with a human storyteller to craft branching adventures.",
            llmModel = resolvedModel,
        )
        val result = runCatching { agent.run(rendered) }
        runCatching { agent.close() }
        val response = result.getOrElse { error ->
            throw StoryModelClientException("Story provider request failed", error)
        }
        return parser.parse(response)
    }
}
