package no.uio.ifi.titanic.data.remote.repositories

import android.util.Log
import no.uio.ifi.titanic.data.remote.Client
import no.uio.ifi.titanic.data.remote.locForecast.LocForecastDataSource
import no.uio.ifi.titanic.data.remote.locForecast.Properties


class LocForecastRepo(
    private val locForecastDataSource: LocForecastDataSource = LocForecastDataSource(Client())
){
    //Communicates with the LocationForecast data source and returns only the dataset
    suspend fun getWeatherNow(lat: Double, lon: Double): Properties {
        try {
            Log.d("getWeatherData", "Getting weather")
            return locForecastDataSource.getLocForecastApi(lat, lon).properties
        } catch (e: Exception) {
            throw e
        }
    }
}
