package no.uio.ifi.titanic.data.remote.oceanForecast

import android.util.Log
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import no.uio.ifi.titanic.data.remote.Client

class OceanForecastDataSource(client: Client) {
    private val _client = client.proxyClient

    //retrieves data from the OceanForecast API
    suspend fun getOceanForecast(lat: Double, lon: Double): SerializableOceanForecast {
        //try_catch to handle calls outside data model
        return try {
            Log.d("test-alert", "Getting ocean forecast for $lat, $lon")
            val oceanForecast = _client.get("weatherapi/oceanforecast/2.0/complete?lat=$lat&lon=$lon")
            val json = Json {ignoreUnknownKeys = true}
            json.decodeFromString<SerializableOceanForecast>(oceanForecast.bodyAsText())
        } catch (e: Exception) {
            Log.d("OceanForecastDataSource", "$e")
            return SerializableOceanForecast(
                properties = OceanProperties(
                    meta = OceanMeta(
                        units = OceanUnits(
                            "",
                            "",
                            "",
                            "",
                            ""
                        ),
                        updatedAt = ""
                    ),
                    timeseries = listOf()
                ),
                type = ""
            )
        }
    }
}