package no.uio.ifi.titanic.data.remote.locForecast


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SerializableLocForcast (
    val type: String,
    val properties: Properties
)

@Serializable
data class Properties (
    val meta: Meta,
    val timeseries: List<Timesery>
)
@Serializable
data class Meta (
    @SerialName("updated_at")
    val updatedAt: String,
    val units: Units
)
@Serializable
data class Units (
    @SerialName("air_pressure_at_sea_level")
    val airPressureAtSeaLevel: String,
    @SerialName("air_temperature")
    val airTemperature: String,
    @SerialName("cloud_area_fraction")
    val cloudAreaFraction: String,
    @SerialName("precipitation_amount")
    val precipitationAmount: String,
    @SerialName("relative_humidity")
    val relativeHumidity: String,
    @SerialName("wind_from_direction")
    val windFromDirection: String,
    @SerialName("wind_speed")
    val windSpeed: String
)
@Serializable
data class Timesery (
    val time: String,
    val data: Data
)
@Serializable
data class Data (
    val instant: Instant,
    @SerialName("next_1_hours")
    val next1Hours: NextHours? = null,
    @SerialName("next_6_hours")
    val next6Hours: NextHours? = null
)
@Serializable
data class Instant (
    val details: InstantDetails
)
@Serializable
data class InstantDetails (
    @SerialName("air_pressure_at_sea_level")
    val airPressureAtSeaLevel: Double,
    @SerialName("air_temperature")
    val airTemperature: Double,
    @SerialName("cloud_area_fraction")
    val cloudAreaFraction: Double,
    @SerialName("relative_humidity")
    val relativeHumidity: Double,
    @SerialName("wind_from_direction")
    val windFromDirection: Double,
    @SerialName("wind_speed")
    val windSpeed: Double
)

@Serializable
data class Summary (
    @SerialName("symbol_code")
    val symbolCode: String
)

@Serializable
data class NextHours (
    val summary: Summary,
    val details: NextHoursDetails
)
@Serializable
data class NextHoursDetails (
    @SerialName("precipitation_amount")
    val precipitationAmount: Double
)