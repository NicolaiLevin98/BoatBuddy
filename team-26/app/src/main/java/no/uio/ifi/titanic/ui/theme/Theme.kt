package no.uio.ifi.titanic.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    background = Gray1,
    primary = White1,
    primaryContainer = White1,
    secondary = White1,
    tertiary = White1,
    inverseOnSurface = Color.Black,
    onPrimary = White1,
    onSurface = Color.Black,
    onBackground = White1,
    onSurfaceVariant = White1,
    surfaceVariant = White1,
    tertiaryContainer = Gray4,
    surfaceDim = White1,
)

private val LightColorScheme = lightColorScheme(
    background = White1,
    primary = Teal0,
    primaryContainer = Teal100,
    secondary = Teal200,
    tertiary = Teal300,
    inverseOnSurface = Teal400,
    onPrimary = Teal400,
    onSurface = White1,
    onBackground = Color.Black,
    onSurfaceVariant = Teal500,
    surfaceVariant = Color.Gray,
    tertiaryContainer = Gray2,
    surfaceDim = Gray3,
)


@Composable
fun TitanicTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) DarkColorScheme else LightColorScheme
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}