package no.uio.ifi.titanic.ui.homeScreen.homeScreenComponents.weather

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import no.uio.ifi.titanic.R

@Composable
fun getWeatherIcon(weatherDescription: String): Painter {
    val weatherSymbolKeys = mapOf(
        "clearsky_day" to R.drawable.clearsky_day,
        "clearsky_night" to R.drawable.clearsky_night,
        "clearsky_polartwilight" to R.drawable.clearsky_polartwilight,
        "fair_day" to R.drawable.fair_day,
        "fair_night" to R.drawable.fair_night,
        "fair_polartwilight" to R.drawable.fair_polartwilight,
        "partlycloudy_day" to R.drawable.partlycloudy_day,
        "partlycloudy_night" to R.drawable.partlycloudy_night,
        "partlycloudy_polartwilight" to R.drawable.partlycloudy_polartwilight,
        "cloudy" to R.drawable.cloudy,
        "rainshowers_day" to R.drawable.rainshowers_day,
        "rainshowers_night" to R.drawable.rainshowers_night,
        "rainshowers_polartwilight" to R.drawable.rainshowers_polartwilight,
        "rainshowersandthunder_day" to R.drawable.rainshowersandthunder_day,
        "rainshowersandthunder_night" to R.drawable.rainshowersandthunder_night,
        "rainshowersandthunder_polartwilight" to R.drawable.rainshowersandthunder_polartwilight,
        "sleetshowers_day" to R.drawable.sleetshowers_day,
        "sleetshowers_night" to R.drawable.sleetshowers_night,
        "sleetshowers_polartwilight" to R.drawable.sleetshowers_polartwilight,
        "snowshowers_day" to R.drawable.snowshowers_day,
        "snowshowers_night" to R.drawable.snowshowers_night,
        "snowshowers_polartwilight" to R.drawable.snowshowers_polartwilight,
        "rain" to R.drawable.rain,
        "heavyrain" to R.drawable.heavyrain,
        "heavyrainandthunder" to R.drawable.heavyrainandthunder,
        "sleet" to R.drawable.sleet,
        "snow" to R.drawable.snow,
        "snowandthunder" to R.drawable.snowandthunder,
        "fog" to R.drawable.fog,
        "sleetshowersandthunder_day" to R.drawable.sleetshowersandthunder_day,
        "sleetshowersandthunder_night" to R.drawable.sleetshowersandthunder_night,
        "sleetshowersandthunder_polartwilight" to R.drawable.sleetshowersandthunder_polartwilight,
        "snowshowersandthunder_day" to R.drawable.snowshowersandthunder_day,
        "snowshowersandthunder_night" to R.drawable.snowshowersandthunder_night,
        "snowshowersandthunder_polartwilight" to R.drawable.snowshowersandthunder_polartwilight,
        "rainandthunder" to R.drawable.rainandthunder,
        "sleetandthunder" to R.drawable.sleetandthunder,
        "lightrainshowersandthunder_day" to R.drawable.lightrainshowersandthunder_day,
        "lightrainshowersandthunder_night" to R.drawable.lightrainshowersandthunder_night,
        "lightrainshowersandthunder_polartwilight" to R.drawable.lightrainshowersandthunder_polartwilight,
        "heavyrainshowersandthunder_day" to R.drawable.heavyrainshowersandthunder_day,
        "heavyrainshowersandthunder_night" to R.drawable.heavyrainshowersandthunder_night,
        "heavyrainshowersandthunder_polartwilight" to R.drawable.heavyrainshowersandthunder_polartwilight,
        "lightssleetshowersandthunder_day" to R.drawable.lightssleetshowersandthunder_day,
        "lightssleetshowersandthunder_night" to R.drawable.lightssleetshowersandthunder_night,
        "lightssleetshowersandthunder_polartwilight" to R.drawable.lightssleetshowersandthunder_polartwilight,
        "heavysleetshowersandthunder_day" to R.drawable.heavysleetshowersandthunder_day,
        "heavysleetshowersandthunder_night" to R.drawable.heavysleetshowersandthunder_night,
        "heavysleetshowersandthunder_polartwilight" to R.drawable.heavysleetshowersandthunder_polartwilight,
        "lightssnowshowersandthunder_day" to R.drawable.lightssnowshowersandthunder_day,
        "lightssnowshowersandthunder_night" to R.drawable.lightssnowshowersandthunder_night,
        "lightssnowshowersandthunder_polartwilight" to R.drawable.lightssnowshowersandthunder_polartwilight,
        "heavysnowshowersandthunder_day" to R.drawable.heavysnowshowersandthunder_day,
        "heavysnowshowersandthunder_night" to R.drawable.heavysnowshowersandthunder_night,
        "heavysnowshowersandthunder_polartwilight" to R.drawable.heavysnowshowersandthunder_polartwilight,
        "lightrainandthunder" to R.drawable.lightrainandthunder,
        "lightsleetandthunder" to R.drawable.lightsleetandthunder,
        "heavysleetandthunder" to R.drawable.heavysleetandthunder,
        "lightsnowandthunder" to R.drawable.lightsnowandthunder,
        "heavysnowandthunder" to R.drawable.heavysnowandthunder,
        "lightrainshowers_day" to R.drawable.lightrainshowers_day,
        "lightrainshowers_night" to R.drawable.lightrainshowers_night,
        "lightrainshowers_polartwilight" to R.drawable.lightrainshowers_polartwilight,
        "heavyrainshowers_day" to R.drawable.heavyrainshowers_day,
        "heavyrainshowers_night" to R.drawable.heavyrainshowers_night,
        "heavyrainshowers_polartwilight" to R.drawable.heavyrainshowers_polartwilight,
        "lightsleetshowers_day" to R.drawable.lightsleetshowers_day,
        "lightsleetshowers_night" to R.drawable.lightsleetshowers_night,
        "lightsleetshowers_polartwilight" to R.drawable.lightsleetshowers_polartwilight,
        "heavysleetshowers_day" to R.drawable.heavysleetshowers_day,
        "heavysleetshowers_night" to R.drawable.heavysleetshowers_night,
        "heavysleetshowers_polartwilight" to R.drawable.heavysleetshowers_polartwilight,
        "lightsnowshowers_day" to R.drawable.lightsnowshowers_day,
        "lightsnowshowers_night" to R.drawable.lightsnowshowers_night,
        "lightsnowshowers_polartwilight" to R.drawable.lightsnowshowers_polartwilight,
        "heavysnowshowers_day" to R.drawable.heavysnowshowers_day,
        "heavysnowshowers_night" to R.drawable.heavysnowshowers_night,
        "heavysnowshowers_polartwilight" to R.drawable.heavysnowshowers_polartwilight,
        "lightrain" to R.drawable.lightrain,
        "lightsleet" to R.drawable.lightsleet,
        "heavysleet" to R.drawable.heavysleet,
        "lightsnow" to R.drawable.lightsnow,
        "heavysnow" to R.drawable.heavysnow
    )

    //can safely non-null assert as we check all possible outcomes
    return if (weatherDescription in weatherSymbolKeys.keys) {
        painterResource(id = weatherSymbolKeys[weatherDescription]!!)
    } else {
        painterResource(id = R.drawable.cloudsvg)
    }
}