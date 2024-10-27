package no.uio.ifi.titanic.api.repo

import android.util.Log
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking
import no.uio.ifi.titanic.data.remote.metAlerts.Feature
import no.uio.ifi.titanic.data.remote.metAlerts.MetAlertsDataSource
import no.uio.ifi.titanic.data.remote.metAlerts.Properties2
import no.uio.ifi.titanic.data.remote.metAlerts.RiskMatrixColor
import no.uio.ifi.titanic.data.remote.metAlerts.SerializableMetAlerts
import no.uio.ifi.titanic.data.remote.metAlerts.When
import no.uio.ifi.titanic.data.remote.repositories.MetAlertsRepository
import org.junit.Test

class MetAlertsRepositoryTest {
    private val dataSource = mockk<MetAlertsDataSource>()
    private  val repo = MetAlertsRepository(dataSource)
    @Test
    fun `test correct data retrieval in getWeatherAlerts`() = runBlocking {
        val lat = 9.58
        val lon = 60.1
        val listFeatures = listOf( Feature(
            properties= Properties2(area="Deler av Østlandet",
                awarenessResponse="Følg med",
                awarenessSeriousness="Utfordrende situasjon",
                awarenessLevel="2; yellow; Moderate",
                awarenessType="8; forest-fire",
                certainty="Observed",
                consequences="Vegetasjon kan lett antennes og store områder kan bli berørt." ,
                county= listOf("40", "34", "33", "39", "32", "03", "31"),
                description="Det ventes lokal gress- og lyngbrannfare i snøfrie områder  inntil det kommer regn av betydning eller at ny vegetasjon vokser opp. ",
                event="forestFire",
                eventAwarenessName="Skogbrannfare",
                eventEndingTime=null,
                geographicDomain="land",
                id="2.49.0.1.578.0.20240422084424.091",
                instruction="Følg lokale myndigheters instruksjoner. ",
                riskMatrixColor= RiskMatrixColor.Yellow,
                severity="Moderate",
                title="Skogbrannfare",
                triggerLevel=null,
                type="Update",
                municipalityID=null,
                administrativeID=null,
                incidentName=null),
            type="Feature",
            featureWhen= When(interval=listOf("2024-04-22T08:00:00+00:00", "2024-04-25T22:00:00+00:00")
            )
        ))
        println(listFeatures)
        coEvery { dataSource.getWeatherAlerts(any(),any()) } returns SerializableMetAlerts(features = listFeatures , lang = "no", lastChange = "2024-04-22T14:40:36+00:00", type = "FeatureCollection")
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0

        val result = repo.getAlert(lat,lon)

        coVerify { dataSource.getWeatherAlerts(lat,lon) }
        assertThat(result).isEqualTo(listFeatures)

    }

}