package no.uio.ifi.titanic.model

data class MapInfoUiState(
    val airTemperature: Int = 0,
    val seaDepth: Double = 0.0,
    val seaCurrentSpeed: Double = 0.0,
    val waterTemperature: Double = 0.0,
    val waveDirection: Double = 0.0,
    val waveHeight: Double = 0.0,
    val windSpeed: Double = 0.0,
    val city: String = "Ukjent",
)