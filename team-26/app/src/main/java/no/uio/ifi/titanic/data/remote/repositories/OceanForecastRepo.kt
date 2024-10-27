package no.uio.ifi.titanic.data.remote.repositories

import android.util.Log
import no.uio.ifi.titanic.data.remote.Client
import no.uio.ifi.titanic.data.remote.oceanForecast.OceanDetails
import no.uio.ifi.titanic.data.remote.oceanForecast.OceanForecastDataSource

class OceanForecastRepo (
    private val oceanForecastDataSource: OceanForecastDataSource = OceanForecastDataSource(Client())
) {
    //Communicates with the OceanForecast data source and returns the current ocean forecast.
    suspend fun getOceanForecast(lat: Double, lon: Double): OceanDetails {
        try {
            val forecast = oceanForecastDataSource.getOceanForecast(lat, lon).properties.timeseries
            if (forecast.isNotEmpty()) {
                return forecast[0].data.instant.details
            }
            return OceanDetails(
                0.0,
                0.0,
                0.0,
                0.0,
                0.0
            )
        } catch(e: Exception) {
            Log.d("OceanForecastDS", "Could not fetch the Ocean forecast data.")
            throw e
        }
    }
}