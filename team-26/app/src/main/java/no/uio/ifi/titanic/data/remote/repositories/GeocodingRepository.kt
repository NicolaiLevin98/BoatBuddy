package no.uio.ifi.titanic.data.remote.repositories

import android.content.Context
import android.location.Geocoder
import android.util.Log
import no.uio.ifi.titanic.model.UserLocationNameUiState
import java.io.IOException
import java.util.Locale

//Handles all communication with the Geocoder.
class GeocodingRepository(private val context: Context) {
    fun getCoordinatesFromLocationName(locationName: String): Pair<Double, Double>? {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocationName(locationName, 1)
            if (addresses.isNullOrEmpty()) {
                Log.d("no.uio.ifi.titanic.data.remote.repositories.GeocodingRepository", "No address found for the location name.")
                return null
            }
            val address = addresses.first()
            Log.d("no.uio.ifi.titanic.data.remote.repositories.GeocodingRepository", "Coordinates: ${address.latitude}, ${address.longitude}")
            return Pair(address.latitude, address.longitude)
        } catch (e: IOException) {
            Log.e("no.uio.ifi.titanic.data.remote.repositories.GeocodingRepository", "Error getting coordinates: ${e.localizedMessage}")
            throw e
        }
    }

    fun getLocationNameFromCoordinates(latitude: Double, longitude: Double): UserLocationNameUiState {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            Log.d("addresses", "$addresses")
            if (!addresses.isNullOrEmpty()) {
                val address = addresses.first()
                val areaDescription = when {
                    address.subAdminArea != null && address.thoroughfare != null ->
                        "${address.subAdminArea}, ${address.thoroughfare}"
                    address.subAdminArea != null && address.featureName != null ->
                        "${address.subAdminArea}, ${address.featureName}"
                    address.subAdminArea != null -> address.subAdminArea
                    else -> address.locality ?: "Unknown"
                }
                return UserLocationNameUiState(
                    locality = address.locality,
                    thoroughfare = address.thoroughfare,
                    featureName = address.featureName,
                    areaDescription = formatAreaDescription(areaDescription)
                )
            } else {
                return UserLocationNameUiState(areaDescription = "Unknown")
            }
        } catch (e: IOException) {
            Log.e("no.uio.ifi.titanic.data.remote.repositories.GeocodingRepository", "Error fetching location name: ${e.localizedMessage}")
            throw e
        }
    }

    //Formats the given string from geocoder to a prettier format
    private fun formatAreaDescription(input: String): String {
        return input.replace(" kommune", "", ignoreCase = true)
            .replace(" Municipality", "", ignoreCase = true)
            .trim()
    }
}