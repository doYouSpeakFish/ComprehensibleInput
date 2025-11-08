package input.comprehensible.storygen.provider

import input.comprehensible.storygen.provider.internal.koog.GoogleModelResolver
import input.comprehensible.storygen.provider.internal.koog.KoogStoryModelClient

object StoryModelClientFactory {
    fun googleGemini(apiKey: String, modelId: String): StoryModelClient {
        val model = GoogleModelResolver.resolve(modelId)
        return KoogStoryModelClient(apiKey = apiKey, model = model)
    }
}
