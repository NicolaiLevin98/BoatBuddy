package no.uio.ifi.titanic.ui.openstreetmapScreen

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.titanic.LocationManager
import no.uio.ifi.titanic.data.remote.repositories.GeocodingRepository
import no.uio.ifi.titanic.domain.MapInfoUseCase
import no.uio.ifi.titanic.model.MapInfoUiState
import no.uio.ifi.titanic.networkconnectivity.NetworkUtility
import org.osmdroid.util.GeoPoint

// Needed to get the context for the GeocodingRepository without using it in the ViewModel
// The ViewModel should not have a reference to the context, but the ViewModel uses
// the GeocodingRepository which needs the context.
class MapScreenViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapScreenViewModel::class.java)) {
            val geocodingRepository = GeocodingRepository(context.applicationContext)
            val mapInfoUseCase = MapInfoUseCase(geocodingRepository)
            return MapScreenViewModel(
                mapInfoUseCase,
                geocodingRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class MapScreenViewModel (
    private val mapInfoUseCase: MapInfoUseCase,
    private val geocodingRepository: GeocodingRepository
) : ViewModel() {

    private val locationManager = LocationManager
    private val locationState: StateFlow<Location?> = locationManager.locationState

    private val networkUtility = NetworkUtility
    val networkAvailable: StateFlow<Boolean> = networkUtility.networkAvailable

    // Mutable state for search location
    private var _searchLocation = MutableStateFlow<GeoPoint?>(null)
    val searchLocation = _searchLocation.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _mapInfoUiState = MutableStateFlow(MapInfoUiState())
    val mapInfoUiState: StateFlow<MapInfoUiState> = _mapInfoUiState.asStateFlow()

    fun setInitialUserLocation(location: GeoPoint) {
        // Initialize the map view location to this point if not already set
        if (_searchLocation.value == null) {
            _searchLocation.value = location
        }
    }

    init {
        updateMapUiState(locationState.value?.latitude ?: 0.0, locationState.value?.longitude ?: 0.0)
    }

    fun resetToInitialUserLocation() {
        if (locationState.value != null) {
            _searchLocation.value = locationState.value?.let { GeoPoint(it.latitude, it.longitude) }
            updateMapUiState(locationState.value!!.latitude, locationState.value!!.longitude)
        }
    }

    fun updateLocation(geoPoint: GeoPoint) {
        _searchLocation.value = geoPoint
        updateMapUiState(geoPoint.latitude, geoPoint.longitude)
    }

    private fun updateMapUiState(lat: Double, lon: Double) {
        Log.d("UpdateOceanForecast", "Current network status: ${networkAvailable.value}")
        try {
            viewModelScope.launch(Dispatchers.IO) {
                if (networkUtility.isInternetAvailable()) {
                    val mapInfoData = mapInfoUseCase(lat, lon)
                    _mapInfoUiState.update {
                        it.copy(
                            airTemperature = mapInfoData.airTemperature,
                            seaDepth = mapInfoData.seaDepth,
                            seaCurrentSpeed = mapInfoData.seaCurrentSpeed,
                            waterTemperature = mapInfoData.waterTemperature,
                            waveDirection = mapInfoData.waveDirection,
                            waveHeight = mapInfoData.waveHeight,
                            windSpeed = mapInfoData.windSpeed,
                            city = mapInfoData.city
                        )
                    }
                    Log.d("UpdateOceanForecast", "${_mapInfoUiState.value}")
                }
            }
        } catch (e: Exception) {
            Log.d("UpdateOceanForecast", "Failed to update ocean forecast: ${e.localizedMessage}")
            _mapInfoUiState.update {
                it.copy(
                    airTemperature = 0,
                    seaDepth = 0.0,
                    seaCurrentSpeed = 0.0,
                    waterTemperature = 0.0,
                    waveDirection = 0.0,
                    waveHeight = 0.0,
                    windSpeed = 0.0,
                    city = "No internet.."
                )
            }
        }
    }

    fun onSearchTextChange(newText: String) {
        _searchText.value = newText
    }

    fun onSearchCommit(locatioName: String) {
        viewModelScope.launch {
            try {
                geocodingRepository.getCoordinatesFromLocationName(locatioName)?.let { coordinates ->
                    val geoPoint = GeoPoint(coordinates.first, coordinates.second)
                    _searchLocation.value = geoPoint
                    updateMapUiState(geoPoint.latitude, geoPoint.longitude)
                } ?: run {
                    Log.d("onSearchCommit", "No valid address found for the location.")
                }
            } catch (e: Exception) {
                Log.e("onSearchCommit", "Failed to geocode address: ${e.localizedMessage}")
            }
        }
    }
}