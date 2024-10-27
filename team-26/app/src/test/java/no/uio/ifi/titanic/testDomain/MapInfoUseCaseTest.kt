package no.uio.ifi.titanic.testDomain

import android.util.Log
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking
import no.uio.ifi.titanic.data.remote.oceanForecast.OceanDetails
import no.uio.ifi.titanic.data.remote.repositories.GeocodingRepository
import no.uio.ifi.titanic.data.remote.repositories.OceanForecastRepo
import no.uio.ifi.titanic.data.remote.repositories.WaterLevelRepo
import no.uio.ifi.titanic.data.remote.waterlevel.Data
import no.uio.ifi.titanic.data.remote.waterlevel.Location
import no.uio.ifi.titanic.data.remote.waterlevel.LocationData
import no.uio.ifi.titanic.data.remote.waterlevel.Tide
import no.uio.ifi.titanic.data.remote.waterlevel.WaterLevel
import no.uio.ifi.titanic.domain.FetchWeatherUseCase
import no.uio.ifi.titanic.domain.MapInfoUseCase
import no.uio.ifi.titanic.model.LocForecastUiState
import no.uio.ifi.titanic.model.UserLocationNameUiState
import org.junit.Before
import org.junit.Test
import java.time.OffsetDateTime

class MapInfoUseCaseTest{
    private val weatherFetcher = mockk<FetchWeatherUseCase>()
    private val oceanForecastRepo = mockk<OceanForecastRepo>()
    private val waterLevelRepo = mockk<WaterLevelRepo>()
    private val geoCodingRepo = mockk<GeocodingRepository>()
    val useCase = MapInfoUseCase(
        geocodingRepository = geoCodingRepo,
        weatherFetcher = weatherFetcher,
        oceanForecastRepo = oceanForecastRepo,
        waterLevelRepo =waterLevelRepo
    )
    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0

    }
    @Test
    fun `test MapInfoUseCaseTest data handling`() = runBlocking {
       val weatherResponse = listOf(
            LocForecastUiState(
                airTemperature = 11,
                windSpeed = 1.1,
            )
        )

        val oceanResponse = OceanDetails(
            seaWaterSpeed = 0.0,
            seaSurfaceWaveFromDirection = 0.0,
            seaSurfaceWaveHeight = 0.0,
            seaWaterTemperature = 0.0,
            seaWaterToDirection = 0.0
        )
        val waterResponse = Tide(
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
        val geoResponse = UserLocationNameUiState(
            locality = "",
            thoroughfare = "",
            featureName = "",
            areaDescription = ""
        )
        val lat = 1.1
        val lon = 2.2
        coEvery { weatherFetcher.invoke(lat,lon) } returns weatherResponse
        coEvery { oceanForecastRepo.getOceanForecast(lat,lon) } returns oceanResponse
        coEvery { waterLevelRepo.getWaterLevel(lat,lon) } returns waterResponse
        coEvery { geoCodingRepo.getLocationNameFromCoordinates(lat,lon) } returns geoResponse
        var result = useCase(lat,lon)

        assertThat(result.airTemperature).isEqualTo(11)
        assertThat(result.seaDepth).isEqualTo(75.2)
        assertThat(result.seaCurrentSpeed).isEqualTo(0.0)

        //check if MapInfoUseCase Works if Tide is NULL
        coEvery { waterLevelRepo.getWaterLevel(lat,lon) } returns null
        result = useCase(lat, lon)
        assertThat(result.seaDepth).isEqualTo(0.0)
        assertThat(result.airTemperature).isEqualTo(11)

    }
}