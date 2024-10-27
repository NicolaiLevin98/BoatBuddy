package no.uio.ifi.titanic.ui.openstreetmapScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.titanic.R
import no.uio.ifi.titanic.model.MapInfoUiState

@Composable
fun MapWeatherDataDisplay(mapinfoUiState: MapInfoUiState) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.95f))
            .padding(10.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                Row(
                    modifier = Modifier
                        .padding(vertical = 6.dp, horizontal = 4.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    HeaderText("Havvarsel:")
                    HeaderText(mapinfoUiState.city)
                }
            }

            // Weather data in a grid layout:
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        WeatherDataRow(
                            icon = R.drawable.thermostat,
                            label = "Lufttemperatur",
                            value = "${mapinfoUiState.airTemperature}°C",
                            contentDescription = "Lufttemperatur er ${mapinfoUiState.airTemperature} grader Celsius",
                            modifier = Modifier.weight(1f)
                        )
                        WeatherDataRow(
                            icon = R.drawable.thermostat,
                            label = "Vanntemperatur",
                            value = "${mapinfoUiState.waterTemperature}°C",
                            contentDescription = "Vanntemperatur er ${mapinfoUiState.waterTemperature} grader Celsius",
                            modifier = Modifier.weight(1f)
                        )
                        WeatherDataRow(
                            icon = R.drawable.wind_symbol,
                            label = "Vindstyrke",
                            value = "${mapinfoUiState.windSpeed}m/s",
                            contentDescription = "Vindstyrke er ${mapinfoUiState.windSpeed} meter per sekund",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        WeatherDataRow(
                            icon = R.drawable.water,
                            label = "Vanndybde",
                            value = "${mapinfoUiState.seaDepth}m",
                            contentDescription = "Vanndybde er ${mapinfoUiState.seaDepth} meter",
                            modifier = Modifier.weight(1f)
                        )
                        WeatherDataRow(
                            icon = R.drawable.currents,
                            label = "Strømninger",
                            value = "${mapinfoUiState.seaCurrentSpeed}m/s",
                            contentDescription = "Strømninger er ${mapinfoUiState.seaCurrentSpeed} meter per sekund",
                            modifier = Modifier.weight(1f)
                        )
                        WeatherDataRow(
                            icon = R.drawable.wave,
                            label = "Bølgehøyde",
                            value = "${mapinfoUiState.waveHeight}m",
                            contentDescription = "Bølgehøyde er ${mapinfoUiState.waveHeight} meter",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherDataRow(icon: Int, label: String, value: String, contentDescription: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = contentDescription,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 12.sp
        )
    }
}


@Composable
fun HeaderText(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 16.sp,
        modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewData() {
    val mockMapInfoUiState = MapInfoUiState(
        airTemperature = 20,
        waterTemperature = 12.9,
        seaDepth = 0.0,
        seaCurrentSpeed = 0.0,
        waveHeight = 0.0,
        windSpeed = 27.4,
        city = "Oslo, Kolonihagestien"
    )
    MapWeatherDataDisplay(mapinfoUiState = mockMapInfoUiState)
}