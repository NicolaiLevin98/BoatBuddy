package no.uio.ifi.titanic.model

data class UserLocationNameUiState(
    val userArea: String = "",
    val locality: String? = null,
    val thoroughfare: String? = null,
    val featureName: String? = null,
    val areaDescription: String? = null
)