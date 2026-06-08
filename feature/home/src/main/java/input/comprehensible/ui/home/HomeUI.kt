package input.comprehensible.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import input.comprehensible.feature.home.R
import input.comprehensible.ui.theme.ComprehensibleInputTheme
import input.comprehensible.util.DefaultPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreen(
    textAdventuresEnabled: Boolean,
    onStoriesClick: () -> Unit,
    onTextAdventuresClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.home_title)) },
                actions = {
                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier.testTag("home_settings_button"),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.home_settings_content_description),
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(
                start = 20.dp,
                top = 20.dp,
                end = 20.dp,
                bottom = 32.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                HomeIntroduction()
            }
            item {
                HomeOptionCard(
                    eyebrow = stringResource(R.string.home_stories_eyebrow),
                    label = stringResource(R.string.home_stories),
                    description = stringResource(R.string.home_stories_description),
                    lightImage = R.drawable.home_stories_marker_light,
                    darkImage = R.drawable.home_stories_marker_dark,
                    accent = Color(0xFFE75F3C),
                    onClick = onStoriesClick,
                    modifier = Modifier.testTag("home_stories_card"),
                )
            }
            if (textAdventuresEnabled) {
                item {
                    HomeOptionCard(
                        eyebrow = stringResource(R.string.home_text_adventures_eyebrow),
                        label = stringResource(R.string.home_text_adventures),
                        description = stringResource(R.string.home_text_adventures_description),
                        lightImage = R.drawable.home_adventures_marker_light,
                        darkImage = R.drawable.home_adventures_marker_dark,
                        accent = Color(0xFF159BC5),
                        onClick = onTextAdventuresClick,
                        modifier = Modifier.testTag("home_text_adventures_card"),
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeIntroduction() {
    Column(
        modifier = Modifier.padding(bottom = 6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = stringResource(R.string.home_intro_eyebrow),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(R.string.home_intro_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
        )
        Text(
            text = stringResource(R.string.home_intro_description),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun HomeOptionCard(
    eyebrow: String,
    label: String,
    description: String,
    lightImage: Int,
    darkImage: Int,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDarkTheme = isSystemInDarkTheme()
    val cardHeight = 220.dp * LocalDensity.current.fontScale.coerceAtLeast(1f)
    val cardColor = if (isDarkTheme) {
        Color(0xFF181A18)
    } else {
        Color(0xFFFFFBF2)
    }

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(cardHeight),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(cardHeight),
        ) {
            Image(
                painter = painterResource(if (isDarkTheme) darkImage else lightImage),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            0f to cardColor,
                            0.5f to cardColor.copy(alpha = 0.97f),
                            0.76f to Color.Transparent,
                        ),
                    ),
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.72f)
                    .align(Alignment.TopStart)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = eyebrow,
                    color = accent,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                )
                Text(
                    text = description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = accent,
                )
            }
        }
    }
}

@DefaultPreview
@Composable
fun PreviewHome() {
    ComprehensibleInputTheme {
        HomeScreen(
            textAdventuresEnabled = true,
            onStoriesClick = {},
            onTextAdventuresClick = {},
            onSettingsClick = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@DefaultPreview
@Composable
fun PreviewHomeTextAdventuresDisabled() {
    ComprehensibleInputTheme {
        HomeScreen(
            textAdventuresEnabled = false,
            onStoriesClick = {},
            onTextAdventuresClick = {},
            onSettingsClick = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
