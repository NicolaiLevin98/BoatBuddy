package no.uio.ifi.titanic.data.remote.repositories

import no.uio.ifi.titanic.data.remote.Client
import no.uio.ifi.titanic.data.remote.metAlerts.Feature
import no.uio.ifi.titanic.data.remote.metAlerts.MetAlertsDataSource

class MetAlertsRepository(
    private val metAlertsDataSource: MetAlertsDataSource = MetAlertsDataSource(Client())
) {
    //Communicates with the MetAlerts data source to get all active alerts at given coordinates
    suspend fun getAlert(lat: Double, lon: Double): List<Feature> {
        try {
            return metAlertsDataSource.getWeatherAlerts(lat, lon).features
        } catch (e: Exception) {
            throw e
        }
    }

    //Communicates with the MetAlerts data source to get all active marine alerts
    suspend fun getMarineAlerts(): List<Feature> {
        try {
            val allAlerts = metAlertsDataSource.getAllWeatherAlerts()
            return allAlerts.features.filter {
                it.properties.geographicDomain.equals("marine", ignoreCase = true)
            }
        } catch (e: Exception) {
            throw e
        }
    }
}
