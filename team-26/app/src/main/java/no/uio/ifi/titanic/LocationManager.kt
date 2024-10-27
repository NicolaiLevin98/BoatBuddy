package no.uio.ifi.titanic

import android.location.Location
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object LocationManager {
    private const val TAG = "LocationManager"
    private const val LOCATION_UPDATE_INTERVAL = 60_000L // 1 minute
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private val _locationState = MutableStateFlow<Location?>(null)
    val locationState: StateFlow<Location?> = _locationState.asStateFlow()

    // Request permissions and check if the app has them
    fun checkAndRequestPermissions(
        context: Context,
        requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    ): Boolean {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val permissionsNeeded = mutableListOf<String>()

        if (!hasFineLocationPermission) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (!hasCoarseLocationPermission) {
            permissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (permissionsNeeded.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsNeeded.toTypedArray())
            return false
        }
        return true
    }

    // Start receiving location updates if permissions are granted
    fun startLocationUpdates(
        context: Context,
        requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    ) {
        if (!checkAndRequestPermissions(context, requestPermissionLauncher)) {
            Log.e(TAG, "Permissions not granted")
            return
        }

        // Determine highest available location permission
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val priority = if (hasFineLocationPermission) {
            Priority.PRIORITY_HIGH_ACCURACY
        } else {
            Priority.PRIORITY_BALANCED_POWER_ACCURACY
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val locationRequest = LocationRequest.Builder(LOCATION_UPDATE_INTERVAL).apply {
            setPriority(priority)
        }.build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let {
                    Log.d(TAG, "New location: ${it.latitude}, ${it.longitude}")
                    _locationState.value = it
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            ).addOnSuccessListener {
                Log.d(TAG, "Location updates started with priority $priority")
            }.addOnFailureListener {
                Log.e(TAG, "Failed to start location updates", it)
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "Missing location permission: ${e.message}")
        }
    }

    // Stop receiving location updates
    fun stopLocationUpdates() {
        if (this::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            Log.d(TAG, "Location updates stopped")
        }
    }
}