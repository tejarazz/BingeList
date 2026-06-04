package com.example.bingelist.presentation.watchlist

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.bingelist.domain.model.Drama
import com.example.bingelist.presentation.components.DramaCard
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun WatchlistScreen(
    viewModel: WatchlistViewModel = hiltViewModel(),
    onDramaClick: (Drama) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val gridState = rememberLazyGridState()
    val reorderableState = rememberReorderableLazyGridState(gridState) { from, to ->
        viewModel.onMove(from.index, to.index)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Watchlist") }
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            state = gridState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.dramas, key = { it.id }) { drama ->
                ReorderableItem(reorderableState, key = drama.id) { isDragging ->
                    val scale = if (isDragging) 1.1f else 1.0f
                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                            .zIndex(if (isDragging) 1f else 0f)
                    ) {
                        DramaCard(
                            drama = drama,
                            modifier = Modifier.longPressDraggableHandle(
                                onDragStopped = {
                                    viewModel.onDragFinished()
                                }
                            ),
                            onClick = { onDramaClick(drama) },
                            showAddButton = true,
                            onAddClick = { viewModel.onRemoveClick(drama) },
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    }
                }
            }
        }
    }
}
