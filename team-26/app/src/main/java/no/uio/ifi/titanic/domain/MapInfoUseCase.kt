package no.uio.ifi.titanic.domain

import android.util.Log
import no.uio.ifi.titanic.data.remote.repositories.GeocodingRepository
import no.uio.ifi.titanic.data.remote.repositories.OceanForecastRepo
import no.uio.ifi.titanic.data.remote.repositories.WaterLevelRepo
import no.uio.ifi.titanic.model.MapInfoUiState

class MapInfoUseCase (
    private val geocodingRepository: GeocodingRepository,
    private val weatherFetcher: FetchWeatherUseCase = FetchWeatherUseCase(),
    private val oceanForecastRepo: OceanForecastRepo = OceanForecastRepo(),
    private val waterLevelRepo: WaterLevelRepo = WaterLevelRepo(),
) {
    //Formats data from the three APIs and Geocoder into a single UI-State for the MapScreen display
    suspend operator fun invoke(lat: Double, lon: Double): MapInfoUiState {
        try {
            val weatherData = weatherFetcher(lat, lon)
            val oceanData = oceanForecastRepo.getOceanForecast(lat, lon)
            val tideState = waterLevelRepo.getWaterLevel(lat, lon)
            val cityName = geocodingRepository.getLocationNameFromCoordinates(lat, lon)

            return MapInfoUiState(
                airTemperature = weatherData[0].airTemperature,
                seaDepth = tideState?.locationdata?.data?.waterlevel?.get(0)?.value ?: 0.0,
                seaCurrentSpeed = oceanData.seaWaterSpeed,
                waterTemperature = oceanData.seaWaterTemperature,
                waveDirection = oceanData.seaSurfaceWaveFromDirection,
                waveHeight = oceanData.seaSurfaceWaveHeight,
                windSpeed = weatherData[0].windSpeed,
                city = cityName.areaDescription ?: "Ukjent"
            )
        } catch(e: Exception) {
            Log.d("MapInfoUseCase", "Not able to fetch data MapInfoUseCase $e")
            throw e
        }
    }
}