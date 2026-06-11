package input.comprehensible.ui.textadventure

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import input.comprehensible.feature.textadventure.R
import input.comprehensible.ui.components.LanguageSelection
import input.comprehensible.ui.components.LanguageSelector
import input.comprehensible.ui.theme.homeOptionCardColor

// The warm orange that runs through this screen: the early-access eyebrow and the primary actions
// share it, matching the supplied design.
private val AdventureAccent = Color(0xFFE8590C)

@Composable
internal fun TextAdventuresListScreen(
    onSignInClick: () -> Unit,
    onCreateAccountClick: () -> Unit,
    onStartAdventure: () -> Unit,
    onAdventureClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TextAdventuresListViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle(
        initialValue = TextAdventuresListUiState.INITIAL,
    )
    TextAdventuresListScreen(
        state = state,
        onSignInClick = onSignInClick,
        onCreateAccountClick = onCreateAccountClick,
        onStartAdventure = onStartAdventure,
        onAdventureClick = onAdventureClick,
        onSettingsClick = onSettingsClick,
        onDeleteAdventure = viewModel::onDeleteAdventure,
        onLearningLanguageSelected = viewModel::onLearningLanguageSelected,
        onTranslationLanguageSelected = viewModel::onTranslationLanguageSelected,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TextAdventuresListScreen(
    state: TextAdventuresListUiState,
    onSignInClick: () -> Unit,
    onCreateAccountClick: () -> Unit,
    onStartAdventure: () -> Unit,
    onAdventureClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onDeleteAdventure: (String) -> Unit,
    onLearningLanguageSelected: (LanguageSelection) -> Unit,
    onTranslationLanguageSelected: (LanguageSelection) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.testTag("text_adventures_screen"),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.text_adventures_title)) },
                actions = {
                    LanguageSelector(
                        leaningLanguage = state.learningLanguage,
                        translationLanguage = state.translationLanguage,
                        languageOptions = state.languagesAvailable,
                        onLanguageSelected = onLearningLanguageSelected,
                        onTranslationLanguageSelected = onTranslationLanguageSelected,
                    )
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.text_adventures_settings_content_description),
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
        when {
            !state.isSignedIn -> SignedOutContent(
                modifier = contentModifier,
                onSignInClick = onSignInClick,
                onCreateAccountClick = onCreateAccountClick,
            )

            state.adventures.isNotEmpty() -> AdventureListContent(
                modifier = contentModifier,
                state = state,
                onStartAdventure = onStartAdventure,
                onAdventureClick = onAdventureClick,
                onDeleteAdventure = onDeleteAdventure,
            )

            else -> SignedInEmptyContent(
                modifier = contentModifier,
                state = state,
                onStartAdventure = onStartAdventure,
            )
        }
    }
}

@Composable
private fun AdventureListContent(
    modifier: Modifier,
    state: TextAdventuresListUiState,
    onStartAdventure: () -> Unit,
    onAdventureClick: (String) -> Unit,
    onDeleteAdventure: (String) -> Unit,
) {
    LazyColumn(
        modifier = modifier.testTag("text_adventures_list"),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { Intro() }
        item { EarlyAccessCard() }
        item { StartAdventureButton(onStartAdventure) }
        if (state.showError) item { StatusBanner(stringResource(R.string.text_adventures_error), "text_adventures_error") }
        if (state.showBusyMessage) item { StatusBanner(stringResource(R.string.text_adventure_busy), "text_adventures_busy") }
        items(items = state.adventures, key = { it.id }) { adventure ->
            AdventureRow(
                adventure = adventure,
                onClick = { onAdventureClick(adventure.id) },
                onDelete = { onDeleteAdventure(adventure.id) },
            )
        }
    }
}

