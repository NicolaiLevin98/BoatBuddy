package no.uio.ifi.titanic.openstreetmap


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import no.uio.ifi.titanic.ui.openstreetmapScreen.MapScreenViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.TilesOverlay

//Initializes the mapView
@Composable
fun OsmMapView(
    mapScreenViewModel: MapScreenViewModel,
    modifier: Modifier = Modifier,
    onLoad: ((map: MapView) -> Unit)? = null,
    onMarkerFirstPlace: () -> Unit,
    onMarkerPress: () -> Unit
) {
    val context = LocalContext.current

    val searchLocation by mapScreenViewModel.searchLocation.collectAsState()

    LaunchedEffect(key1 = context) {
        Configuration.getInstance().load(context, androidx.preference.PreferenceManager.getDefaultSharedPreferences(context))
    }

    val mapViewState = rememberMapViewWithLifecycle(mapScreenViewModel, onMarkerFirstPlace, onMarkerPress)


    AndroidView(
        factory = { mapViewState },
        modifier = modifier,
        update = { mapView ->
            // Update the map center whenever the searchLocation changes
            searchLocation?.let {
                mapView.controller.setCenter(it)
                mapView.invalidate()
            }

            // Ensure the overlay is added only once
            if (mapView.overlays.none { it is TilesOverlay }) {
                addOpenSeaMapOverlay(mapView, mapScreenViewModel, onMarkerFirstPlace, onMarkerPress)
            }

            // Call additional onLoad logic
            onLoad?.invoke(mapView)
        }
    )
}