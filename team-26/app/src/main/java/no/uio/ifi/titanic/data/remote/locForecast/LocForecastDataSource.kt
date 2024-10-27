package no.uio.ifi.titanic.data.remote.locForecast
import android.util.Log
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import no.uio.ifi.titanic.data.remote.Client

/**
 * @param client klienten som inneholder base linken til api-en og api passord
 *
 */
class LocForecastDataSource(client: Client) {
    private val client = client.proxyClient
    suspend fun getLocForecastApi(lat: Double, lon: Double): SerializableLocForcast {
        try {
            Log.d("LocForecastDataSource", "Getting weather for: $lat, $lon")
            val vaer =  client.get("weatherapi/locationforecast/2.0/compact?lat=${lat}&lon=${lon}")
            //gjør at vi kan ignorere data vi ikke trenger f.eks geometry
            val json = Json {ignoreUnknownKeys = true}
            return json.decodeFromString<SerializableLocForcast>(vaer.bodyAsText())
        } catch (e: Exception) {
            Log.d("LocForecastDS", "Could not fetch the location forecast data.")
            throw e
        }
    }
}