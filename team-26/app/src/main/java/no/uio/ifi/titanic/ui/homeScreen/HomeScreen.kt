package no.uio.ifi.titanic.ui.homeScreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import no.uio.ifi.titanic.DataStoreViewModel
import no.uio.ifi.titanic.model.LocForecastUiStateList
import no.uio.ifi.titanic.model.MarineMetAlertsState
import no.uio.ifi.titanic.model.MetAlertList
import no.uio.ifi.titanic.model.TideInfoUiState
import no.uio.ifi.titanic.ui.homeScreen.homeScreenComponents.ErrorDisplay
import no.uio.ifi.titanic.ui.homeScreen.homeScreenComponents.HelloBox
import no.uio.ifi.titanic.ui.homeScreen.homeScreenComponents.metAlertsComponents.MarineAlertDisplay
import no.uio.ifi.titanic.ui.homeScreen.homeScreenComponents.metAlertsComponents.WeatherAlertBanner
import no.uio.ifi.titanic.ui.homeScreen.homeScreenComponents.tide.TideInfoDisplay
import no.uio.ifi.titanic.ui.homeScreen.homeScreenComponents.wind.BigSingleWeatherDisplay
import no.uio.ifi.titanic.ui.homeScreen.homeScreenComponents.wind.BigWindDisplay
import no.uio.ifi.titanic.ui.navigationComponents.BottombarDesign
import no.uio.ifi.titanic.ui.theme.TitanicTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

sealed class UiState<out T> {
    data class Loading<out T>(val previousData: T? = null) : UiState<T>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error<out T>(val message: String, val previousData: T? = null) : UiState<T>()
}

