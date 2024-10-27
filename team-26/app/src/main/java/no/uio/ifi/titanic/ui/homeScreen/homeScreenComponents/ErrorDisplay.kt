package no.uio.ifi.titanic.ui.homeScreen.homeScreenComponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.titanic.R

@Composable
fun ErrorDisplay(
    errorMessage: String,
    onRetry: () -> Unit
) {
    //2 columns so the entire screen is the correct color
    Column(Modifier.background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(120.dp))

            Text(
                text = "Ups! her skjedde det en feil",
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage,
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.seagull),
                contentDescription = "Lighthouse image",
                modifier = Modifier.size(280.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                colors = ButtonDefaults.buttonColors(Color(0xFF84B1BA)),
                onClick = onRetry,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Trykk her for å prøve igjen ",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.background
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Trykk på knappen for å laste innholdet på nytt",
                textAlign = TextAlign.Left,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

        }
    }
}

