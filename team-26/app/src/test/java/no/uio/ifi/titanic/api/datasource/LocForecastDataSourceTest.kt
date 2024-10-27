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
import no.uio.ifi.titanic.data.remote.locForecast.LocForecastDataSource
import org.junit.Assert.fail
import org.junit.Test

class LocForecastDataSourceTest {
    @Test
    fun `test locForecast JSON serialization`() = runBlocking {
        //result.properties.timeseries[0].data.instant.details.airTemperature should be 911
        val jsonResponse = """{"type": "Feature","geometry": {"type": "Point","coordinates": [9.58, 60.1, 496]},"properties": {"meta": {"updated_at": "2024-04-22T13:36:02Z","units": {"air_pressure_at_sea_level": "hPa","air_temperature": "celsius","air_temperature_max": "celsius","air_temperature_min": "celsius","air_temperature_percentile_10": "celsius","air_temperature_percentile_90": "celsius","cloud_area_fraction": "%","cloud_area_fraction_high": "%","cloud_area_fraction_low": "%","cloud_area_fraction_medium": "%","dew_point_temperature": "celsius","fog_area_fraction": "%","precipitation_amount": "mm","precipitation_amount_max": "mm","precipitation_amount_min": "mm","probability_of_precipitation": "%","probability_of_thunder": "%","relative_humidity": "%","ultraviolet_index_clear_sky": "1","wind_from_direction": "degrees","wind_speed": "m/s","wind_speed_of_gust": "m/s","wind_speed_percentile_10": "m/s","wind_speed_percentile_90": "m/s"}},"timeseries": [{"time": "2024-04-22T13:00:00Z","data": {"instant": {"details": {"air_pressure_at_sea_level": 1024.3,"air_temperature": 911,"air_temperature_percentile_10": 6.8,"air_temperature_percentile_90": 9.3,"cloud_area_fraction": 0.1,"cloud_area_fraction_high": 0.0,"cloud_area_fraction_low": 0.0,"cloud_area_fraction_medium": 0.1,"dew_point_temperature": -7.9,"fog_area_fraction": 0.0,"relative_humidity": 31.6,"ultraviolet_index_clear_sky": 2.5,"wind_from_direction": 181.9,"wind_speed": 1.4,"wind_speed_of_gust": 5.9,"wind_speed_percentile_10": 1.0,"wind_speed_percentile_90": 1.5}},"next_12_hours": {"summary": {"symbol_code": "fair_day","symbol_confidence": "certain"},"details": {"probability_of_precipitation": 0.0}},"next_1_hours": {"summary": {"symbol_code": "clearsky_day"},"details": {"precipitation_amount": 0.0,"precipitation_amount_max": 0.0,"precipitation_amount_min": 0.0,"probability_of_precipitation": 0.0,"probability_of_thunder": 0.0}},"next_6_hours": {"summary": {"symbol_code": "clearsky_day"},"details": {"air_temperature_max": 8.7,"air_temperature_min": 2.3,"precipitation_amount": 0.0,"precipitation_amount_max": 0.0,"precipitation_amount_min": 0.0,"probability_of_precipitation": 0.0}}}}]}}"""
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

        val dataSource = LocForecastDataSource(client)
        val result = dataSource.getLocForecastApi(60.10,9.58)
        assertThat(result).isNotNull()
        assertThat(result.properties.timeseries[0].data.instant.details.airTemperature).isEqualTo(911)
    }
    @Test
    fun `test locForecast network failure`() = runBlocking {
        val mockEngine = MockEngine { _ ->
            respondError(HttpStatusCode.InternalServerError)
        }
        val httpClient = HttpClient(mockEngine)
        val client = mockk<Client>()
        coEvery { client.proxyClient } returns httpClient
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        val dataSource = LocForecastDataSource(client)

        try {
            dataSource.getLocForecastApi(60.10, 9.58)
            fail("Expected")
        } catch (e: Exception) {
            assertThat(e).hasMessageThat().contains("Internal Server Error")
        }
    }

}
