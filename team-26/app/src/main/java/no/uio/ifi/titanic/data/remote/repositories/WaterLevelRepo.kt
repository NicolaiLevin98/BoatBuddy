package no.uio.ifi.titanic.data.remote.repositories

import android.util.Log
import no.uio.ifi.titanic.data.remote.Client
import no.uio.ifi.titanic.data.remote.waterlevel.Tide
import no.uio.ifi.titanic.data.remote.waterlevel.WaterLevelDataSource

class WaterLevelRepo(
    private val waterLevelDataSource: WaterLevelDataSource = WaterLevelDataSource(Client())
) {
    //Communicates with the 'Se havniva' data source and returns data set for the next 24 hours
    suspend fun getWaterLevel(lat: Double, lon: Double): Tide? {
        return try {
            waterLevelDataSource.fetchWaterLevel(lat, lon)
        } catch (e: Exception) {
            Log.e("WaterLevelRepo", "Failed to fetch water level: ${e.message}")
            throw e
        }
    }
}
