package no.uio.ifi.titanic.api.repo

import android.util.Log
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking
import no.uio.ifi.titanic.data.remote.repositories.WaterLevelRepo
import no.uio.ifi.titanic.data.remote.waterlevel.Data
import no.uio.ifi.titanic.data.remote.waterlevel.Location
import no.uio.ifi.titanic.data.remote.waterlevel.LocationData
import no.uio.ifi.titanic.data.remote.waterlevel.Tide
import no.uio.ifi.titanic.data.remote.waterlevel.WaterLevel
import no.uio.ifi.titanic.data.remote.waterlevel.WaterLevelDataSource
import org.junit.Before
import org.junit.Test
import java.time.OffsetDateTime

class WaterLevelRepoTest {
    private val dataSource = mockk<WaterLevelDataSource>()
    private  val repo = WaterLevelRepo(dataSource)
    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0

    }
    @Test
    fun `test correct data retrieval in getWaterLevel`() = runBlocking {
        val lat = 9.58
        val lon = 60.1
        val tide = Tide(
            locationdata= LocationData(
                location= Location(
                    name="Oslo",
                    code="OSL",
                    latitude=59.911491,
                    longitude=10.757933,
                    description="Tidevann fra Oslo",
                    observerName="Oslo", observerCode="OSL"
                ),
                reflevelcode="CD",
                data= Data(
                    type="prediction",
                    unit="cm",
                    qualityFlag=2,
                    waterlevel= listOf(
                        WaterLevel(value=75.2, time= OffsetDateTime.parse("2024-05-01T05:45+02:00"), flag="high"),
                    )
                )
            )
        )

        coEvery { dataSource.fetchWaterLevel(any(),any()) } returns tide

        val result = repo.getWaterLevel(lat,lon)

        coVerify { dataSource.fetchWaterLevel(lat, lon) }
        assertThat(result).isEqualTo(tide)
    }

}