package no.uio.ifi.titanic.ui.myInfoScreenUi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun RSinfo(locationViewModel: LocationViewModel) {
    val userLocation by locationViewModel.userGeoPoint.collectAsState()
    Row (modifier =  Modifier.padding(horizontal = 16.dp)){
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(10.dp)
        ) {
            Column {
                Text(
                    text = "Kontaktinfo\nRedningsskøyten: ",
                    modifier = Modifier
                        .padding(vertical = 10.dp),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "RS Kundeservice",
                    color = Color.Black
                )
                Text(
                    text = "Tlf: 98706757",
                    color = Color.Black
                )

                Spacer(modifier = Modifier.padding(10.dp))

                Text(
                    text = "RS Servicetelefon",
                    color = Color.Black
                )
                Text(
                    text = "Tlf: 91502016",
                    color = Color.Black
                )

                Spacer(modifier = Modifier.padding(10.dp))

                Text(
                    text = "Kystradiostasjonen",
                    color = Color.Black
                )
                Text(
                    text = "Tlf: 120",
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.padding(10.dp))

        Box(modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(10.dp)) {
            Column {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Dine koordinater: ${userLocation?.latitude}, ${userLocation?.longitude}",
                        modifier = Modifier
                            .padding(vertical = 10.dp),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}