@Composable
private fun SignedInEmptyContent(
    modifier: Modifier,
    state: TextAdventuresListUiState,
    onStartAdventure: () -> Unit,
) {
    Column(
        modifier = modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Intro()
        EarlyAccessCard()
        StartAdventureButton(onStartAdventure)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            when {
                state.isLoading -> CircularProgressIndicator(modifier = Modifier.testTag("text_adventures_loading"))
                state.showError -> CenteredMessage(stringResource(R.string.text_adventures_error), "text_adventures_error")
                state.showBusyMessage -> CenteredMessage(stringResource(R.string.text_adventure_busy), "text_adventures_busy")
                else -> CenteredMessage(stringResource(R.string.text_adventures_empty), "text_adventures_empty")
            }
        }
    }
}

@Composable
private fun SignedOutContent(
    modifier: Modifier,
    onSignInClick: () -> Unit,
    onCreateAccountClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Intro()
        EarlyAccessCard()
        SignInCard(onSignInClick = onSignInClick, onCreateAccountClick = onCreateAccountClick)
    }
}

/** The short lead-in shown under the top bar in every state. */
@Composable
private fun Intro() {
    Text(
        text = stringResource(R.string.text_adventures_subtitle),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

/**
 * The early-access card, shown in every state. A coloured eyebrow and explanation sit beside a
 * marker-style illustration, matching the home screen's warm option-card styling.
 */
@Composable
private fun EarlyAccessCard() {
    OptionCard(modifier = Modifier.testTag("text_adventures_early_access")) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = stringResource(R.string.text_adventures_early_access_eyebrow),
                    color = AdventureAccent,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    text = stringResource(R.string.text_adventures_early_access),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Image(
                painter = painterResource(R.drawable.text_adventure_early_access),
                contentDescription = stringResource(R.string.text_adventures_early_access_image_content_description),
                modifier = Modifier.size(96.dp),
                contentScale = ContentScale.Fit,
            )
        }
    }
}

@Composable
private fun StartAdventureButton(onStartAdventure: () -> Unit) {
    Button(
        onClick = onStartAdventure,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .testTag("text_adventures_new_button"),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AdventureAccent, contentColor = Color.White),
    ) {
        Icon(Icons.Default.Add, contentDescription = null)
        Text(
            text = stringResource(R.string.text_adventures_new),
            modifier = Modifier.padding(start = 8.dp),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
private fun SignInCard(onSignInClick: () -> Unit, onCreateAccountClick: () -> Unit) {
    OptionCard(modifier = Modifier.testTag("text_adventures_sign_in_prompt")) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.text_adventure_sign_in),
                contentDescription = stringResource(R.string.text_adventures_sign_in_image_content_description),
                modifier = Modifier.size(88.dp),
                contentScale = ContentScale.Fit,
            )
            Text(
                text = stringResource(R.string.text_adventures_sign_in_message),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(R.string.text_adventures_sign_in_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Button(
                onClick = onSignInClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
                    .testTag("text_adventures_sign_in_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AdventureAccent, contentColor = Color.White),
            ) {
                Text(stringResource(R.string.text_adventures_sign_in_button), style = MaterialTheme.typography.titleMedium)
            }
            OutlinedButton(
                onClick = onCreateAccountClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
                    .testTag("text_adventures_create_account_button"),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(stringResource(R.string.text_adventures_create_account_button), style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

/** The shared warm option card used across this screen, mirroring the home screen's cards. */
@Composable
internal fun OptionCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.homeOptionCardColor())
    val shape = MaterialTheme.shapes.extraLarge
    val border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    val elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            shape = shape,
            colors = colors,
            border = border,
            elevation = elevation,
            content = { content() },
        )
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = shape,
            colors = colors,
            border = border,
            elevation = elevation,
            content = { content() },
        )
    }
}

@Composable
private fun CenteredMessage(text: String, testTag: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.testTag(testTag),
    )
}

/** An inline error/busy banner shown above a populated list. */
@Composable
private fun StatusBanner(text: String, testTag: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.testTag(testTag),
    )
}
