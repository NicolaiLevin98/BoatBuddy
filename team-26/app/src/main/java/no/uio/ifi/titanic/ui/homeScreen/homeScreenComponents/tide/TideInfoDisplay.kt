package no.uio.ifi.titanic.ui.homeScreen.homeScreenComponents.tide

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.titanic.R
import no.uio.ifi.titanic.model.TideInfoUiState
import no.uio.ifi.titanic.ui.theme.Gray1

@Composable
fun TideInfoDisplay(tideInfoState: TideInfoUiState) {
    val icon = if (MaterialTheme.colorScheme.background == Gray1) R.drawable.vanndark
    else R.drawable.tideiconwithcircle
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primary)
            .width(240.dp)
            .fillMaxHeight(0.9f)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    text = "Tidevann",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = Color.Black
                )
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = "TideIcon",
                    modifier = Modifier
                        .size(34.dp, 34.dp),
                    tint = Color.Unspecified,
                )
            }
            if (tideInfoState.currentTideType == "outofbounds") {
                Spacer(modifier = Modifier.padding(6.dp))
                Text(
                    text = "Din posisjon er utenfor vårt dekningsområde for tidevannsdata",
                    color = Color.Black,
                    fontWeight = FontWeight.Light,
                    fontSize = 16.sp,
                )
                Spacer(modifier = Modifier.padding(vertical = 33.dp))
            } else {
                // Water level and current tide type
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start) {

                    // Determine the appropriate icon for the tide type:
                    val tideIcon = when (tideInfoState.currentTideType.lowercase()) {
                        "high" -> R.drawable.hightidewatericon
                        "low" -> R.drawable.lowtidewatericon
                        else -> R.drawable.water // Default icon
                    }
                    Icon(
                        painter = painterResource(id = tideIcon),
                        contentDescription = "${tideInfoState.currentTideType} Tide",
                        modifier = Modifier.size(32.dp, 32.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = tideInfoState.waterLevel.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color.Black
                        )
                        Text(
                            text = "cm",
                            modifier = Modifier.padding(top = 5.dp),
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = "Neste forventede høyvann (flo):",
                    fontWeight = FontWeight.Light,
                    fontSize = 12.sp,
                    color = Color.Black
                )
                Text(
                    text = "Kl. ${tideInfoState.nextHighTideTime}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = "Neste forventede lavvann (fjære):",
                    fontWeight = FontWeight.Light,
                    fontSize = 12.sp,
                    color = Color.Black
                )
                Text(
                    text = "Kl. ${tideInfoState.nextLowTideTime}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.padding(vertical = 6.dp))
            }
        }
    }
}

@Composable
@Preview
fun ShowTideInfo() {
    val mockTideInfo = TideInfoUiState(
        currentTideType = "Low",
        waterLevel = 120.0,
        nextHighTideTime = "12:45",
        nextLowTideTime = "18:30"
    )
    TideInfoDisplay(mockTideInfo)
}