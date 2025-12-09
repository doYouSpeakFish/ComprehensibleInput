package input.comprehensible.ui.components.storycontent.part

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import input.comprehensible.ui.components.TranslatableText

/**
 * A composable for displaying a part of a stories main content.
 */
@Composable
fun StoryContentPart(
    modifier: Modifier = Modifier,
    selectedSentenceIndex: Int? = null,
    selectedChoiceIndex: Int? = null,
    isSelectionTranslated: Boolean = false,
    onSentenceSelected: (Int) -> Unit = {},
    onChoiceTextSelected: (Int) -> Unit = {},
    state: StoryContentPartUiState,
) {
    Box(modifier) {
        when (state) {
            is StoryContentPartUiState.Paragraph -> Paragraph(
                selectedSentenceIndex = selectedSentenceIndex,
                isSelectionTranslated = isSelectionTranslated,
                onSentenceSelected = onSentenceSelected,
                state = state,
            )
            is StoryContentPartUiState.Image -> StoryImage(state = state)
            is StoryContentPartUiState.Choices -> StoryChoices(
                state = state,
                selectedOptionIndex = selectedChoiceIndex,
                isSelectionTranslated = isSelectionTranslated,
                onOptionTextSelected = onChoiceTextSelected,
            )
        }
    }
}

@Composable
private fun Paragraph(
    modifier: Modifier = Modifier,
    selectedSentenceIndex: Int?,
    isSelectionTranslated: Boolean,
    onSentenceSelected: (Int) -> Unit,
    state: StoryContentPartUiState.Paragraph,
) {
    TranslatableText(
        modifier = modifier,
        sentences = state.sentences,
        translatedSentences = state.translatedSentences,
        selectedSentenceIndex = selectedSentenceIndex,
        isSelectionTranslated = isSelectionTranslated,
        onSentenceSelected = onSentenceSelected,
    )
}

@Composable
private fun StoryImage(
    modifier: Modifier = Modifier,
    state: StoryContentPartUiState.Image
) {
    Box(modifier) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground,
                    shape = RoundedCornerShape(16.dp)
                ),
            bitmap = state.bitmap.asImageBitmap(),
            contentDescription = state.contentDescription,
            contentScale = ContentScale.FillWidth,
        )
    }
}

@Composable
private fun StoryChoices(
    modifier: Modifier = Modifier,
    state: StoryContentPartUiState.Choices,
    selectedOptionIndex: Int?,
    isSelectionTranslated: Boolean,
    onOptionTextSelected: (Int) -> Unit,
) {
    Column(
        modifier = modifier.padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        state.options.forEachIndexed { index, option ->
            Choice(
                modifier = Modifier.fillMaxWidth(),
                isSelected = selectedOptionIndex == index,
                isSelectionTranslated = isSelectionTranslated,
                onOptionTextSelected = { onOptionTextSelected(index) },
                option = option,
            )
        }
    }
}

@Composable
private fun Choice(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    isSelectionTranslated: Boolean,
    onOptionTextSelected: () -> Unit,
    option: StoryContentPartUiState.Choices.Option,
) {
    val colorScheme = MaterialTheme.colorScheme
    val buttonContainerColor =
        if (option.isChosen) colorScheme.onBackground else colorScheme.background
    val buttonContentColor =
        if (option.isChosen) colorScheme.background else colorScheme.onBackground
    val buttonIcon =
        if (option.isChosen) Icons.Filled.Check else Icons.AutoMirrored.Filled.ArrowForward
    Box(modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                modifier = Modifier
                    .semantics {
                        role = Role.RadioButton
                        selected = option.isChosen
                    }
                    .testTag("story_choice_button_${option.id}")
                    .border(
                        width = 1.dp,
                        shape = CircleShape,
                        color = colorScheme.onBackground,
                    ),
                onClick = option.onClick,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = buttonContainerColor,
                    contentColor = buttonContentColor,
                ),
                shape = CircleShape,
            ) {
                Icon(
                    imageVector = buttonIcon,
                    contentDescription = null,
                )
            }
            TranslatableText(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(weight = 1f),
                text = option.text,
                onTextSelected = onOptionTextSelected,
                translation = option.translatedText,
                textStyle = MaterialTheme.typography.headlineSmall,
                isTextSelected = isSelected,
                isTextTranslated = isSelectionTranslated,
            )
        }
    }
}
