package no.uio.ifi.titanic.model

data class TideInfoUiState(
    val nextTideType: String = "",
    val nextTideTime: String = "",
    val currentTideTime: String = "",
    val currentTideType:String = "",
    val nextHighTideTime: String = "",
    val nextLowTideTime: String = "",
    val waterLevel: Double = 0.0,
    val flag: String = "",
)