package input.comprehensible.ui.textadventure

import androidx.compose.animation.core.animate
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import input.comprehensible.feature.textadventure.R
import input.comprehensible.ui.textadventure.TextAdventuresListUiState.AdventureItem
import input.comprehensible.ui.theme.homeOptionCardColor
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

// The accent shared with the "Text adventures" card on the home screen, so tapping through to this
// screen feels like the same place. Used for the section eyebrow, each adventure's language label
// and the empty/sign-in artwork.
private val AdventureAccent = Color(0xFF159BC5)

// Fraction of a row's width it must be dragged towards the start before releasing deletes it.
private const val DELETE_SWIPE_FRACTION = 0.4f

@Composable
internal fun TextAdventuresListScreen(
    onSignInClick: () -> Unit,
    onStartAdventure: () -> Unit,
    onAdventureClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TextAdventuresListViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle(
        initialValue = TextAdventuresListUiState.INITIAL,
    )
    TextAdventuresListScreen(
        state = state,
        onSignInClick = onSignInClick,
        onStartAdventure = onStartAdventure,
        onAdventureClick = onAdventureClick,
        onDeleteAdventure = viewModel::onDeleteAdventure,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TextAdventuresListScreen(
    state: TextAdventuresListUiState,
    onSignInClick: () -> Unit,
    onStartAdventure: () -> Unit,
    onAdventureClick: (String) -> Unit,
    onDeleteAdventure: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.testTag("text_adventures_screen"),
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.text_adventures_title)) })
        },
        floatingActionButton = {
            if (state.isSignedIn) {
                ExtendedFloatingActionButton(
                    onClick = onStartAdventure,
                    modifier = Modifier.testTag("text_adventures_new_button"),
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text(stringResource(R.string.text_adventures_new)) },
                )
            }
        },
    ) { paddingValues ->
        val hasAdventures = state.isSignedIn && state.adventures.isNotEmpty()
        if (hasAdventures) {
            // A populated list scrolls as a single editorial column, mirroring the home screen: the
            // header and notice sit above the cards and scroll away with them.
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .testTag("text_adventures_list"),
                contentPadding = PaddingValues(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item { ListHeader() }
                item { EarlyAccessNotice() }
                if (state.showError) item { StatusMessage(R.string.text_adventures_error, "text_adventures_error") }
                if (state.showBusyMessage) item { StatusMessage(R.string.text_adventure_busy, "text_adventures_busy") }
                items(items = state.adventures, key = { it.id }) { adventure ->
                    AdventureRow(
                        adventure = adventure,
                        onClick = { onAdventureClick(adventure.id) },
                        onDelete = { onDeleteAdventure(adventure.id) },
                    )
                }
            }
        } else {
            // Every other state keeps the same header and notice, with the state-specific content
            // centred in the space that remains.
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                ListHeader()
                EarlyAccessNotice()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    when {
                        !state.isSignedIn -> SignInPrompt(onSignInClick = onSignInClick)
                        state.isLoading -> CircularProgressIndicator(
                            modifier = Modifier.testTag("text_adventures_loading"),
                        )

                        state.showError -> CenteredMessage(
                            text = stringResource(R.string.text_adventures_error),
                            color = MaterialTheme.colorScheme.error,
                            testTag = "text_adventures_error",
                        )

                        state.showBusyMessage -> CenteredMessage(
                            text = stringResource(R.string.text_adventure_busy),
                            color = MaterialTheme.colorScheme.error,
                            testTag = "text_adventures_busy",
                        )

                        else -> EmptyState()
                    }
                }
            }
        }
    }
}

/**
 * The editorial header that matches the home screen's style: a coloured eyebrow over a bold
 * headline, tying the screen back to the "Text adventures" card the user tapped to get here.
 */
@Composable
private fun ListHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = stringResource(R.string.text_adventures_eyebrow),
            color = AdventureAccent,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Black,
        )
        Text(
            text = stringResource(R.string.text_adventures_headline),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
        )
    }
}

/**
 * A banner explaining that the text adventures feature is free while in early access. Shown above
 * the list (and the sign-in prompt) so every visitor to the screen sees it.
 */
@Composable
private fun EarlyAccessNotice() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .testTag("text_adventures_early_access"),
    ) {
        Text(
            text = stringResource(R.string.text_adventures_early_access),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        )
    }
}

/**
 * A single adventure as a card matching the home option-card style, with swipe-to-delete: dragging
 * the card towards the start reveals a delete background and, past a threshold, deletes it.
 *
 * The offset is driven directly (rather than via [androidx.compose.material3.SwipeToDismissBox]) so
 * that a delete snaps the card straight back to rest. The list removes the adventure optimistically
 * and restores it if the delete fails; snapping (instead of animating) means a restored row is
 * immediately back in place rather than left stranded mid-swipe.
 */
