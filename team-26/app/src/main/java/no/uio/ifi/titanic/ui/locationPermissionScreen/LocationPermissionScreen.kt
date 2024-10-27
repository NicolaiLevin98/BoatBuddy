package no.uio.ifi.titanic.ui.locationPermissionScreen

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.titanic.DataStoreViewModel
import no.uio.ifi.titanic.LocationManager
import no.uio.ifi.titanic.R
import no.uio.ifi.titanic.ui.theme.TitanicTheme

@Composable
fun LocationPermissionScreen(
    requestLocationPermissionLauncher: ActivityResultLauncher<Array<String>>,
    dataStoreViewModel: DataStoreViewModel
) {
    val context = LocalContext.current
    val verticalScrollState = rememberScrollState()
    val locationManager = LocationManager
    val darktheme = dataStoreViewModel.darkModeFlow.collectAsState(initial = false).value

    TitanicTheme(
        darkTheme = darktheme
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(verticalScrollState)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 32.dp)
            ) {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .fillMaxWidth()
                        .border(0.8.dp, Color.LightGray, shape = RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    // Image
                    Image(
                        painter = painterResource(id = R.drawable.lighthouse),
                        contentDescription = "Lighthouse image",
                        modifier = Modifier.size(200.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Main text
                    Text(
                        text = "Aktiver lokasjon for bedre app-opplevelser!",
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Å ha på lokasjonstjenester kan forbedre appens funksjonalitet ved å tilby mer relevante innhold og tjenester basert på din geografiske posisjon, noe som gjør opplevelsen mer personlig og effektiv.",
                        textAlign = TextAlign.Left,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.surfaceDim,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Button
                    Button(
                        colors = ButtonDefaults.buttonColors(Color(0xFF84B1BA)),
                        onClick = {
                            locationManager.checkAndRequestPermissions(
                                context,
                                requestLocationPermissionLauncher
                            )
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "Aktiver her",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = "Husk å start appen på nytt etter at du har endret på instillingene for lokasjon",
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}