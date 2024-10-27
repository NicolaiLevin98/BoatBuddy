package no.uio.ifi.titanic.api.datasource

import android.util.Log
import com.google.common.truth.Truth.assertThat
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import no.uio.ifi.titanic.data.remote.Client
import no.uio.ifi.titanic.data.remote.oceanForecast.OceanForecastDataSource
import org.junit.Test

class OceanForecastDataSourceTest {
    @Test
    fun `test OceanForecast JSON serialization`() = runBlocking {
        //result.properties.timeseries[0].data.instant.details.seaWaterTemperature should be 911
            val jsonResponse = """{"type": "Feature","geometry": {"type": "Point","coordinates": [10.7285, 59.9021]},"properties": {"meta": {"updated_at": "2024-04-24T06:13:21Z","units": {"sea_surface_wave_from_direction": "degrees","sea_surface_wave_height": "m","sea_water_speed": "m/s","sea_water_temperature": "celsius","sea_water_to_direction": "degrees"}},"timeseries": [{"time": "","data": {"instant": {"details": {"sea_surface_wave_from_direction": 353.3,"sea_surface_wave_height": 0.0,"sea_water_speed": 0.0,"sea_water_temperature": 911,"sea_water_to_direction": 132.9}}}}]}}"""
            val mockEngine = MockEngine { _ ->
            respond(
                content = jsonResponse,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
        val client = mockk<Client>()
        coEvery { client.proxyClient } returns  httpClient

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0

        val dataSource = OceanForecastDataSource(client)
        val result = dataSource.getOceanForecast(60.10,9.58)
        assertThat(result).isNotNull()
        assertThat(result.properties.timeseries[0].data.instant.details.seaWaterTemperature).isEqualTo(911)
    }
    @Test
    fun `test OceanForecast network failure`() = runBlocking {
        val mockEngine = MockEngine { _ ->
            respondError(HttpStatusCode.InternalServerError)
        }
        val httpClient = HttpClient(mockEngine)
        val client = mockk<Client>()
        coEvery { client.proxyClient } returns httpClient
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        val dataSource = OceanForecastDataSource(client)
        val oceanForecast = dataSource.getOceanForecast(60.10, 9.58)
        println(oceanForecast)
        assertThat(oceanForecast.type).isEqualTo("")
        assertThat(oceanForecast.properties.meta.updatedAt).isEmpty()
    }

}
