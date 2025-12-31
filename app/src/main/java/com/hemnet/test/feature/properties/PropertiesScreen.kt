package com.hemnet.test.feature.properties

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.hemnet.test.R
import com.hemnet.test.common.base.Content
import com.hemnet.test.common.ui.common.PropertyImage
import com.hemnet.test.common.ui.lightGreen
import com.hemnet.test.common.ui.typography
import com.hemnet.test.domain.model.Property
import com.hemnet.test.domain.model.PropertyType
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun PropertiesScreen(
    viewModel: PropertiesViewModel = hiltViewModel(),
    navigateToDetail: (Property) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val modalBottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    ModalBottomSheetLayout(sheetContent = {
        val focusManager = LocalFocusManager.current
        if (modalBottomSheetState.currentValue != ModalBottomSheetValue.Hidden) {
            DisposableEffect(Unit) {
                onDispose {
                    focusManager.clearFocus()
                }
            }
        }
        Column(modifier = Modifier.padding(16.dp)) {
            var query by rememberSaveable { mutableStateOf("") }

            TextField(
                value = query,
                onValueChange = { query = it },
                label = { Text(stringResource(R.string.query)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    viewModel.submitQuery(query)
                    coroutineScope.launch {
                        modalBottomSheetState.hide()
                    }
                }),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.align(Alignment.End)) {
                OutlinedButton(onClick = {
                    viewModel.submitQuery(null)
                    query = ""
                    coroutineScope.launch {
                        modalBottomSheetState.hide()
                    }
                }) {
                    Text(stringResource(R.string.clear))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = {
                    viewModel.submitQuery(query)
                    coroutineScope.launch {
                        modalBottomSheetState.hide()
                    }
                }) {
                    Text(stringResource(R.string.search))
                }
            }
        }
    }, sheetState = modalBottomSheetState) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(stringResource(R.string.hemnet))
                    },
                    actions = {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                modalBottomSheetState.show()
                            }
                        }) {
                            Icon(Icons.Filled.Search, "search")
                        }
                    }
                )
            },
            content = {
                Content(viewModel) { state ->
                    Column {
                        PropertiesFilterChips(state.propertyType, viewModel)
                        SwipeRefresh(
                            state = rememberSwipeRefreshState(state.base.isRefreshing),
                            onRefresh = { viewModel.refresh(true) },
                            indicator = { state, trigger ->
                                SwipeRefreshIndicator(
                                    state,
                                    trigger
                                )
                            },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .fillMaxSize()
                        ) {
                            PropertiesScreenContent(state.filteredProperties, navigateToDetail)
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun PropertiesFilterChips(
    selectedType: PropertyType?,
    viewModel: PropertiesViewModel
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(12.dp)
    ) {
        PropertyType.entries.forEach { filter ->
            val isSelected = filter == selectedType

            CustomFilterChip(
                text = filter.name,
                selected = isSelected,
                onClick = {
                    viewModel.onFilterChanged(
                        if (isSelected) null else filter
                    )
                }
            )
        }
    }
}

@Composable
fun PropertiesScreenContent(
    filteredProperties: List<Property>,
    navigateToDetail: (Property) -> Unit
) {
    if (filteredProperties.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            NoDataFoundAnimation(modifier = Modifier.size(200.dp))
        }
    } else {
        val orientation = LocalConfiguration.current.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredProperties) { property ->
                    Divider(thickness = 8.dp)
                    PropertyRowComposable(property, navigateToDetail)
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.background(
                    MaterialTheme.colors.onSurface.copy(
                        alpha = 0.12f
                    )
                )
            ) {
                itemsIndexed(filteredProperties) { index, property ->
                    val isFirstColumn = index % 2 == 0
                    Column(
                        Modifier.padding(
                            if (isFirstColumn) 8.dp else 4.dp,
                            8.dp,
                            if (isFirstColumn) 4.dp else 8.dp,
                            0.dp
                        )
                    ) {
                        PropertyRowComposable(property, navigateToDetail)
                    }
                }
            }
        }
    }
}

@Composable
fun PropertyRowComposable(
    property: Property,
    navigateToDetail: (Property) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface)
            .clip(RoundedCornerShape(4.dp))
            .padding(16.dp)
            .clickable {
                navigateToDetail.invoke(property)
            }
    ) {
        PropertyImage(property.image, Modifier.size(160.dp))
        Column(
            modifier = Modifier
                .padding(start = 8.dp)
                .align(Alignment.CenterVertically)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    property.area,
                    fontWeight = FontWeight.Bold,
                    style = typography.h6,
                    modifier = Modifier.weight(1f)
                )
            }
            property.municipality?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.body2,
                )
            }
        }
    }
}

@Composable
private fun CustomFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (selected) lightGreen else Color.White,
        contentColor = Color.Black,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun NoDataFoundAnimation(modifier: Modifier = Modifier) {
    val preloaderLottieComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(
            R.raw.no_data_found,
        ),
    )
    val preloaderProgress by animateLottieCompositionAsState(
        preloaderLottieComposition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
    )
    LottieAnimation(
        composition = preloaderLottieComposition,
        progress = preloaderProgress,
        modifier = modifier,
    )
}