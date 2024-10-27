package no.uio.ifi.titanic.ui.homeScreen.homeScreenComponents.metAlertsComponents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import no.uio.ifi.titanic.model.MetAlertsUiState
import no.uio.ifi.titanic.ui.theme.OrangeAlertBanner
import no.uio.ifi.titanic.ui.theme.RedAlertBanner
import no.uio.ifi.titanic.ui.theme.TitanicTheme
import no.uio.ifi.titanic.ui.theme.YellowAlertBanner
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun WeatherAlertBanner(
    alertState: MetAlertsUiState,
    isExpanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
)  {
    val backgroundColor = when (alertState.alertColor) {
        "Red" -> RedAlertBanner
        "Orange" -> OrangeAlertBanner
        "Yellow" -> YellowAlertBanner
        else -> return
    }

    Box(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                color = backgroundColor
            )
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        )  {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ){
                AlertHeaderRow(alertState, isExpanded, onExpandChange)
                AnimatedVisibility(visible = isExpanded) {
                    AlertCard(alertState)
                }
            }
        }
    }
}

@Composable
fun AlertHeaderRow(
    alertState: MetAlertsUiState,
    isExpanded: Boolean,
    onExpandChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = getMetAlertsIcon(
                    getAwarenessType(alertState.awarenessType),
                    alertState.alertColor
                ),
                contentDescription = "Alert Icon",
                modifier = Modifier.size(38.dp),
                tint = Color.Unspecified
            )
            Column(
                modifier = Modifier.padding(start = 8.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = alertState.eventAwarenessName,
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "(${alertState.area})",
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
        IconButton(
            onClick = { onExpandChange(!isExpanded) },
            modifier = Modifier
                .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                .padding(start = 8.dp)
        ) {
            Icon(
                painter = painterResource(
                    id = if (isExpanded) R.drawable.arrow_triangle_up
                    else R.drawable.arrow_triangle_down
                ),
                contentDescription = if (isExpanded) "Lukk" else "Åpne farevarselet for mer info",
                modifier = Modifier.size(36.dp),
                tint = Color.Black
            )
        }
    }
}

@Composable
fun AlertCard(alertState: MetAlertsUiState){
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ){
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(
                text = when (alertState.alertColor) {
                    "Yellow" -> "Gult nivå"
                    "Red" -> "Rødt Nivå"
                    "Orange" -> "Oransje nivå"
                    else -> ""
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                color = Color.Black
            )
            Spacer(modifier = Modifier.padding(18.dp))
            Text(
                text = "Faregrad: ${alertState.awarenessType.split(";").first().trim()}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )
        }
        Text(
            text = "Anbefalinger: ",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.padding(vertical = 2.dp))
        Text(
            text = alertState.instructions,
            fontSize = 14.sp,
            fontWeight = FontWeight.Light,
            color = Color.Black
        )
        Spacer(modifier = Modifier.padding(vertical = 4.dp))
        Text(
            text = "Konsekvenser: ",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.padding(vertical = 2.dp))
        Text(
            text = "${alertState.consequences}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Light,
            color = Color.Black
        )
        Spacer(modifier = Modifier.padding(vertical = 2.dp))
        Text(
            text = "Beskrivelse: ",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
        Text(
            text = alertState.description,
            fontSize = 14.sp,
            fontWeight = FontWeight.Light,
            color = Color.Black
        )
        Spacer(modifier = Modifier.padding(vertical = 4.dp))
        Column {
            TimePeriodRow(alertState)
        }
    }

}

@Composable
fun TimePeriodRow(alertState: MetAlertsUiState) {
    Column {
        if (alertState.featureWhen.isNotEmpty()) {
            val startDateText = formatDate(alertState.featureWhen[0]) + " - faren starter"
            Text(
                text = "Tidsperiode:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.symbol_point_1),
                    contentDescription = "Symbol for start period",
                    modifier = Modifier.size(11.dp),
                    tint = Color.Unspecified
                )
                Text(
                    modifier = Modifier.padding(bottom = 3.dp),
                    text = startDateText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.Black
                )
            }
            if (alertState.eventEndingTime != null) {
                val endDateText = formatDate(alertState.eventEndingTime) + " - faren over"
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.symbol_point_2),
                        contentDescription = "Symbol for end period",
                        modifier = Modifier.size(12.dp),
                        tint = Color.Unspecified
                    )
                    Text(
                        modifier = Modifier.padding(top = 3.dp),
                        text = endDateText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Light,
                        color = Color.Black
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Arrow down",
                    modifier = Modifier.size(12.dp),
                    tint = Color.DarkGray
                )
            }
        }
    }
}

fun getAwarenessType(awarenessType: String): String {
    return awarenessType.split(";")
        .last()
        .replace("-", "")
        .trim()
}

fun formatDate(dateTimeString: String): String {
    val zonedDateTime = ZonedDateTime.parse(dateTimeString)
        .withZoneSameInstant(ZoneId.of("Europe/Oslo")) // Convert to Norwegian time zone
    val norwegianLocale = java.util.Locale("no", "NO")
    val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM 'kl.' HH.mm", norwegianLocale)
    return zonedDateTime.format(formatter)
}

@Preview(showBackground = true)
@Composable
fun PreviewWeatherAlertBanner() {
    TitanicTheme {
        WeatherAlertBanner(
            alertState = MetAlertsUiState(
                alertColor = "Yellow",
                eventAwarenessName = "Skogbrannfare",
                awarenessType = "Gult",
                area = "Deler av Vestland og Rogaland",
                instructions = "Ikke bruk ild, følg lokale myndigheters instruksjoenr",
                consequences = "Vegetasjon kan lett antennes og store områder kan bli berørt",
                description = "Lokal gress- og lyngbrannfare inntil det kommer tilstrekkelig nedbør",
                featureWhen = listOf("2024-05-12T14:00:00Z"),
                eventEndingTime = "2024-05-13T18:00:00Z"
            ),
            isExpanded = true,
            onExpandChange = {},
        )
    }
}