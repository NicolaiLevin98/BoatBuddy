package no.uio.ifi.titanic.ui.homeScreen

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import no.uio.ifi.titanic.LocationManager
import no.uio.ifi.titanic.data.remote.repositories.GeocodingRepository
import no.uio.ifi.titanic.data.remote.repositories.MetAlertsRepository
import no.uio.ifi.titanic.data.remote.repositories.WaterLevelRepo
import no.uio.ifi.titanic.data.remote.waterlevel.WaterLevel
import no.uio.ifi.titanic.domain.FetchWeatherUseCase
import no.uio.ifi.titanic.model.LocForecastUiStateList
import no.uio.ifi.titanic.model.MarineMetAlertsState
import no.uio.ifi.titanic.model.MetAlertList
import no.uio.ifi.titanic.model.MetAlertsUiState
import no.uio.ifi.titanic.model.TideInfoUiState
import no.uio.ifi.titanic.model.UserLocationNameUiState
import no.uio.ifi.titanic.networkconnectivity.NetworkUtility
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class HomeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            val metAlertsRepository = MetAlertsRepository()
            val waterLevelRepo = WaterLevelRepo()
            val fetchWeatherUseCase = FetchWeatherUseCase()
            val geocodingRepository = GeocodingRepository(context.applicationContext)
            return HomeViewModel(
                metAlertsRepository,
                waterLevelRepo,
                fetchWeatherUseCase,
                geocodingRepository,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class HomeViewModel(
    private val metAlertsRepository: MetAlertsRepository = MetAlertsRepository(),
    private val waterLevelRepo: WaterLevelRepo = WaterLevelRepo(),
    private val fetchWeatherUseCase: FetchWeatherUseCase = FetchWeatherUseCase(),
    private val geocodingRepository: GeocodingRepository,
) : ViewModel() {

    private val locationManager = LocationManager
    private val networkUtility = NetworkUtility

    private val locationState: StateFlow<Location?> = locationManager.locationState
    val networkAvailable: StateFlow<Boolean> = networkUtility.networkAvailable

    //User location data - Latitude & Longitude
    private var userLatitude: Double = 59.9139
    private var userLongitude: Double = 10.7522

    private var initialDataLoaded = false

    //Location name from user location
    private val _userLocationName = MutableStateFlow(UserLocationNameUiState())
    val userLocationName: StateFlow<UserLocationNameUiState> = _userLocationName.asStateFlow()

    // States for individual features
    private val _metAlertsUiState = MutableStateFlow<UiState<MetAlertList>>(UiState.Loading())
    private val _marineAlertsUiState = MutableStateFlow<UiState<MarineMetAlertsState>>(UiState.Loading())
    private val _locForecastUiState = MutableStateFlow<UiState<LocForecastUiStateList>>(UiState.Loading())
    private val _tideInfoUiState = MutableStateFlow<UiState<TideInfoUiState>>(UiState.Loading())
    private val _searchBarUsed = MutableStateFlow(false)


    // Public state flows
    val metAlertsUiState: StateFlow<UiState<MetAlertList>> = _metAlertsUiState.asStateFlow()
    val marineAlertsUiState: StateFlow<UiState<MarineMetAlertsState>> = _marineAlertsUiState.asStateFlow()
    val locForecastUiState: StateFlow<UiState<LocForecastUiStateList>> = _locForecastUiState.asStateFlow()
    val tideInfoUiState: StateFlow<UiState<TideInfoUiState>> = _tideInfoUiState.asStateFlow()
    val searchBarUsed: StateFlow<Boolean> = _searchBarUsed.asStateFlow()

    init {
        handleLocationUpdates()
    }

    private fun handleLocationUpdates() {
        viewModelScope.launch {
            locationState.collect { location ->
                location?.let {
                    // check if searchBarUsed is false, if true, do not update location data
                    if (!searchBarUsed.value) {
                        updateAlertsUiState(it.latitude, it.longitude)
                        updateMarineAlerts()
                        fetchWeatherForLocation(it.latitude, it.longitude, "userRetry")
                        updateTideInformation(it.latitude, it.longitude)
                        updateLocationName(it.latitude, it.longitude)
                    }
                }
            }
        }
    }

    private fun updateLocationData(latitude: Double, longitude: Double, origin: String) {
        Log.d("updateLocationData", "I have been called by $origin!")
        viewModelScope.launch {
            try {
                // Update each individual state
                fetchWeatherForLocation(latitude, longitude, origin)
                updateAlertsUiState(latitude, longitude)
                updateTideInformation(latitude, longitude)
                updateLocationName(latitude, longitude)
                updateMarineAlerts()
            } catch (e: Exception) {
                Log.e("updateLocationData", "Failed to update data! Error: $e")
            }
        }
    }

    private fun updateAlertsUiState(lat: Double, lon: Double) {
        Log.d("updateAlertsUiState","Getting from ${lat}, $lon")
        viewModelScope.launch {
            val previousData = (_metAlertsUiState.value as? UiState.Success)?.data
            _metAlertsUiState.value = UiState.Loading(previousData)
            try {
                // Retrieve alerts
                val alerts = withContext(Dispatchers.IO) { metAlertsRepository.getAlert(lat, lon) }

                // Map alerts to UI states
                val alertsUiStates = alerts.map { feature ->
                    MetAlertsUiState(
                        alertColor = feature.properties.riskMatrixColor.value,
                        eventAwarenessName = feature.properties.eventAwarenessName,
                        area = feature.properties.area,
                        awarenessLevel = feature.properties.awarenessLevel,
                        instructions = feature.properties.instruction,
                        consequences = feature.properties.consequences,
                        description = feature.properties.description,
                        eventEndingTime = feature.properties.eventEndingTime,
                        certainty = feature.properties.certainty,
                        featureWhen = feature.featureWhen.interval,
                        awarenessType = feature.properties.awarenessType,
                        geographicDomain = feature.properties.geographicDomain
                    )
                }

                // Update with success state
                _metAlertsUiState.value = UiState.Success(MetAlertList(alertsUiStates))
            } catch (e: Exception) {
                // Update with error state
                _metAlertsUiState.value = UiState.Error("Failed to retrieve data: ${e.message}", previousData)
            }
        }
    }

    private fun updateMarineAlerts() {
        viewModelScope.launch {
            val previousData = (_marineAlertsUiState.value as? UiState.Success)?.data
            _marineAlertsUiState.value = UiState.Loading(previousData)
            try {
                // Fetch marine alerts on a background thread
                val marineAlerts = withContext(Dispatchers.IO) {
                    metAlertsRepository.getMarineAlerts()
                }

                Log.d("Getting marine alerts", "$marineAlerts")

                // Process alerts to filter and map to the desired UI state
                val alertsList = marineAlerts
                    .filter { alert -> alert.properties.riskMatrixColor.value != "Green" }
                    .map { alert ->
                        MetAlertsUiState(
                            alertColor = alert.properties.riskMatrixColor.value,
                            eventAwarenessName = alert.properties.eventAwarenessName,
                            area = alert.properties.area,
                            awarenessLevel = alert.properties.awarenessLevel,
                            instructions = alert.properties.instruction,
                            consequences = alert.properties.consequences,
                            description = alert.properties.description,
                            eventEndingTime = alert.properties.eventEndingTime,
                            certainty = alert.properties.certainty,
                            featureWhen = alert.featureWhen.interval,
                            awarenessType = alert.properties.awarenessType,
                            geographicDomain = alert.properties.geographicDomain
                        )
                    }
                // Update state with the processed data
                val marineMetAlertsState = MarineMetAlertsState(alertState = alertsList)
                _marineAlertsUiState.value = UiState.Success(marineMetAlertsState)
            } catch (e: Exception) {
                // Log and set error state
                Log.e("MarineAlertsError", "Error fetching marine alerts: ${e.message}")
                _marineAlertsUiState.value = UiState.Error("Error fetching marine alerts: ${e.message}", previousData)
            }
        }
    }

    private fun fetchWeatherForLocation(lat: Double, lon: Double, origin: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val previousData = (_locForecastUiState.value as? UiState.Success)?.data
            val shouldReload = !initialDataLoaded || origin == "userRetry"
            if (shouldReload) {
                _locForecastUiState.emit(UiState.Loading(previousData))
            }

            try {
                val weatherStates = fetchWeatherUseCase(lat, lon)
                val forecastUiStateList = LocForecastUiStateList(uiStates = weatherStates)
                _locForecastUiState.emit(UiState.Success(forecastUiStateList))
                initialDataLoaded = true
            } catch (e: Exception) {
                _locForecastUiState.emit(UiState.Error("Failed to retrieve weather: ${e.message}", previousData))
            }
        }
    }

    private fun updateLocationName(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val locationUiState = geocodingRepository.getLocationNameFromCoordinates(latitude, longitude)
                _userLocationName.update { locationUiState }
            } catch (e:Exception) {
                Log.e("updateLocationName", "Failed to update location name: ${e.message}")
                _userLocationName.update { UserLocationNameUiState(areaDescription = "Unknown") }
            }
        }
    }

    private fun updateTideInformation(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val previousData = (_tideInfoUiState.value as? UiState.Success)?.data
            _tideInfoUiState.emit(UiState.Loading(previousData))
            try {
                val tideData = waterLevelRepo.getWaterLevel(lat, lon)
                val updatedState =
                    if (tideData?.locationdata != null) {
                        val tideState = getNextTides(tideData.locationdata.data.waterlevel)

                        TideInfoUiState(
                            waterLevel = tideState.waterLevel,
                            nextHighTideTime = tideState.nextHighTideTime,
                            nextLowTideTime = tideState.nextLowTideTime,
                            currentTideTime = tideData.locationdata.data.waterlevel.maxByOrNull { it.time }?.time.toString(),
                            currentTideType = tideState.currentTideType
                        )
                    } else {
                        TideInfoUiState(
                            waterLevel = 9999999.9,
                            nextHighTideTime = "outofbounds",
                            nextLowTideTime = "outofbounds",
                            currentTideTime = "outofbounds",
                            currentTideType = "outofbounds"
                        )
                    }
                _tideInfoUiState.emit(UiState.Success(updatedState))
            } catch (e: Exception) {
                _tideInfoUiState.emit(UiState.Error("Failed to update tide information: ${e.message}", previousData))
                Log.d("updateTideInformation", "Error: ${e.message}")
            }
        }
    }

    private fun getNextTides(tides: List<WaterLevel>): TideInfoUiState {
        return try {
            val now = OffsetDateTime.now(ZoneId.systemDefault())
            val formatter = DateTimeFormatter.ofPattern("HH:mm")

            val futureTides = tides.filter { it.time.isAfter(now) }
            val nextHighTide = futureTides.filter { it.flag == "high" }.minByOrNull { it.time }
            val nextLowTide = futureTides.filter { it.flag == "low" }.minByOrNull { it.time }

            Log.d("TideUpdate", "Next high tide: $nextHighTide and low tide: $nextLowTide")

            TideInfoUiState(
                waterLevel = futureTides.firstOrNull()?.value ?: 0.0,
                nextHighTideTime = nextHighTide?.time?.format(formatter) ?: "Unavailable",
                nextLowTideTime = nextLowTide?.time?.format(formatter) ?: "Unavailable",
                currentTideTime = now.toString(),
                currentTideType = futureTides.firstOrNull()?.flag ?: "Unknown"
            )
        } catch (e: Exception) {
            Log.d("TideUpdate", "Exception during TideUpdate: $e")
            // Return a default value in case of an exception
            TideInfoUiState(
                waterLevel = 0.0,
                nextHighTideTime = "Unavailable",
                nextLowTideTime = "Unavailable",
                currentTideTime = "Unavailable",
                currentTideType = "Unknown"
            )
        }
    }

    fun retryFetchingData(origin: String) {
        val previousData = (_locForecastUiState.value as? UiState.Success)?.data
        _locForecastUiState.value = UiState.Loading(previousData)
        viewModelScope.launch {
            try {
                delay(1000) // Delay to simulate a retry delay
                updateLocationData(userLatitude, userLongitude, origin)
            } catch (e: Exception) {
                _locForecastUiState.value = UiState.Error("Retry failed: ${e.message}", previousData)
            }
        }
    }

    fun searchBarClicked(searchText: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                geocodingRepository.getCoordinatesFromLocationName(searchText)?.let { coordinates ->
                    updateLocationData(coordinates.first, coordinates.second, "searchBarClicked")
                    _searchBarUsed.value = true
                } ?: updateLocationData(userLatitude, userLongitude, "searchBarClicked")
            } catch(e:Exception) {
                Log.e("searchBarClicked", "Failed to get coordinates from search: ${e.message}")
                updateLocationData(userLatitude, userLongitude, "searchBarClicked")
            }
        }
    }

    fun onLocationIconClick() {
        _searchBarUsed.value = false
        if (locationState.value != null) {
            updateLocationData(locationState.value!!.latitude, locationState.value!!.longitude, "onLocationIconClick")
        } else {
            updateLocationData(userLatitude, userLongitude, "onLocationIconClick")
        }
    }
}