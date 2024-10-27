package no.uio.ifi.titanic.model

data class LocForecastUiState(
    val airTemperature: Int = 0,
    val cloudPercentage: Double = 0.0,
    //windDirection is a degree-based measurement, north = 0, east = 90, south = 180, west = 270
    val windDirection: Double = 0.0,
    val windSpeed: Double = 0.0,
    val next1precipitation: Double = 0.0,
    val next6precipitation: Double = 0.0,
    val symbolCodeN1: String = "",
    val symbolCodeN6: String = "",
    val time: String = ""
)

data class LocForecastUiStateList(
    val uiStates: List<LocForecastUiState> = listOf()
)