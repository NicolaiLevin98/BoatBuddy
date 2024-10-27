package no.uio.ifi.titanic.data.remote.oceanForecast

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SerializableOceanForecast (
    val type: String,
    val properties: OceanProperties
)
@Serializable
@SerialName("Properties")
data class OceanProperties (
    val meta: OceanMeta,
    val timeseries: List<OceanTimesery>
)
@Serializable
@SerialName("Meta")
data class OceanMeta (
    @SerialName("updated_at")
    val updatedAt: String,
    val units: OceanUnits
)
@Serializable
@SerialName("Units")
data class OceanUnits (
    @SerialName("sea_surface_wave_from_direction")
    val seaSurfaceWaveFromDirection: String,
    @SerialName("sea_surface_wave_height")
    val seaSurfaceWaveHeight: String,
    @SerialName("sea_water_speed")
    val seaWaterSpeed: String,
    @SerialName("sea_water_temperature")
    val seaWaterTemperature: String,
    @SerialName("sea_water_to_direction")
    val seaWaterToDirection: String
)
@Serializable
@SerialName("Timesery")
data class OceanTimesery (
    val time: String,
    val data: OceanData
)
@Serializable
@SerialName("Data")
data class OceanData (
    val instant: OceanInstant
)
@Serializable
@SerialName("Instant")
data class OceanInstant (
    val details: OceanDetails
)
@Serializable
@SerialName("Details")
data class OceanDetails (
    @SerialName("sea_surface_wave_from_direction")
    val seaSurfaceWaveFromDirection: Double,
    @SerialName("sea_surface_wave_height")
    val seaSurfaceWaveHeight: Double,
    @SerialName("sea_water_speed")
    val seaWaterSpeed: Double,
    @SerialName("sea_water_temperature")
    val seaWaterTemperature: Double,
    @SerialName("sea_water_to_direction")
    val seaWaterToDirection: Double
)