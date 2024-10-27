package no.uio.ifi.titanic.ui.homeScreen.homeScreenComponents.wind

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.titanic.R
import no.uio.ifi.titanic.model.LocForecastUiState
import no.uio.ifi.titanic.ui.theme.Gray1

@Composable
fun BigWindDisplay(userLocationForecastUiState: LocForecastUiState) {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primary)
            .width(100.dp)
            .height(220.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(all = 8.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val icon = if(MaterialTheme.colorScheme.background == Gray1) R.drawable.vinddark
            else R.drawable.windiconwithcircle
            Icon(
                painter = painterResource(id = icon),
                contentDescription = "Wind",
                modifier = Modifier
                    .size(34.dp, 34.dp),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Vind",
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = userLocationForecastUiState.windSpeed.toString(),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Text(
                    modifier = Modifier.padding(vertical = 1.5.dp),
                    text = " m/s",
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    color = Color.Black
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Vindstyrke",
                fontWeight = FontWeight.Light,
                fontSize = 14.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.Bottom
            ) {
                WindDirectionIcon(userLocationForecastUiState.windDirection, Modifier, color = Color.Black)
                Text(
                    text = degreesToDirection(userLocationForecastUiState.windDirection),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
        }
    }
}


