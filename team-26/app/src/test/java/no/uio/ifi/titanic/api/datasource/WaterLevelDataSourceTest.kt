package no.uio.ifi.titanic.api.datasource

import android.util.Log
import com.google.common.truth.Truth.assertThat
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking
import no.uio.ifi.titanic.data.remote.Client
import no.uio.ifi.titanic.data.remote.waterlevel.WaterLevelDataSource
import org.junit.Assert
import org.junit.Test

class WaterLevelDataSourceTest {

    @Test
    fun `test Waterlevel JSON serialization`() = runBlocking {
        val xmlResponse = """<tide>
        <locationdata>
            <location name="Oslo" code="OSL" latitude="59.912730" longitude="10.746090" delay="0" factor="1.00" obsname="Oslo" obscode="OSL" descr="Tidevann fra Oslo"/>
            <reflevelcode>CD</reflevelcode>
            <data type="prediction" unit="cm" qualityFlag="2" qualityClass="Quality OK" qualityDescription="blablablabla">
                <waterlevel value="75.4" time="2024-04-22T18:43:00+02:00" flag="high"/>
                <waterlevel value="51.2" time="2024-04-23T00:00:00+02:00" flag="low"/>
                <waterlevel value="74.4" time="2024-04-23T06:50:00+02:00" flag="high"/>
                <waterlevel value="50.6" time="2024-04-23T12:34:00+02:00" flag="low"/>
            </data>
        </locationdata>
    </tide>""".trimIndent()

        val mockEngine = MockEngine { _ ->
            respond(
                content = xmlResponse,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Xml.toString())
            )
        }
        val httpClient = HttpClient(mockEngine) {}
        val client = mockk<Client>()
        coEvery { client.havnivaaClient } returns  httpClient
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0

        val dataSource = WaterLevelDataSource(client)
        val result = dataSource.fetchWaterLevel(59.912730,10.746090)

        assertThat(result).isNotNull()
        assertThat(result!!.locationdata!!.data.waterlevel[0].value).isEqualTo(75.4)
        assertThat(result.locationdata!!.data.waterlevel[1].value).isEqualTo(51.2)

    }
    @Test
    fun `test Waterlevel network failure`() = runBlocking {
        val mockEngine = MockEngine { _ ->
            respondError(HttpStatusCode.InternalServerError)
        }
        val httpClient = HttpClient(mockEngine)
        val client = mockk<Client>()
        coEvery { client.havnivaaClient } returns httpClient
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        val dataSource = WaterLevelDataSource(client)

        try {
            dataSource.fetchWaterLevel(60.10, 9.58)
            Assert.fail("Expected")
        } catch (e: Exception) {
            assertThat(e).hasMessageThat().contains("server error")
        }
    }
}