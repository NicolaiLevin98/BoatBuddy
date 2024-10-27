package no.uio.ifi.titanic.ui.homeScreen.homeScreenComponents.wind

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.titanic.model.LocForecastUiState
import no.uio.ifi.titanic.ui.homeScreen.homeScreenComponents.weather.getWeatherIcon


@Composable
fun BigSingleWeatherDisplay(
    userLocationForecastUiState: LocForecastUiState,
    showCurrentLabel: Boolean = false
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 6.dp, 10.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.tertiary,
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.primaryContainer,
                    ),
                )
            )
            .width(130.dp)
            .height(250.dp),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 2.dp)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 4.dp),
                text = if (showCurrentLabel) "Nå" else userLocationForecastUiState.time,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                painter = getWeatherIcon(userLocationForecastUiState.symbolCodeN1),
                contentDescription = "Weather Icon",
                tint = Color.Unspecified,
                modifier = Modifier.size(55.dp)
            )
            Text(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
                text = "${userLocationForecastUiState.airTemperature}°C",
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            WindDirectionIcon(rotationDegrees = userLocationForecastUiState.windDirection)
            Text(
                text = "${userLocationForecastUiState.windSpeed} m/s",
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}