package no.uio.ifi.titanic.ui.myInfoScreenUi

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.osmdroid.util.GeoPoint

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    private val _userGeopoint = MutableStateFlow<GeoPoint?>(null)
    val userGeoPoint = _userGeopoint.asStateFlow()
    private val locationclient = LocationServices.getFusedLocationProviderClient(getApplication<Application>().applicationContext)

    init {
        updateLocation()
    }
    private fun updateLocation() {
        try {
            locationclient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    _userGeopoint.value = GeoPoint(it.latitude , it.longitude)
                }
            }
        } catch (e: SecurityException) {
            Log.e("locationviewmodel", "Exception-> ${e.message}")
        }
    }
}