@Composable
fun HomeScreen(
    navController: NavController,
    themeViewModel: DataStoreViewModel,
    homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(LocalContext.current))
) {
    TitanicTheme(darkTheme = themeViewModel.darkModeFlow.collectAsState(initial = false).value) {
        val metAlertsState by homeViewModel.metAlertsUiState.collectAsState()
        val marineAlertsUiState by homeViewModel.marineAlertsUiState.collectAsState()
        val locForecastState by homeViewModel.locForecastUiState.collectAsState()
        val tideInfoState by homeViewModel.tideInfoUiState.collectAsState()

        val isNetworkAvailable by homeViewModel.networkAvailable.collectAsState()

        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        // Determine aggregated loading or error state
        val showLoading = listOf(metAlertsState, marineAlertsUiState, locForecastState, tideInfoState)
            .any { it is UiState.Loading && (it).previousData == null }

        val anyError = listOf(metAlertsState, marineAlertsUiState, locForecastState, tideInfoState)
            .any { it is UiState.Error }

        val notPrevData = listOf(metAlertsState, marineAlertsUiState, locForecastState, tideInfoState)
            .any {
                it is UiState.Error && (it).previousData == null ||
                        it is UiState.Loading && (it).previousData == null
            }

        // Trigger Snackbar when network is unavailable
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
        { contentPadding ->
            Column(
                modifier = Modifier
                    .padding(contentPadding)
            ) {
                when {
                    // If it is the first time and we are still loading any of the states
                    showLoading -> {
                        LoadingScreen()
                    }

                    anyError && notPrevData -> {
                        // Render a global error message
                        ErrorDisplay(
                            "Klarte ikke å laste data. Sjekk nettverket ditt eller prøv igjen."
                        ) {
                            homeViewModel.retryFetchingData("userRetry")
                        }
                    }
                    else -> {
                        // Use data from success state, or previous data from loading state or empty data
                        ContentScreen(
                            metAlertsList =
                                (metAlertsState as? UiState.Success)?.data ?:
                                (metAlertsState as? UiState.Loading)?.previousData ?:
                                (metAlertsState as? UiState.Error)?.previousData ?:
                                MetAlertList(),
                            marineMetAlertsState =
                                (marineAlertsUiState as? UiState.Success)?.data ?:
                                (marineAlertsUiState as? UiState.Loading)?.previousData ?:
                                (marineAlertsUiState as? UiState.Error)?.previousData ?:
                                MarineMetAlertsState(),
                            locForecastState =
                                (locForecastState as? UiState.Success)?.data ?:
                                (locForecastState as? UiState.Loading)?.previousData ?:
                                (locForecastState as? UiState.Error)?.previousData ?:
                                LocForecastUiStateList(emptyList()),
                            tideInfoState =
                                (tideInfoState as? UiState.Success)?.data ?:
                                (tideInfoState as? UiState.Loading)?.previousData ?:
                                (tideInfoState as? UiState.Error)?.previousData ?:
                                TideInfoUiState(),
                            homeViewModel = homeViewModel,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Log.d("HomeScreenUi", "Loading Screen is displayed")
        Column(horizontalAlignment = Alignment.CenterHorizontally){
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.padding(16.dp))
            Text(
                text = "Laster innhold...",
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentScreen(
    metAlertsList: MetAlertList,
    marineMetAlertsState: MarineMetAlertsState,
    locForecastState: LocForecastUiStateList,
    tideInfoState: TideInfoUiState,
    homeViewModel: HomeViewModel,
    navController: NavController
) {
    //Uistates:
    val userLocationNameUiState by homeViewModel.userLocationName.collectAsState()
    val searchBarUsed by homeViewModel.searchBarUsed.collectAsState()

    val verticalScrollState = rememberScrollState()
    val expandedStates = remember { mutableStateMapOf<Int, Boolean>() }
    var marineAlertisExpanded by remember { mutableStateOf(false) }

    val today = LocalDate.now()
    val norwegianLocale = Locale("nb", "NO")
    val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM", norwegianLocale)
    val formattedDate = today.format(formatter)

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    rememberCoroutineScope()

    val focusManager = LocalFocusManager.current
    val clearFocusModifier = Modifier.pointerInput(Unit) {
        detectTapGestures(onPress = {
            focusManager.clearFocus()
        })
    }
    Scaffold(
        modifier = clearFocusModifier,
        snackbarHost = {
            SnackbarHost(snackbarHostState)},
        topBar = {
            TopAppBar(
                colors = TopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = Color.Unspecified,
                    navigationIconContentColor = Color.Unspecified,
                    titleContentColor = Color.Unspecified,
                    actionIconContentColor = Color.Unspecified
                ),
                title = {
                    Text(
                        text = formattedDate,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp),
                        textAlign = TextAlign.End // Justerer teksten til høyre
                    )
                }
            )
        },
        bottomBar ={
            BottomAppBar {
                BottombarDesign(
                    navController,
                    newSelectedTab = "Home",
                    onHomeClick = {},
                    onInfoClick = {
                        navController.navigate("myInfoScreen") {
                            popUpTo("homeScreen") { inclusive = false }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .verticalScroll(verticalScrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                if (locForecastState.uiStates.isNotEmpty()) {
                    Log.d("userLocationNameUiState", "$userLocationNameUiState")
                    HelloBox(
                        searchBarUsed = searchBarUsed,
                        locForecastState.uiStates[0].symbolCodeN1,
                        userLocationNameUiState,
                        onSearchCommit = {searchText, _ ->
                            homeViewModel.searchBarClicked(searchText)
                        },
                        onLocationButtonClick = {_ ->
                            homeViewModel.onLocationIconClick()
                        }
                    )
                    Spacer(modifier = Modifier.padding(8.dp))
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .align(Alignment.Start),
                        text = "Værvarsel - de neste 3 timene: ",
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ){
                        BigSingleWeatherDisplay(
                            userLocationForecastUiState = locForecastState.uiStates[0],
                            showCurrentLabel = true
                        )
                        BigSingleWeatherDisplay(userLocationForecastUiState = locForecastState.uiStates[1])
                        BigSingleWeatherDisplay(userLocationForecastUiState = locForecastState.uiStates[2])
                        BigSingleWeatherDisplay(userLocationForecastUiState = locForecastState.uiStates[3])
                    }
                }

                Column(modifier = Modifier.padding(10.dp)){
                    Text(
                        text = "Generelle farevarsler: ",
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    metAlertsList.alerts.forEachIndexed{index, alert ->

                        // Initialize the expansion state for new alerts
                        if (expandedStates[index] == null) {
                            expandedStates[index] = false
                        }

                        WeatherAlertBanner(
                            alertState = alert,
                            isExpanded = expandedStates[index] ?: false,
                            onExpandChange = { expanded ->
                                expandedStates[index] = expanded
                            },
                        )
                    }
                }
                Box(modifier = Modifier.padding(10.dp)){
                    MarineAlertDisplay(
                        marinealertState = marineMetAlertsState,
                        isExpanded = marineAlertisExpanded,
                        onExpandChange = { marineAlertisExpanded = !marineAlertisExpanded }
                    )
                }
                Spacer(modifier = Modifier.padding(10.dp))
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .align(Alignment.Start),
                    text = "Havvarsel nå:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.padding(10.dp))
                if (locForecastState.uiStates.isNotEmpty()) {
                    Row (modifier = Modifier.fillMaxWidth()){
                        BigWindDisplay(locForecastState.uiStates[0])
                        TideInfoDisplay(tideInfoState)
                    }
                }
            }
        }
    }
}