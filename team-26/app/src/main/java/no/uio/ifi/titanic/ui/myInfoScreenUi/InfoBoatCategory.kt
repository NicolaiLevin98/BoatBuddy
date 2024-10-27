package no.uio.ifi.titanic.ui.myInfoScreenUi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val categories = listOf(
    Pair(
        "Kategori A",
        "Gjelder havgående båter som er konstruert for lange reiser. Må tåle Beaufort vindstyrke over 8 " +
                "(mer enn 20,8m/sek) og signifikant bølgehøyde er på mer enn 4 meter. Signifikant bølgehøyde " +
                "er gjennomsnitthøyden (fra bølgebunn til bølgetopp) av den tredjedelen av alle bølgene i et " +
                "bølgefelt som er høyest i løpet av 20 minutter."
    ),
    Pair(
        "Kategori B",
        "Gjelder båter som er konstruert for bruk utenfor kysten. De skal tåle bølgehøyde " +
                " til og med 4 meter og vindstyrke til og med 8 (20,7 m/sek)."
    ),
    Pair(
        "Kategori C",
        "Gjelder båter som er konstruert for bruk nær kysten. De skal tåle signifikant bølgehøyde" +
                " til og med 2 meter og vindstyrke til og med 6 (13,8 m/sek)."
    ),
    Pair(
        "Kategori D",
        "Gjelder båter som er konstruert for bruk i beskyttet farevann. " +
                "De skal tåle signifikant bølgehøyde til og med 0,3 meter " +
                "og vindstyrke til og med 4 (mindre enn 7,9 m/sek)."
    )
)


@Composable
fun InfoBoatCategory(categoryTitle: String, categoryDescription: String) {
    Box(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 16.dp)
            .width(300.dp)  // Set a fixed width for each category box
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))
            .background(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = categoryTitle,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.Black,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = categoryDescription,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = Color.Black,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
    }
}