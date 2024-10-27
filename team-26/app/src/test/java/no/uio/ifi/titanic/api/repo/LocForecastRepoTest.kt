package no.uio.ifi.titanic.api.repo

import android.util.Log
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking
import no.uio.ifi.titanic.data.remote.locForecast.Data
import no.uio.ifi.titanic.data.remote.locForecast.Instant
import no.uio.ifi.titanic.data.remote.locForecast.InstantDetails
import no.uio.ifi.titanic.data.remote.locForecast.LocForecastDataSource
import no.uio.ifi.titanic.data.remote.locForecast.Meta
import no.uio.ifi.titanic.data.remote.locForecast.NextHours
import no.uio.ifi.titanic.data.remote.locForecast.NextHoursDetails
import no.uio.ifi.titanic.data.remote.locForecast.Properties
import no.uio.ifi.titanic.data.remote.locForecast.SerializableLocForcast
import no.uio.ifi.titanic.data.remote.locForecast.Summary
import no.uio.ifi.titanic.data.remote.locForecast.Timesery
import no.uio.ifi.titanic.data.remote.locForecast.Units
import no.uio.ifi.titanic.data.remote.repositories.LocForecastRepo
import org.junit.Before
import org.junit.Test

class LocForecastRepoTest {

    private val dataSource = mockk<LocForecastDataSource>()
    private  val repo = LocForecastRepo(dataSource)
    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
    }
    @Test
    fun `test correct data retrieval in getWeatherNow`() = runBlocking {
        val properties = Properties(
            meta= Meta(
                updatedAt="2024-04-22T13:36:02Z",
                units= Units(
                    airPressureAtSeaLevel="hPa",
                    airTemperature="celsius",
                    cloudAreaFraction="%",
                    precipitationAmount="mm",
                    relativeHumidity="%",
                    windFromDirection="degrees",
                    windSpeed="m/s"
                )
            ),
            timeseries= listOf(
                Timesery(
                    time="2024-04-22T13:00:00Z",
                    data= Data(
                        instant=Instant(details= InstantDetails(airPressureAtSeaLevel=1024.3, airTemperature=911.0, cloudAreaFraction=0.1, relativeHumidity=31.6, windFromDirection=181.9, windSpeed=1.4)),
                        next1Hours=NextHours(summary= Summary(symbolCode="clearsky_day"), details= NextHoursDetails(precipitationAmount=0.0)),
                        next6Hours=NextHours(summary=Summary(symbolCode="clearsky_day"), details=NextHoursDetails(precipitationAmount=0.0))
                    )
                )
            )
        )
        coEvery { dataSource.getLocForecastApi(any(),any()) } returns SerializableLocForcast(type = "Feature", properties = properties)

        val result = repo.getWeatherNow(9.58,60.1)

        coVerify { dataSource.getLocForecastApi(9.58,60.1) }
        assertThat(result).isEqualTo(properties)

    }
}