@Composable
private fun AdventureRow(
    adventure: AdventureItem,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    // The live drag offset is held in plain state and updated synchronously while dragging, so the
    // value read in onDragEnd is always current. A delete snaps it straight back to rest; an
    // incomplete swipe springs back. Keyed on the id so a different adventure starts at rest.
    var offsetX by remember(adventure.id) { mutableFloatStateOf(0f) }
    // Swiping is not reachable without a pointer, so expose the same delete as an accessibility
    // action for screen-reader users now that the explicit delete button is gone.
    val deleteLabel = stringResource(R.string.text_adventures_delete_content_description)
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val width = constraints.maxWidth.toFloat()
        val deleteThreshold = width * DELETE_SWIPE_FRACTION
        DeleteBackground(modifier = Modifier.matchParentSize())
        AdventureCard(
            adventure = adventure,
            onClick = onClick,
            modifier = Modifier
                .semantics {
                    customActions = listOf(CustomAccessibilityAction(deleteLabel) { onDelete(); true })
                }
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .pointerInput(adventure.id) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            // Only allow dragging towards the start (left in LTR); never past the edge.
                            offsetX = (offsetX + dragAmount).coerceIn(-width, 0f)
                        },
                        onDragEnd = {
                            if (offsetX <= -deleteThreshold) {
                                onDelete()
                                offsetX = 0f
                            } else {
                                scope.launch { animate(offsetX, 0f) { value, _ -> offsetX = value } }
                            }
                        },
                        onDragCancel = {
                            scope.launch { animate(offsetX, 0f) { value, _ -> offsetX = value } }
                        },
                    )
                },
        )
    }
}

@Composable
private fun AdventureCard(
    adventure: AdventureItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .testTag("adventure_${adventure.title}"),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.homeOptionCardColor()),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AdventureImage(
                imageUrl = adventure.imageUrl,
                testTag = "adventure_image_${adventure.title}",
                modifier = Modifier
                    .size(76.dp)
                    .clip(MaterialTheme.shapes.large),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = languageDisplayName(adventure.language).uppercase(),
                    color = AdventureAccent,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    text = adventure.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (adventure.translatedTitle.isNotBlank()) {
                    Text(
                        text = adventure.translatedTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

/** The background revealed while swiping an adventure away, signalling the delete action. */
@Composable
private fun DeleteBackground(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.extraLarge)
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.CenterEnd,
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = stringResource(R.string.text_adventures_delete_content_description),
            tint = MaterialTheme.colorScheme.onErrorContainer,
        )
    }
}

@Composable
private fun SignInPrompt(onSignInClick: () -> Unit) {
    StatePanel(
        icon = { AccentBadge(Icons.Default.Lock) },
        title = stringResource(R.string.text_adventures_sign_in_message),
        modifier = Modifier.testTag("text_adventures_sign_in_prompt"),
    ) {
        Button(
            onClick = onSignInClick,
            modifier = Modifier.testTag("text_adventures_sign_in_button"),
        ) {
            Text(stringResource(R.string.text_adventures_sign_in_button))
        }
    }
}

@Composable
private fun EmptyState() {
    StatePanel(
        icon = { AccentBadge(Icons.Default.PlayArrow) },
        title = stringResource(R.string.text_adventures_empty_title),
        description = stringResource(R.string.text_adventures_empty),
        descriptionTestTag = "text_adventures_empty",
    )
}

/**
 * The shared layout for the centred states (sign-in and empty): a soft accent badge, a bold title,
 * an optional description and optional action, stacked and centred.
 */
@Composable
private fun StatePanel(
    icon: @Composable () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    descriptionTestTag: String? = null,
    action: @Composable (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        icon()
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        if (description != null) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = descriptionTestTag?.let { Modifier.testTag(it) } ?: Modifier,
            )
        }
        action?.invoke()
    }
}

@Composable
private fun AccentBadge(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(96.dp)
            .clip(CircleShape)
            .background(AdventureAccent.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AdventureAccent,
            modifier = Modifier.size(44.dp),
        )
    }
}

@Composable
private fun CenteredMessage(text: String, color: Color, testTag: String) {
    Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        modifier = Modifier.testTag(testTag),
    )
}

/** An inline error/busy banner shown above a populated list. */
@Composable
private fun StatusMessage(textRes: Int, testTag: String) {
    Text(
        text = stringResource(textRes),
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.testTag(testTag),
    )
}
