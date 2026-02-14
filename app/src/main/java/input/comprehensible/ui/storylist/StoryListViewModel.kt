package input.comprehensible.ui.storylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import input.comprehensible.data.languages.LanguageSettingsRepository
import input.comprehensible.data.stories.StoriesListResult
import input.comprehensible.data.textadventures.TextAdventuresListResult
import input.comprehensible.ui.components.LanguageSelection
import input.comprehensible.usecases.GetStoriesListUseCase
import input.comprehensible.usecases.GetTextAdventuresListUseCase
import input.comprehensible.usecases.StartTextAdventureUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * A ViewModel for the StoryList screen.
 */
class StoryListViewModel(
    private val languageSettingsRepository: LanguageSettingsRepository = LanguageSettingsRepository(),
    getStoriesListUseCase: GetStoriesListUseCase = GetStoriesListUseCase(),
    getTextAdventuresListUseCase: GetTextAdventuresListUseCase = GetTextAdventuresListUseCase(),
    private val startTextAdventureUseCase: StartTextAdventureUseCase = StartTextAdventureUseCase(),
) : ViewModel() {
    private val _events = MutableSharedFlow<StoryListEvent>()
    val events = _events.asSharedFlow()

    val state = combine(
        getStoriesListUseCase(),
        getTextAdventuresListUseCase(),
        languageSettingsRepository.learningLanguage,
        languageSettingsRepository.translationsLanguage,
    ) { storiesResult, adventuresResult, learningLanguage, translationsLanguage ->
        val storyItems = when (storiesResult) {
            is StoriesListResult.Success -> storiesResult.storiesList.stories.map { story ->
                StoryListUiState.StoryListItem.Story(
                    id = story.id,
                    title = story.title,
                    subtitle = story.titleTranslated,
                    featuredImage = story.featuredImage,
                )
            }

            StoriesListResult.Error -> emptyList()
        }
        val adventureItems = when (adventuresResult) {
            is TextAdventuresListResult.Success -> adventuresResult.adventures.map { adventure ->
                StoryListUiState.StoryListItem.TextAdventure(
                    id = adventure.id,
                    title = adventure.title,
                    isComplete = adventure.isComplete,
                )
            }

            TextAdventuresListResult.Error -> emptyList()
        }
        val items = storyItems + adventureItems + StoryListUiState.StoryListItem.StartTextAdventure
        StoryListUiState(
            items = items,
            learningLanguage = LanguageSelection.entries
                .firstOrNull { it.languageCode == learningLanguage },
            translationLanguage = LanguageSelection.entries
                .firstOrNull { it.languageCode == translationsLanguage },
            languagesAvailable = LanguageSelection.entries
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = StoryListUiState.INITIAL
        )

    /**
     * Called when the user selects a language to learn.
     */
    fun onLearningLanguageSelected(learningLanguage: LanguageSelection) {
        viewModelScope.launch {
            languageSettingsRepository.setLearningLanguage(learningLanguage.languageCode)
        }
    }

    /**
     * Called when the user selects a language to learn.
     */
    fun onTranslationLanguageSelected(translationLanguage: LanguageSelection) {
        viewModelScope.launch {
            languageSettingsRepository.setTranslationLanguage(translationLanguage.languageCode)
        }
    }

    fun onStartTextAdventure() {
        viewModelScope.launch {
            val adventureId = startTextAdventureUseCase()
            _events.emit(StoryListEvent.TextAdventureStarted(adventureId))
        }
    }
}

sealed interface StoryListEvent {
    data class TextAdventureStarted(val adventureId: String) : StoryListEvent
}
