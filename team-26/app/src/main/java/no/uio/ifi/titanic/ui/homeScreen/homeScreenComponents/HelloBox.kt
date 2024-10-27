package no.uio.ifi.titanic.ui.homeScreen.homeScreenComponents

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.titanic.model.UserLocationNameUiState
import no.uio.ifi.titanic.ui.homeScreen.homeScreenComponents.weather.getNorwegianWeatherDescription

@Composable
fun HelloBox(
    searchBarUsed: Boolean,
    symbolCode: String,
    areaUiState: UserLocationNameUiState,
    onSearchCommit: (String, Context) -> Unit,
    onLocationButtonClick: (Context) -> Unit
) {
    val norwegianDescription = getNorwegianWeatherDescription(symbolCode)
    Column (
        modifier = Modifier
            .padding(horizontal = 10.dp)
    ){
        Text(
            text = "Hei Buddy,",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 34.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        )

        Spacer(modifier = Modifier.padding(10.dp))

        Text(
            text = buildAnnotatedString {
                append("det er ")
                withStyle(style = SpanStyle(color =MaterialTheme.colorScheme.onPrimary)) {
                    append(norwegianDescription.replace("_", " "))
                    append(" i ${areaUiState.areaDescription}")
                    Log.d("Userlocation", "UserlocationSearch: ${areaUiState.areaDescription}")
                }
            },
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.padding(10.dp))
        SearchBar(
            searchBarUsed = searchBarUsed,
            onSearchCommit = onSearchCommit,
            onLocationButtonClick = onLocationButtonClick
        )
    }
}
