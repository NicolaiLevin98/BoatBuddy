package no.uio.ifi.titanic.openstreetmap

import android.content.Context
import android.graphics.Color
import android.location.Location
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import no.uio.ifi.titanic.R
import no.uio.ifi.titanic.ui.openstreetmapScreen.MapScreenViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.TilesOverlay


class MapEventsReceiverImpl(
    private val mapView: MapView,
    private val viewModel: MapScreenViewModel,
    private val onMarkerFirstPlace: () -> Unit,
    private val onMarkerPress: () -> Unit
) : MapEventsReceiver {
    private var currentMarker: Marker? = null
    private var isFirstMarkerPlacement = true

    // This happens when the user taps the screen
    override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
        viewModel.viewModelScope.launch {
            delay(300) // Delays the following operations by 300 milliseconds

            // Sets a marker if one doesn't exist already, changes location if it does
            if (currentMarker == null) {
                currentMarker = Marker(mapView).apply {
                    position = p
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    icon = ContextCompat.getDrawable(mapView.context, R.drawable.marker_map)
                    setOnMarkerClickListener { marker, mapView ->
                        onMarkerPress()
                        true
                    }
                    mapView.overlays.add(this)
                }
            } else {
                // Move the existing marker to the new position
                currentMarker?.position = p
                mapView.controller.animateTo(p)
                mapView.invalidate()
            }

            // Center the view to the new marker position
            mapView.controller.animateTo(p)
            mapView.invalidate()
            viewModel.updateLocation(p)

            // Toggle weather panel if it is the first marker placement
            if (isFirstMarkerPlacement) {
                onMarkerFirstPlace()
                isFirstMarkerPlacement = false
            }
        }
        return true
    }

    // Handles long press on the map
    override fun longPressHelper(p: GeoPoint): Boolean {
        viewModel.viewModelScope.launch {
            if (currentMarker != null) {
                mapView.overlays.remove(currentMarker)
                currentMarker = null
                mapView.invalidate() // Refresh the map to reflect changes
                isFirstMarkerPlacement = true // Reset for new marker placement
            }
        }
        return true
    }
}

//Adds the OpenSeaMap tiles and Map Events Reciever overlay to the map
fun addOpenSeaMapOverlay(
    mapView: MapView,
    viewModel: MapScreenViewModel,
    onMarkerFirstPlace: () -> Unit,
    onMarkerPress: () -> Unit
) {
    val tileSource = XYTileSource(
        "OpenSeaMap",
        0, 18, 256, ".png",
        arrayOf("https://tiles.openseamap.org/seamark/")
    )

    val tilesOverlay = TilesOverlay(MapTileProviderBasic(mapView.context, tileSource), mapView.context).apply {
        loadingBackgroundColor = Color.TRANSPARENT
    }
    val mapEventsReceiver = MapEventsReceiverImpl(mapView, viewModel, onMarkerFirstPlace, onMarkerPress)
    val mapEventsOverlay = MapEventsOverlay(mapEventsReceiver)
    mapView.overlays.add(tilesOverlay)
    mapView.overlays.add(mapEventsOverlay)
}

//Creates and returns the mapView for the map
@Composable
fun rememberMapViewWithLifecycle(mapScreenViewModel: MapScreenViewModel, onMarkerFirstPlace: () -> Unit, onMarkerPress: () -> Unit): MapView {
    Log.d("rememberMapViewWithLifecycle", "Rerendering rememberMapViewWithLifecycle")
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val userLocationState = remember { mutableStateOf<GeoPoint?>(null) }
    val searchLocation by mapScreenViewModel.searchLocation.collectAsState()

    val mapView = remember {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        MapView(context).apply {
            setMultiTouchControls(true)
            controller.setZoom(15.0)
            configureTileProvider(this)
        }
    }

    //Adds the current position marker
    LaunchedEffect(key1 = context) {
        Log.d("MapView", "Running LaunchedEffect with key1 = context")
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val userLocation = GeoPoint(location.latitude, location.longitude)
                        userLocationState.value = userLocation
                        mapScreenViewModel.setInitialUserLocation(userLocation)

                        // Call to add a marker at the user's initial location
                        addInitialMarker(mapView, userLocation, onMarkerFirstPlace, onMarkerPress)
                    } else {
                        Log.d("MapView", "Last known location is null. Unable to get location.")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("MapView", "Failed to get last known location: ${e.message}")
                }
        } catch (e: SecurityException) {
            Log.e("MapView", "Security exception: ${e.message}")
        }
    }

    // Observe changes in the search location and update the map center accordingly
    LaunchedEffect(searchLocation) {
        Log.d("MapView", "Searching location")
        searchLocation?.let {
            mapView.controller.setCenter(it)
        } ?: run {
            userLocationState.value?.let { mapView.controller.setCenter(it) }
        }
    }

    // Observe changes in the user's initial location state
    LaunchedEffect(userLocationState.value) {
        userLocationState.value?.let {
            mapView.controller.setCenter(it)
        }
    }

    // Lifecycle observer for handling map lifecycle events
    val lifecycleObserver = remember(mapView) {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
    }

    // Add and remove the lifecycle observer as needed
    DisposableEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
            mapView.onDetach()
        }
    }

    //Refreshes overlays when the mapView changes
    LaunchedEffect(key1 = mapView) {
        addOpenSeaMapOverlay(mapView, mapScreenViewModel, onMarkerFirstPlace, onMarkerPress)
    }

    return mapView
}

//Adds the initial marker based on the users location
private fun addInitialMarker(
    mapView: MapView,
    location: GeoPoint,
    onMarkerFirstPlace: () -> Unit,
    onMarkerPress: () -> Unit
) {
    val marker = Marker(mapView).apply {
        position = location
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        icon = ContextCompat.getDrawable(mapView.context, R.drawable.my_location_marker)
        setOnMarkerClickListener { marker, mapView ->
            onMarkerPress()
            true
        }
    }
    mapView.overlays.add(marker)
    mapView.controller.setCenter(location)
    mapView.invalidate()
    onMarkerFirstPlace()
}

//Specifies an endpoint to get map tiles from
private fun configureTileProvider(mapView: MapView) {
    val tileProvider = MapTileProviderBasic(mapView.context).apply {
        tileSource = XYTileSource("OpenStreetMap", 0, 18, 256, ".png", arrayOf("https://tile.openstreetmap.org/"))
    }
    mapView.tileProvider = tileProvider
}
