package no.uio.ifi.titanic.data.remote.waterlevel

import android.util.Log
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import no.uio.ifi.titanic.data.remote.Client
import java.time.Instant
import java.time.ZoneId

class WaterLevelDataSource(client: Client){
    //specifies the correct client to be used
    private val client = client.havnivaaClient

    //Communicates with the 'Se havniva' API and returns the data set
    suspend fun fetchWaterLevel(lat: Double, lon: Double): Tide? {
        try {
            Log.d("fetchWaterLevel", "fetching waterlevel from lat: $lat and lon:${lon}")
            val waterLevel = client.get(urlBuilder(lat, lon))

            val xmlDeserializer = XmlMapper(JacksonXmlModule().apply {
                setDefaultUseWrapper(false)
            })
                .registerKotlinModule()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

            if ("nodata" in waterLevel.bodyAsText()) {
                return null
            }
            if(!waterLevel.status.isSuccess()){
                throw Exception("failed to fetchwaterlevel server error ${waterLevel.status.value}")
            }
            return xmlDeserializer.readValue(waterLevel.bodyAsText(), Tide::class.java)
        } catch(e: Exception) {
            Log.d("WaterLevelDataSource", "Could not fetch the water level for given location.")
            throw e
        }
    }

    //Handles the URL building of parameters needed for relevant data the next 24 hours
    private fun urlBuilder(lat: Double, lon: Double): String {
        val tomorrow = Instant.now()
            .atZone(ZoneId.systemDefault())
            .plusDays(1)
            .toInstant()
        return "tideapi.php?lat=${lat}&lon=${lon}&fromtime=${Instant.now()}&totime=$tomorrow&datatype=tab&refcode=cd&place=&file=&lang=nb&interval=60&dst=1&tzone=1&tide_request=locationdata"
    }
}
