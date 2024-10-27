package no.uio.ifi.titanic.ui.homeScreen.homeScreenComponents.metAlertsComponents

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import no.uio.ifi.titanic.R

//Gets the icon corresponding to the alertDescription and level provided by MetAlerts.
@Composable
fun getMetAlertsIcon(alertDescription: String, alertLevel: String): Painter {
    Log.d("GetMetAlertIcon", "Received alert description: $alertDescription")
    Log.d("GetMetAlertIcon", "Received alertlevel: $alertLevel")

    val descriptionMap = mapOf(
        //Avalanches
        Pair("avalanches", "red") to R.drawable.icon_warning_avalanches_red,
        Pair("avalanches", "orange") to R.drawable.icon_warning_avalanches_orange,
        Pair("avalanches", "yellow") to R.drawable.icon_warning_avalanches_yellow,

        //Blowing snow
        Pair("blowingSnow", "red") to R.drawable.icon_warning_snow_red,
        Pair("blowingSnow", "orange") to R.drawable.icon_warning_snow_orange,
        Pair("blowingSnow", "yellow") to R.drawable.icon_warning_snow_yellow,

        //Snowice
        Pair("snowice", "red") to R.drawable.icon_warning_snow_red,
        Pair("snowice", "orange") to R.drawable.icon_warning_snow_orange,
        Pair("snowice", "yellow") to R.drawable.icon_warning_snow_yellow,

        //DrivingConditions
        Pair("drivingConditions", "red") to R.drawable.icon_warning_drivingconditions_red,
        Pair("drivingConditions", "orange") to R.drawable.icon_warning_drivingconditions_orange,
        Pair("drivingConditions", "yellow") to R.drawable.icon_warning_drivingconditions_yellow,

        //Flood
        Pair("flood", "red") to R.drawable.icon_warning_flood_red,
        Pair("flood", "orange") to R.drawable.icon_warning_flood_orange,
        Pair("flood", "yellow") to R.drawable.icon_warning_flood_yellow,

        //Forestfire
        Pair("forestfire", "red") to R.drawable.icon_warning_forestfire_red,
        Pair("forestfire", "orange") to R.drawable.icon_warning_forestfire_orange,
        Pair("forestfire", "yellow") to R.drawable.icon_warning_forestfire_yellow,

        //Gale
        Pair("gale", "red") to R.drawable.icon_warning_wind_red,
        Pair("gale", "orange") to R.drawable.icon_warning_wind_orange,
        Pair("gale", "yellow") to R.drawable.icon_warning_wind_yellow,

        //Ice
        Pair("ice", "red") to R.drawable.icon_warning_drivingconditions_red,
        Pair("ice", "orange") to R.drawable.icon_warning_drivingconditions_orange,
        Pair("ice", "yellow") to R.drawable.icon_warning_drivingconditions_yellow,

        //Icing
        Pair("icing", "red") to R.drawable.icon_warning_generic_red,
        Pair("icing", "orange") to R.drawable.icon_warning_generic_orange,
        Pair("icing", "yellow") to R.drawable.icon_warning_generic_yellow,

        //Landslide
        Pair("landslide", "red") to R.drawable.icon_warning_landslide_red,
        Pair("landslide", "orange") to R.drawable.icon_warning_landslide_orange,
        Pair("landslide", "yellow") to R.drawable.icon_warning_landslide_yellow,

        //PolarLow
        Pair("polarLow", "red") to R.drawable.icon_warning_polarlow_red,
        Pair("polarLow", "orange") to R.drawable.icon_warning_polarlow_orange,
        Pair("polarLow", "yellow") to R.drawable.icon_warning_polarlow_yellow,

        //Rain
        Pair("rain", "red") to R.drawable.icon_warning_rain_red,
        Pair("rain", "orange") to R.drawable.icon_warning_rain_orange,
        Pair("rain", "yellow") to R.drawable.icon_warning_rain_yellow,

        //Rainflood
        Pair("rainflood", "red") to R.drawable.icon_warning_rainflood_red,
        Pair("rainflood", "orange") to R.drawable.icon_warning_rainflood_orange,
        Pair("rainflood", "yellow") to R.drawable.icon_warning_rainflood_yellow,

        //Snow
        Pair("snow", "red") to R.drawable.icon_warning_snow_red,
        Pair("snow", "orange") to R.drawable.icon_warning_snow_orange,
        Pair("snow", "yellow") to R.drawable.icon_warning_snow_yellow,

        //Stormsurge
        Pair("stormSurge", "red") to R.drawable.icon_warning_stormsurge_red,
        Pair("stormSurge", "orange") to R.drawable.icon_warning_stormsurge_orange,
        Pair("stormSurge", "yellow") to R.drawable.icon_warning_stormsurge_yellow,

        //Lightning
        Pair("lightning", "red") to R.drawable.icon_warning_lightning_red,
        Pair("lightning", "orange") to R.drawable.icon_warning_lightning_orange,
        Pair("lightning", "yellow") to R.drawable.icon_warning_lightning_yellow,

        //Wind
        Pair("wind", "red") to R.drawable.icon_warning_wind_red,
        Pair("wind", "orange") to R.drawable.icon_warning_wind_orange,
        Pair("wind", "yellow") to R.drawable.icon_warning_wind_yellow,


        //Unknown
        Pair("unknown", "red") to R.drawable.icon_warning_generic_red,
        Pair("unknown", "orange") to R.drawable.icon_warning_generic_orange,
        Pair("unknown", "yellow") to R.drawable.icon_warning_generic_yellow,


        Pair("unknown", "extreme") to R.drawable.icon_warning_extreme

    )

    val genericIcons = mapOf(
        "red" to R.drawable.icon_warning_generic_red,
        "orange" to R.drawable.icon_warning_generic_orange,
        "yellow" to R.drawable.icon_warning_generic_yellow
    )

    val specificResourceId = descriptionMap[Pair(alertDescription.lowercase(), alertLevel.lowercase())]
    val resourceId = specificResourceId ?: genericIcons[alertLevel.lowercase()] ?: R.drawable.icon_warning_generic_yellow
    return painterResource(id = resourceId)
}
