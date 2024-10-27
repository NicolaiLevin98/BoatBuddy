package no.uio.ifi.titanic.data.remote.metAlerts

import android.util.Log
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import no.uio.ifi.titanic.data.remote.Client

class MetAlertsDataSource(client: Client) {
    private val client = client.proxyClient

    //gets all alerts at the specified location
    suspend fun getWeatherAlerts(lat: Double, lon: Double): SerializableMetAlerts {
        try {
            Log.d("test-alert", "Getting alert for $lat, $lon")
            val alert = client.get("weatherapi/metalerts/2.0/current.json?lat=${lat}&lon=${lon}")
            val json = Json { ignoreUnknownKeys = true }
            return json.decodeFromString<SerializableMetAlerts>(alert.bodyAsText())
        } catch (e: Exception) {
            Log.d("metAlertsDataSource", "Could not fetch the weather alerts at given location.")
            throw e
        }
    }

    //gets all current alerts categorized as marine
    suspend fun getAllWeatherAlerts(): SerializableMetAlerts {
        try {
            Log.d("metAlertsDataSource", "Getting all weather alerts")
            val alertResponse = client.get("weatherapi/metalerts/2.0/current.json")
            val json = Json { ignoreUnknownKeys = true }
            return json.decodeFromString<SerializableMetAlerts>(alertResponse.bodyAsText())
        } catch (e: Exception) {
            Log.d("metAlertDataSource", "Could not fetch the Ocean forecast data.")
            throw e
        }
    }
}