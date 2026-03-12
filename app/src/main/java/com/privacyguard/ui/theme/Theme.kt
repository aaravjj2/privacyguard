package com.privacyguard.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = TrustBlue,
    onPrimary = SurfaceLight,
    primaryContainer = TrustBlueLight,
    onPrimaryContainer = OnSurfaceLight,
    secondary = SuccessGreen,
    onSecondary = SurfaceLight,
    background = BackgroundLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceLight,
    error = AlertRed,
    onError = SurfaceLight,
    errorContainer = AlertRed.copy(alpha = 0.12f),
    onErrorContainer = AlertRedDark,
    outline = SurfaceVariantLight
)

private val DarkColorScheme = darkColorScheme(
    primary = TrustBlueLight,
    onPrimary = OnSurfaceDark,
    primaryContainer = TrustBlueDark,
    onPrimaryContainer = OnSurfaceDark,
    secondary = SuccessGreen,
    onSecondary = OnSurfaceDark,
    background = BackgroundDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceDark,
    error = AlertRed,
    onError = OnSurfaceDark,
    errorContainer = AlertRed.copy(alpha = 0.12f),
    onErrorContainer = AlertRed,
    outline = SurfaceVariantDark
)

@Composable
fun PrivacyGuardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = colorScheme.primary.toArgb()
                WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PrivacyGuardTypography,
        content = content
    )
}
