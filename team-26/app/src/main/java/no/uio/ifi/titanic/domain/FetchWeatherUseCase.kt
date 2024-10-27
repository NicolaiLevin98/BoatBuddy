package no.uio.ifi.titanic.domain

import no.uio.ifi.titanic.data.remote.repositories.LocForecastRepo
import no.uio.ifi.titanic.model.LocForecastUiState
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class FetchWeatherUseCase(private val locForecastRepository: LocForecastRepo = LocForecastRepo()) {
    //communicates with the LocForecastRepo and returns a data set with LocForecasts the next 3 hours
    suspend operator fun invoke(lat: Double, lon: Double): List<LocForecastUiState> {
        try {
            return locForecastRepository
                .getWeatherNow(lat, lon)
                .timeseries
                .subList(0,4)
                .map {
                    LocForecastUiState(
                        airTemperature = it.data.instant.details.airTemperature.toInt(),
                        cloudPercentage = it.data.instant.details.cloudAreaFraction,
                        windDirection = it.data.instant.details.windFromDirection,
                        windSpeed = it.data.instant.details.windSpeed,
                        symbolCodeN1 = it.data.next1Hours?.summary?.symbolCode ?: "",
                        time = formatHour(it.time)
                    )
                }
        } catch(e: Exception) {
            throw e
        }
    }
}

//formats the time provided from the API response
fun formatHour(dateTime: String): String {
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    val defaultZoneId = ZoneId.systemDefault() // Storing system default ZoneId

    return try {
        val zonedDateTime = ZonedDateTime.parse(dateTime, formatter)
        zonedDateTime.withZoneSameInstant(defaultZoneId)
            .format(DateTimeFormatter.ofPattern("'Kl. 'HH")).lowercase()
    } catch (e: DateTimeParseException) {
        e.printStackTrace()
        "Invalid date format"
    }
}


