package com.example.bingelist.presentation.detail

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.bingelist.domain.model.Credit
import com.example.bingelist.domain.model.Drama
import java.util.Locale

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel = hiltViewModel(),
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollState = rememberLazyListState()

    // Premium Color Palette
    val primaryBg = Color(0xFF121212) // Deep Charcoal
    val accentColor = Color(0xFF3D5AFE) // Electric Blue Accent

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(primaryBg)
    ) {
        when (val currentState = state) {
            is DetailState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = accentColor
                )
            }
            is DetailState.Error -> {
                Text(
                    text = currentState.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is DetailState.Success -> {
                val drama = currentState.drama

                // Hero Background Section
                with(sharedTransitionScope) {
                    HeroBackground(
                        imageUrl = drama.imageUrl,
                        overlayColor = primaryBg,
                        modifier = Modifier.sharedElement(
                            sharedContentState = rememberSharedContentState(key = "image-${drama.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    )
                }

                LazyColumn(
                    state = scrollState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Spacer(modifier = Modifier.height(320.dp))
                    }

                    item {
                        // Main Content Card with Overlap
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = primaryBg,
                            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 32.dp)
                            ) {
                                DetailHeaderSection(
                                    drama = drama,
                                    sharedTransitionScope = sharedTransitionScope,
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                                
                                AnimatedVisibility(
                                    visible = !currentState.isInitialData,
                                    enter = fadeIn() + expandVertically(),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    DetailInfoSection(drama, currentState.credits)
                                }
                                
                                Spacer(modifier = Modifier.height(100.dp))
                            }
                        }
                    }
                }

                // Sticky Header
                DetailStickyHeader(
                    title = drama.title,
                    showTitle = scrollState.firstVisibleItemIndex > 0 || scrollState.firstVisibleItemScrollOffset > 300,
                    onBack = onBack,
                    onDelete = {
                        viewModel.deleteDrama(drama) {
                            onBack()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun HeroBackground(
    imageUrl: String,
    overlayColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(450.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        // Sophisticated Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            overlayColor.copy(alpha = 0.2f),
                            overlayColor.copy(alpha = 0.8f),
                            overlayColor
                        ),
                        startY = 0f
                    )
                )
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DetailHeaderSection(
    drama: Drama,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        with(sharedTransitionScope) {
            Text(
                text = drama.title,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp,
                    lineHeight = 40.sp
                ),
                color = Color.White,
                modifier = Modifier.sharedElement(
                    sharedContentState = rememberSharedContentState(key = "title-${drama.id}"),
                    animatedVisibilityScope = animatedVisibilityScope
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MetadataItem(
                icon = Icons.Default.Star,
                text = String.format(Locale.getDefault(), "%.1f", drama.rating),
                iconColor = Color(0xFFFFD700)
            )
            MetadataItem(text = if (drama.year > 0) drama.year.toString() else "N/A")
            MetadataItem(text = drama.status.ifBlank { "N/A" })
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            drama.genre.forEach { genre ->
                GenreTag(genre)
            }
        }
    }
}

@Composable
fun MetadataItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    text: String,
    iconColor: Color = Color.Gray
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun GenreTag(genre: String) {
    Surface(
        color = Color.White.copy(alpha = 0.08f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Text(
            text = genre,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelLarge,
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}

@Composable
fun DetailInfoSection(drama: Drama, credits: List<Credit>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp)
    ) {
        Text(
            text = "Synopsis",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = drama.description.ifBlank { "No synopsis available." },
            style = MaterialTheme.typography.bodyLarge.copy(
                lineHeight = 28.sp,
                letterSpacing = 0.2.sp
            ),
            color = Color.White.copy(alpha = 0.7f)
        )

        if (credits.isNotEmpty()) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Cast & Crew",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // Horizontal Scroll for Cast
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                credits.forEach { credit ->
                    CastItem(credit)
                }
            }
        }
    }
}

@Composable
fun CastItem(credit: Credit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(credit.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = credit.name,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = credit.name,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
        Text(
            text = credit.characterOrJob,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.5f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DetailStickyHeader(
    title: String,
    showTitle: Boolean,
    onBack: () -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.Black.copy(alpha = 0.3f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }

        AnimatedVisibility(
            visible = showTitle,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 60.dp)
            )
        }
    }
}
