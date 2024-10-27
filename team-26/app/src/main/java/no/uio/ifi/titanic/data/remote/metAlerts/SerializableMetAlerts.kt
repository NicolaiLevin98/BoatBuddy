package no.uio.ifi.titanic.data.remote.metAlerts
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

    @Serializable
    data class SerializableMetAlerts (
        val features: List<Feature>,
        val lang: String,
        val lastChange: String,
        val type: String
    )

    @Serializable
    data class Feature (
        val properties: Properties2,
        val type: String,

        @SerialName("when")
        val featureWhen: When
    )

    @Serializable
    data class When (
        val interval: List<String>
    )

    @Serializable
    @SerialName("properties")
    data class Properties2 (
        val area: String,
        val awarenessResponse: String,
        val awarenessSeriousness: String? = null,

        @SerialName("awareness_level")
        val awarenessLevel: String,

        @SerialName("awareness_type")
        val awarenessType: String,

        val certainty: String?,
        val consequences: String? = null,
        val county: List<String>,
        val description: String,
        val event: String?,
        val eventAwarenessName: String,
        val eventEndingTime: String? = null,
        val geographicDomain: String? = null,
        val id: String,
        val instruction: String,
        val riskMatrixColor: RiskMatrixColor,
        val severity: String,
        val title: String,
        val triggerLevel: String? = null,
        val type: String,

        @SerialName("MunicipalityId")
        val municipalityID: String? = null,

        @SerialName("administrativeId")
        val administrativeID: String? = null,

        val incidentName: String? = null
    )



    @Suppress("unused")
    @Serializable
    enum class RiskMatrixColor(val value: String) {
        @SerialName("Green") Green("Green"),
        @SerialName("Orange") Orange("Orange"),
        @SerialName("Red") Red("Red"),
        @SerialName("Yellow") Yellow("Yellow");
    }



