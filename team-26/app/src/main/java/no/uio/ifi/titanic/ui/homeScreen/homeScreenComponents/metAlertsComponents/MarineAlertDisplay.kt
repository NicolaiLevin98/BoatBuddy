package no.uio.ifi.titanic.ui.homeScreen.homeScreenComponents.metAlertsComponents


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import no.uio.ifi.titanic.model.MarineMetAlertsState
import no.uio.ifi.titanic.model.MetAlertsUiState
import no.uio.ifi.titanic.ui.theme.OrangeAlertBanner
import no.uio.ifi.titanic.ui.theme.RedAlertBanner
import no.uio.ifi.titanic.ui.theme.YellowAlertBanner


@Composable
fun MarineAlertDisplay(
    marinealertState: MarineMetAlertsState,
    isExpanded: Boolean,
    onExpandChange: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primary)
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            MarineHeaderRow(marinealertState,isExpanded, onExpandChange)
            if (marinealertState.alertState.isEmpty()) {
                Text(
                    text = "Her vil du kunne se en oversikt over alle aktive farevarsler for hav og kyst i Norge",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                )
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = "Det er ingen varsler for øyeblikket",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                )
            } else {
                AnimatedVisibility(visible = isExpanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Her vil du kunne se en oversikt over alle aktive farevarsler for hav og kyst i Norge:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Light,
                            color = Color.Black,
                        )
                        Spacer(modifier = Modifier.padding(6.dp))
                        marinealertState.alertState.forEach { alert ->
                            val alertBackgroundColor = when (alert.alertColor) {
                                "Red" -> RedAlertBanner
                                "Orange" -> OrangeAlertBanner
                                "Yellow" -> YellowAlertBanner
                                else -> Color.LightGray
                            }
                            Box(
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        color = alertBackgroundColor.copy(alpha = 0.7f)
                                    )
                                    .border(
                                        width = 1.2.dp,
                                        color = alertBackgroundColor,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .fillMaxWidth(),
                            ) {
                                MarineAlertItem(marinealertState = alert)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MarineHeaderRow(marinealertState: MarineMetAlertsState,isExpanded: Boolean, onExpandChange: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Farevarsler i Norge (Hav og Kyst)",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = Color.Black
        )
        Box(
            modifier = Modifier
                .size(48.dp)
                .padding(4.dp)
                .clickable(onClick = onExpandChange),
            contentAlignment = Alignment.Center
        ) {
            if (marinealertState.alertState.isEmpty()){
                Text(text = "")
            } else{
                Icon(
                    painter = painterResource(id = if (isExpanded) R.drawable.arrow_triangle_up else R.drawable.arrow_triangle_down),
                    contentDescription = if (isExpanded) "Lukk" else "Åpne farevarselet for mer info",
                    modifier = Modifier.size(36.dp),
                    tint = Color.Black
                )
            }
        }
    }
}

@Composable
fun MarineAlertItem(marinealertState: MetAlertsUiState) {
    Column(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                painter = getMetAlertsIcon(
                    getAwarenessType(marinealertState.awarenessType),
                    marinealertState.alertColor
                ),
                contentDescription = "Alert Icon",
                modifier = Modifier
                    .size(42.dp)
                    .padding(end = 8.dp),
                tint = Color.Unspecified
            )
            Text(
                text = "${marinealertState.eventAwarenessName} (${marinealertState.area})",
                color = Color.Black,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

        }
        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = when (marinealertState.alertColor) {
                    "Yellow" -> "Gult nivå"
                    "Red" -> "Rødt Nivå"
                    "Orange" -> "Oransje nivå"
                    else -> ""
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 10.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Anbefalinger:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )
            Text(
                text = marinealertState.instructions,
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                color = Color.Black
            )
            Text(
                text = "Beskrivelse: ",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )
            Text(
                text = marinealertState.description,
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(2.dp))
            TimePeriodRow(marinealertState)
        }
    }
}