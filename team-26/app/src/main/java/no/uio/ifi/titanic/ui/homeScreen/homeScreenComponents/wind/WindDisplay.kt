package no.uio.ifi.titanic.ui.homeScreen.homeScreenComponents.wind

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import no.uio.ifi.titanic.R

@Composable
fun WindDirectionIcon(
    rotationDegrees: Double,
    modifier: Modifier = Modifier,
    color:  Color = MaterialTheme.colorScheme.onSurface
) {
    Box(modifier = Modifier.then(modifier)) {
        Image(
            colorFilter = ColorFilter.tint(color),
            painter = painterResource(id = R.drawable.windarrow),
            contentDescription = "Wind Direction",
            modifier = Modifier
                .size(25.dp)
                .align(Alignment.Center)
                .graphicsLayer {
                    rotationZ = rotationDegrees.toFloat()
                    transformOrigin = TransformOrigin.Center
                }
        )
    }
}

fun degreesToDirection(degrees: Double): String {
    val directions = arrayOf(
        "N", "N-NØ", "NØ", "Ø-NØ", "Ø", "Ø-SØ", "SØ", "S-SØ",
        "S", "S-SV", "SV", "V-SV", "V", "V-NV", "NV", "N-NV"
    )
    val adjustedDegrees = if (degrees < 0) 360 + degrees % 360 else degrees % 360
    val index = ((adjustedDegrees + 11.25) % 360 / 22.5).toInt()
    return directions[index]
}
