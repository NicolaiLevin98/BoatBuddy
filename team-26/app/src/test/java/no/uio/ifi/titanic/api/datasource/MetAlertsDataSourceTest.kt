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
import no.uio.ifi.titanic.data.remote.metAlerts.MetAlertsDataSource
import no.uio.ifi.titanic.data.remote.metAlerts.RiskMatrixColor
import org.junit.Assert
import org.junit.Test

class MetAlertsDataSourceTest {
    @Test
    fun `test metalerts JSON serialization`() = runBlocking {
        val jsonResponse = """{"features":[{"geometry":{"coordinates":[],"type":"Polygon"},"properties":{"altitude_above_sea_level":0,"area":"Deler av Østlandet","awarenessResponse":"Følg med","awarenessSeriousness":"Utfordrende situasjon","awareness_level":"2; yellow; Moderate","awareness_type":"8; forest-fire","ceiling_above_sea_level":305,"certainty":"Observed","consequences":"Vegetasjon kan lett antennes og store områder kan bli berørt. ","contact":"","county":[],"description":"Det ventes lokal gress- og lyngbrannfare i snøfrie områder  inntil det kommer regn av betydning eller at ny vegetasjon vokser opp. ","event":"forestFire","eventAwarenessName":"Skogbrannfare","geographicDomain":"land","id":"2.49.0.1.578.0.20240422084424.091","instruction":"Følg lokale myndigheters instruksjoner. ","municipality":[],"resources":[{"description":"CAP file","mimeType":"application\/xml","uri":""},{"description":"","mimeType":"image\/png","uri":""}],"riskMatrixColor":"Yellow","severity":"Moderate","status":"Actual","title":"","type":"Update","web":""},"type":"Feature","when":{"interval":["2024-04-22T08:00:00+00:00","2024-04-25T22:00:00+00:00"]}}],"lang":"no","lastChange":"2024-04-22T14:40:36+00:00","type":"FeatureCollection"}"""
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

        val dataSource = MetAlertsDataSource(client)
        val result = dataSource.getWeatherAlerts(78.2,15.63)
        assertThat(result).isNotNull()
        assertThat(result.features[0].properties.event).isEqualTo("forestFire")
        assertThat(result.features[0].properties.riskMatrixColor).isEquivalentAccordingToCompareTo(RiskMatrixColor.Yellow)
        assertThat(result.lastChange).isEqualTo( "2024-04-22T14:40:36+00:00")

    }
    @Test
    fun `test metalerts network failure`() = runBlocking {
        val mockEngine = MockEngine { _ ->
            respondError(HttpStatusCode.InternalServerError)
        }
        val httpClient = HttpClient(mockEngine)
        val client = mockk<Client>()
        coEvery { client.proxyClient } returns httpClient
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        val dataSource = MetAlertsDataSource(client)

        try {
            dataSource.getWeatherAlerts(60.10, 9.58)
            Assert.fail("Expected")
        } catch (e: Exception) {
            assertThat(e).hasMessageThat().contains("Internal Server Error")
        }
    }
}
