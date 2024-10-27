package no.uio.ifi.titanic.data.remote.waterlevel


import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

data class Tide(
    val locationdata: LocationData?
)

data class LocationData(
    val location: Location,
    val reflevelcode: String,
    @JacksonXmlElementWrapper(useWrapping = false)
    val data: Data
)

data class Location(
    @JacksonXmlProperty(isAttribute = true) val name: String,
    @JacksonXmlProperty(isAttribute = true) val code: String,
    @JacksonXmlProperty(isAttribute = true) val latitude: Double,
    @JacksonXmlProperty(isAttribute = true) val longitude: Double,
    @JsonProperty("descr") val description: String,
    @JsonProperty("obsname") val observerName: String?,
    @JsonProperty("obscode") val observerCode: String?
)

data class Data(
    @JacksonXmlProperty(isAttribute = true) val type: String,
    @JacksonXmlProperty(isAttribute = true) val unit: String,
    @JacksonXmlProperty(isAttribute = true) val qualityFlag: Int,
    @JsonDeserialize(contentAs = WaterLevel::class)
    val waterlevel: List<WaterLevel>
)

data class WaterLevel(
    @JacksonXmlProperty(isAttribute = true) val value: Double,
    @JacksonXmlProperty(isAttribute = true) @JsonDeserialize(using = CustomDateTimeDeserializer::class)
    val time: OffsetDateTime,
    @JacksonXmlProperty(isAttribute = true) val flag: String
)


class CustomDateTimeDeserializer : StdDeserializer<OffsetDateTime>(OffsetDateTime::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): OffsetDateTime {
        return OffsetDateTime.parse(p.text, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }
}