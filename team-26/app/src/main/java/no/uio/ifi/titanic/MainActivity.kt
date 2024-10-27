package no.uio.ifi.titanic

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import no.uio.ifi.titanic.networkconnectivity.NetworkUtility
import no.uio.ifi.titanic.ui.homeScreen.HomeScreen
import no.uio.ifi.titanic.ui.locationPermissionScreen.LocationPermissionScreen
import no.uio.ifi.titanic.ui.myInfoScreenUi.MyInfoScreen
import no.uio.ifi.titanic.ui.openstreetmapScreen.MapScreen
import no.uio.ifi.titanic.ui.theme.TitanicTheme


class MainActivity : ComponentActivity() {
    private val locationPermissionGranted = mutableStateOf(false)
    private lateinit var requestLocationPermissionsLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register network callback
        NetworkUtility.registerNetworkCallback(this)

        val dataStoreViewModel: DataStoreViewModel by viewModels()

        // Check if location permission is already granted
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        locationPermissionGranted.value = checkSelfPermission(permission) == android.content.pm.PackageManager.PERMISSION_GRANTED

        // Initialize the launcher for multiple permissions
        requestLocationPermissionsLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            // Process the map of permissions and their grant results
            locationPermissionGranted.value = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (locationPermissionGranted.value) {
                LocationManager.startLocationUpdates(this, requestLocationPermissionsLauncher)
            } else {
                // Here we can handle the case where permission is not granted
            }
        }

        setContent {
            TitanicTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    locationPermissionGranted.value = LocationManager.checkAndRequestPermissions(
                        this, requestLocationPermissionsLauncher
                    )

                    if (locationPermissionGranted.value) {
                        LocationManager.startLocationUpdates(this, requestLocationPermissionsLauncher)
                        val navController = rememberNavController()
                        NavHost(
                            navController = navController,
                            startDestination = "homeScreen",
                        ) {
                            composable("homeScreen") {
                                HomeScreen(navController = navController, themeViewModel = dataStoreViewModel)
                            }
                            composable("Map") {
                                MapScreen(navController = navController,themeViewModel = dataStoreViewModel)
                            }
                            composable("myInfoScreen") {
                                MyInfoScreen(navController = navController,dataStoreViewModel = dataStoreViewModel)
                            }
                        }
                    }
                    else {
                        LocationPermissionScreen(requestLocationPermissionsLauncher, dataStoreViewModel)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop location updates to prevent memory leaks
        LocationManager.stopLocationUpdates()
    }
}