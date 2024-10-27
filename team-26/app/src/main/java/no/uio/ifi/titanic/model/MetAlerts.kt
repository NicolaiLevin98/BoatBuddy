package no.uio.ifi.titanic.model

data class MetAlertsUiState(
    val alertColor: String = "",
    val eventAwarenessName: String = "",
    val area: String = "",
    val awarenessLevel: String = "",
    val instructions: String = "",
    val consequences: String? = "",
    val description: String = "",
    val eventEndingTime: String? = "",
    val certainty: String? = "",
    val featureWhen: List<String> = emptyList(),
    val awarenessType: String = "",
    val geographicDomain: String? = ""
)

data class MarineMetAlertsState(
    val alertState: List<MetAlertsUiState> = emptyList()
)

data class MetAlertList(
    val alerts: List<MetAlertsUiState> = emptyList()
)