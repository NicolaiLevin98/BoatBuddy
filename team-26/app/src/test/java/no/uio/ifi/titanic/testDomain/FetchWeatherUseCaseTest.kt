package no.uio.ifi.titanic.testDomain

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.uio.ifi.titanic.data.remote.locForecast.Data
import no.uio.ifi.titanic.data.remote.locForecast.Instant
import no.uio.ifi.titanic.data.remote.locForecast.InstantDetails
import no.uio.ifi.titanic.data.remote.locForecast.Meta
import no.uio.ifi.titanic.data.remote.locForecast.NextHours
import no.uio.ifi.titanic.data.remote.locForecast.NextHoursDetails
import no.uio.ifi.titanic.data.remote.locForecast.Properties
import no.uio.ifi.titanic.data.remote.locForecast.Summary
import no.uio.ifi.titanic.data.remote.locForecast.Timesery
import no.uio.ifi.titanic.data.remote.locForecast.Units
import no.uio.ifi.titanic.data.remote.repositories.LocForecastRepo
import no.uio.ifi.titanic.domain.FetchWeatherUseCase
import org.junit.Test


class FetchWeatherUseCaseTest{

    private val repo = mockk<LocForecastRepo>()
    val useCase = FetchWeatherUseCase(repo)

    @Test
    fun `test fetchweatherusecase good return data`() = runBlocking {
        val data = Timesery(
            time="2024-04-29T15:00:00Z",
            data= Data(
                instant= Instant(
                    details= InstantDetails(airPressureAtSeaLevel=1019.4, airTemperature=11.9, cloudAreaFraction=0.0, relativeHumidity=72.0, windFromDirection=281.3, windSpeed=3.7)
                ),
                next1Hours= NextHours(summary= Summary(symbolCode="clearsky_day"), details= NextHoursDetails(precipitationAmount=0.0)),
                next6Hours=NextHours(summary=Summary(symbolCode="clearsky_day"), details=NextHoursDetails(precipitationAmount=0.0))
            )
        )

        val response = Properties(
            meta= Meta(
                updatedAt="2024-04-29T13:25:17Z",
                units= Units(airPressureAtSeaLevel="hPa", airTemperature="celsius", cloudAreaFraction="%", precipitationAmount="mm", relativeHumidity="%", windFromDirection="degrees", windSpeed="m/s")
            ),
            //Fetchweatherusecase må ha flere enn 4 TimeseryObjekter
            timeseries= listOf(data,data,data,data)
        )

        val lat = 1.1
        val lon = 2.2
        coEvery { repo.getWeatherNow(lat,lon) } returns response

        val result =  useCase(lat,lon)

        assertThat(result.size).isEqualTo(4)
        result.forEach{
            assertThat(it.airTemperature).isEqualTo(11)
            assertThat(it.cloudPercentage).isEqualTo(0.0)
            assertThat(it.windDirection).isEqualTo(281.3)
        }

    }

}