package input.storygen

import ai.koog.prompt.dsl.prompt
import ai.koog.prompt.executor.clients.google.GoogleLLMClient
import ai.koog.prompt.llm.LLMCapability
import ai.koog.prompt.llm.LLMProvider
import ai.koog.prompt.llm.LLModel
import ai.koog.prompt.executor.llms.SingleLLMPromptExecutor
import ai.koog.prompt.text.text
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Properties
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class KoogStoryPartsDataSource(
    private val promptExecutor: SingleLLMPromptExecutor,
    private val model: LLModel = defaultModel,
    private val json: Json = Json { ignoreUnknownKeys = true }
) : StoryPartsDataSource {
    override suspend fun generatePart(request: StoryModelRequest): StoryModelResult {
        val prompt = buildPrompt(request)
        val responses = promptExecutor.execute(prompt, model)
        val content = responses.firstOrNull()?.content?.takeIf { it.isNotBlank() }
            ?: error("No response returned for part '${request.targetPartId}'")

        return json.decodeFromString(StoryModelResult.serializer(), content)
    }

    private fun buildPrompt(request: StoryModelRequest) = prompt(
        id = "story-part-${request.targetPartId}",
    ) {
        system {
            text {
                +"""
                You are an interactive storyteller.
                Always answer with JSON shaped exactly like this:
                {
                  \"part\": {
                    \"id\": \"<must match the requested part id>\",
                    \"content\": [
                      {\"type\":\"paragraph\", \"sentences\":[""".trimIndent()
                + """
                        "short sentences written for readers" ]},
                      {\"type\":\"image\", \"path\":\"key\", \"contentDescription\":\"alt text\"}
                    ]
                  },
                  \"choices\": [ {\"nextPartId\":\"next-id\", \"text\":\"What the reader sees\"} ]
                }
                To end a branch, return an empty choices array.
                The maximum depth for any branch is ${request.maxDepth}; you are currently at depth ${request.currentDepth} with ${request.stepsRemaining} steps remaining.
                You may end earlier when the scene feels complete.
                Each paragraph should keep sentences concise.
                """.trimIndent()
            }
        }
        user {
            text {
                +"Story id: ${request.storyId}"
                +"Story title: ${request.title}"
                +"Target language: ${request.language.label} (${request.language.code})"
                val parentChoice = request.parentChoice
                if (parentChoice == null) {
                    +"Begin the opening scene for readers."
                } else {
                    +"Continue the story after the choice '${parentChoice.choiceText}'."
                    +"It follows part '${parentChoice.parentPartId}'."
                }
                +"Use the provided part id: ${request.targetPartId}."
                +"Keep choices optional; end the branch whenever it feels natural."
            }
        }
    }

    companion object {
        private val defaultModel = LLModel(
            provider = LLMProvider.Google,
            id = "gemini-3-flash-preview",
            capabilities = listOf(
                LLMCapability.Completion,
                LLMCapability.MultipleChoices,
                LLMCapability.Temperature,
                LLMCapability.Schema.JSON.Basic,
            ),
            contextLength = 1_048_576,
            maxOutputTokens = 8_192,
        )

        fun fromLocalProperties(
            storiesJson: Json = Json { ignoreUnknownKeys = true },
            root: Path = Paths.get("."),
            propertyName: String = "googleAi.apiKey",
        ): KoogStoryPartsDataSource? {
            val apiKey = loadApiKey(root = root, propertyName = propertyName) ?: return null
            val client = GoogleLLMClient(apiKey)
            return KoogStoryPartsDataSource(
                promptExecutor = SingleLLMPromptExecutor(client),
                json = storiesJson,
            )
        }

        private fun loadApiKey(root: Path, propertyName: String): String? {
            val localProperties = root.resolve("local.properties")
            if (!Files.exists(localProperties)) {
                return null
            }
            val properties = Properties()
            localProperties.toFile().inputStream().use(properties::load)
            val value = properties.getProperty(propertyName)?.trim()
            return value?.takeIf { it.isNotEmpty() }
        }
    }
}
