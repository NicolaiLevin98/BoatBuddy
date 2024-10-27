package no.uio.ifi.titanic.ui.openstreetmapScreen

import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import no.uio.ifi.titanic.DataStoreViewModel
import no.uio.ifi.titanic.openstreetmap.OsmMapView
import no.uio.ifi.titanic.ui.theme.TitanicTheme

@Composable
fun MapScreen(
    navController: NavController,
    themeViewModel: DataStoreViewModel,
    viewModel: MapScreenViewModel = viewModel(factory = MapScreenViewModelFactory(LocalContext.current))
) {
    TitanicTheme(
        darkTheme = themeViewModel.darkModeFlow.collectAsState(initial = false).value
    ) {
        val searchLocation by viewModel.searchText.collectAsState()
        val mapScreenUiState = viewModel.mapInfoUiState.collectAsState().value
        var isWeatherExpanded by rememberSaveable { mutableStateOf(false) }

        val snackbarHostState = remember { SnackbarHostState() }
        val isNetworkAvailable by viewModel.networkAvailable.collectAsState()
        val scope = rememberCoroutineScope()

        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        val weatherDisplayModifier = if (isLandscape) {
            Modifier
                .width(400.dp)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        } else {
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 45.dp)
        }

        LaunchedEffect(isNetworkAvailable) {
            Log.d("isNetworkAvailable", "isNetworkAvailable: $isNetworkAvailable")
            if (!isNetworkAvailable) {
                Log.d("isNetworkAvailable", "Network not availble, should show snackbar")
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Ingen internetforbindelse",
                        actionLabel = "Lukk",
                        duration = SnackbarDuration.Indefinite
                    )
                }
            } else {
                snackbarHostState.currentSnackbarData?.dismiss()
            }
        }

        Scaffold(
            snackbarHost = {
                SnackbarHost(snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        contentColor = MaterialTheme.colorScheme.background,
                        actionColor = MaterialTheme.colorScheme.background,
                    )
                }
            },
        )
        { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                OsmMapView(
                    mapScreenViewModel = viewModel,
                    modifier = Modifier
                        .matchParentSize()
                        .padding(innerPadding),
                    onMarkerFirstPlace = { isWeatherExpanded = true },
                    onMarkerPress = { isWeatherExpanded = !isWeatherExpanded }
                )
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.background.copy(alpha = 0.6f),
                                        MaterialTheme.colorScheme.background.copy(alpha = 0.6f)
                                    ),
                                    startY = 0f,
                                    endY = Float.POSITIVE_INFINITY
                                )
                            )
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        MapSearchBar(
                            searchText = searchLocation,
                            onSearchTextChange = viewModel::onSearchTextChange,
                            onSearchCommit = {
                                viewModel.onSearchCommit(searchLocation)
                            },
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .fillMaxWidth()
                        )
                    }
                    if (isLandscape) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButtons(
                                onExpandChange = { isWeatherExpanded = !isWeatherExpanded },
                                onCenterMap = {
                                    Log.d("MapScreen", "centerMap called from WeatherButton.")
                                    viewModel.resetToInitialUserLocation()
                                }
                            )
                            AnimatedVisibility(
                                visible = isWeatherExpanded,
                                enter = slideInVertically(animationSpec = tween(durationMillis = 300)),
                                exit = slideOutVertically(animationSpec = tween(durationMillis = 300)),
                                modifier = weatherDisplayModifier
                            ) {
                                MapWeatherDataDisplay(
                                    mapinfoUiState = mapScreenUiState
                                )
                            }
                        }
                    } else {
                        Column (
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.End
                        ) {
                            IconButtons(
                                onExpandChange = { isWeatherExpanded = !isWeatherExpanded },
                                onCenterMap = {
                                    Log.d("MapScreen", "centerMap called from WeatherButton.")
                                    viewModel.resetToInitialUserLocation()
                                }
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            AnimatedVisibility(
                                visible = isWeatherExpanded,
                                enter = slideInHorizontally(
                                    initialOffsetX = { fullWidth -> fullWidth },
                                    animationSpec = tween(durationMillis = 300)
                                ),
                                exit = slideOutHorizontally(
                                    targetOffsetX = { fullWidth -> fullWidth },
                                    animationSpec = tween(durationMillis = 300)
                                ),
                                modifier = weatherDisplayModifier.padding(bottom = 60.dp)
                            ) {
                                MapWeatherDataDisplay(
                                    mapinfoUiState = mapScreenUiState
                                )
                            }
                        }
                    }
                }
                MapAttribution(modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(2.dp)
                )
            }
        }
    }
}

@Composable
fun MapAttribution(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(2.dp)
    ) {
        Text(
            text = "Map data © OpenSeaMap contributors",
            color = Color.DarkGray,
            fontSize = 7.sp
        )
        Text(
            text = "Map data © OpenStreetMap contributors",
            color = Color.DarkGray,
            fontSize = 7.sp
        )
    }
}