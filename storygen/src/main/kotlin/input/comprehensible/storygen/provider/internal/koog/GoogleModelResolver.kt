package input.comprehensible.storygen.provider.internal.koog

import ai.koog.prompt.executor.clients.google.GoogleModels
import ai.koog.prompt.llm.LLModel
import input.comprehensible.storygen.provider.StoryModelClientException

internal object GoogleModelResolver {
    fun resolve(modelId: String): LLModel {
        return when (modelId.lowercase()) {
            GoogleModels.Gemini2_5Pro.id.lowercase() -> GoogleModels.Gemini2_5Pro
            GoogleModels.Gemini2_5Flash.id.lowercase() -> GoogleModels.Gemini2_5Flash
            GoogleModels.Gemini2_5FlashLite.id.lowercase() -> GoogleModels.Gemini2_5FlashLite
            GoogleModels.Gemini2_0Flash.id.lowercase() -> GoogleModels.Gemini2_0Flash
            GoogleModels.Gemini2_0Flash001.id.lowercase() -> GoogleModels.Gemini2_0Flash001
            GoogleModels.Gemini2_0FlashLite.id.lowercase() -> GoogleModels.Gemini2_0FlashLite
            GoogleModels.Gemini2_0FlashLite001.id.lowercase() -> GoogleModels.Gemini2_0FlashLite001
            else -> throw StoryModelClientException("Unknown Google Gemini model: $modelId")
        }
    }
}
