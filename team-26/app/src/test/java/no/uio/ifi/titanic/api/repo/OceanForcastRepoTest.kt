package no.uio.ifi.titanic.api.repo

import android.util.Log
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking
import no.uio.ifi.titanic.data.remote.oceanForecast.OceanData
import no.uio.ifi.titanic.data.remote.oceanForecast.OceanDetails
import no.uio.ifi.titanic.data.remote.oceanForecast.OceanForecastDataSource
import no.uio.ifi.titanic.data.remote.oceanForecast.OceanInstant
import no.uio.ifi.titanic.data.remote.oceanForecast.OceanMeta
import no.uio.ifi.titanic.data.remote.oceanForecast.OceanProperties
import no.uio.ifi.titanic.data.remote.oceanForecast.OceanTimesery
import no.uio.ifi.titanic.data.remote.oceanForecast.OceanUnits
import no.uio.ifi.titanic.data.remote.oceanForecast.SerializableOceanForecast
import no.uio.ifi.titanic.data.remote.repositories.OceanForecastRepo
import org.junit.Before
import org.junit.Test

class OceanForcastRepoTest {

    private val dataSource = mockk<OceanForecastDataSource>()
    private  val repo = OceanForecastRepo(dataSource)
    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0

    }
    @Test
    fun `test correct data retrieval in fetchOceanForecast`() = runBlocking {
        val properties = OceanProperties(
            meta= OceanMeta(
                updatedAt="2024-04-24T06:13:21Z",
                units= OceanUnits(seaSurfaceWaveFromDirection="degrees", seaSurfaceWaveHeight="m", seaWaterSpeed="m/s", seaWaterTemperature="celsius", seaWaterToDirection="degrees")
            ), timeseries= listOf( OceanTimesery(time="2024-04-24T07:00:00Z", data= OceanData(instant= OceanInstant(details= OceanDetails(seaSurfaceWaveFromDirection=353.3, seaSurfaceWaveHeight=0.0, seaWaterSpeed=0.0, seaWaterTemperature=5.7, seaWaterToDirection=132.9))))))
        coEvery { dataSource.getOceanForecast(any(),any()) } returns SerializableOceanForecast(type = "Feature", properties = properties)

        val result = repo.getOceanForecast("9.58".toDouble(),"60.1".toDouble())

        coVerify { dataSource.getOceanForecast("9.58".toDouble(),"60.1".toDouble()) }
        assertThat(result).isEqualTo(properties.timeseries[0].data.instant.details)

    }
}
