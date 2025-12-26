package com.hemnet.test.feature.properties

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.ExperimentalMaterialApi
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.hemnet.test.base.Content
import com.hemnet.test.domain.model.Property
import com.hemnet.test.domain.model.PropertyType
import com.hemnet.test.feature.common.MealImage
import com.hemnet.test.ui.typography
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@ExperimentalFoundationApi
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
                label = { Text("Query") },
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
                    Text(text = "Clear")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = {
                    viewModel.submitQuery(query)
                    coroutineScope.launch {
                        modalBottomSheetState.hide()
                    }
                }) {
                    Text(text = "Search")
                }
            }
        }
    }, sheetState = modalBottomSheetState) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("Food App")
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
                Column {
                    PropertiesFilterChips(viewModel)
                    Content(viewModel) { state ->
                        Column {
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
                                PropertiesScreenContent(state, navigateToDetail)
                            }
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun PropertiesFilterChips(viewModel: PropertiesViewModel) {

    val state by viewModel.state.collectAsState()
    val selectedType = state.propertyType

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

@ExperimentalFoundationApi
@Composable
fun PropertiesScreenContent(
    state: PropertiesViewState,
    navigateToDetail: (Property) -> Unit
) {
    if (state.filteredProperties.isEmpty()) {
        Text(text = "No property found")
    } else {
        val orientation = LocalConfiguration.current.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.filteredProperties) { meal ->
                    Divider(thickness = 8.dp)
                    PropertyRowComposable(meal, navigateToDetail)
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
                itemsIndexed(state.filteredProperties) { index, meal ->
                    val isFirstColumn = index % 2 == 0
                    Column(
                        Modifier.padding(
                            if (isFirstColumn) 8.dp else 4.dp,
                            8.dp,
                            if (isFirstColumn) 4.dp else 8.dp,
                            0.dp
                        )
                    ) {
                        PropertyRowComposable(meal, navigateToDetail)
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
        MealImage(property.image, Modifier.size(160.dp))
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
        color = if (selected) Color(0xFFB9FBC0) else Color.White,
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