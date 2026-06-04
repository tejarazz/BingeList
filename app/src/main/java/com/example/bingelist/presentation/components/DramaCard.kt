package com.example.bingelist.presentation.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.bingelist.domain.model.Drama
import java.util.Locale

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DramaCard(
    drama: Drama,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showAddButton: Boolean = false,
    onAddClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = modifier.aspectRatio(0.7f),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            with(sharedTransitionScope) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(drama.imageUrl)
                        .crossfade(true)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .build(),
                    contentDescription = drama.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .sharedElement(
                            rememberSharedContentState(key = "image-${drama.id}"),
                            animatedVisibilityScope
                        ),
                    contentScale = ContentScale.Crop
                )
            }

            if (showAddButton) {
                IconButton(
                    onClick = { onAddClick() },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(50))
                ) {
                    Icon(
                        imageVector = if (drama.isInWatchlist) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = "Watchlist Status",
                        tint = if (drama.isInWatchlist) Color.Green else Color.White
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.BottomStart)
            ) {
                with(sharedTransitionScope) {
                    Text(
                        text = drama.title,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.sharedElement(
                            rememberSharedContentState(key = "title-${drama.id}"),
                            animatedVisibilityScope
                        )
                    )
                }
                Text(
                    text = "★ ${String.format(Locale.getDefault(), "%.1f", drama.rating)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Yellow
                )
            }
        }
    }
}

@Composable
fun ShimmerDramaCard(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f),
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.7f)
            .clip(RoundedCornerShape(16.dp))
            .background(brush)
    )
}
