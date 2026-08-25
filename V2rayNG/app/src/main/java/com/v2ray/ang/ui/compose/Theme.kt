package com.v2ray.ang.ui.compose

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.v2ray.ang.AppConfig
import com.v2ray.ang.handler.MmkvManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Palette mood: crimson red + antique gold + indigo-lavender on near-black,
// inspired by a dark red/gold portrait, a blue-purple figure, and a purple/gold night skyline.
private val LightColor = lightColorScheme(
    primary = Color(0xFF8E1B2E), // Deep Crimson
    onPrimary = Color(0xFFFFFFFF), // White
    primaryContainer = Color(0xFFFFD9DC), // Pale Rose
    onPrimaryContainer = Color(0xFF3B0711), // Dark Crimson
    secondary = Color(0xFFA9790B), // Antique Gold
    onSecondary = Color(0xFFFFFFFF), // White
    secondaryContainer = Color(0xFFFFE7B0), // Pale Gold
    onSecondaryContainer = Color(0xFF3D2E00), // Dark Brown
    tertiary = Color(0xFF5B4B8A), // Indigo Purple
    onTertiary = Color(0xFFFFFFFF), // White
    tertiaryContainer = Color(0xFFE3DBFF), // Pale Lavender
    onTertiaryContainer = Color(0xFF1E1147), // Deep Indigo
    error = Color(0xFFBA1A1A), // Red
    errorContainer = Color(0xFFFFDAD6), // Light Red
    onError = Color(0xFFFFFFFF), // White
    onErrorContainer = Color(0xFF410002), // Dark Red
    background = Color(0xFFFFF8F5), // Warm Ivory
    onBackground = Color(0xFF201A1A), // Near Black
    surface = Color(0xFFFFF8F5), // Warm Ivory
    onSurface = Color(0xFF201A1A), // Near Black
    surfaceVariant = Color(0xFFF0E0DE), // Pale Rose Gray
    onSurfaceVariant = Color(0xFF524345), // Dark Warm Gray
    outline = Color(0xFF847374), // Medium Warm Gray
    outlineVariant = Color(0xFFD7C1C2), // Light Rose Gray
    inverseSurface = Color(0xFF362F2F), // Dark Warm Gray
    inverseOnSurface = Color(0xFFFBEEED), // Very Light Rose
    inversePrimary = Color(0xFFFFB3B8), // Light Rose
    scrim = Color(0xFF000000), // Black
    surfaceTint = Color(0xFF8E1B2E), // Deep Crimson
    surfaceContainerLowest = Color(0xFFFFFFFF), // White
    surfaceContainerLow = Color(0xFFFFF1EE), // Very Pale Rose
    surfaceContainer = Color(0xFFFCE8E5), // Pale Rose
    surfaceContainerHigh = Color(0xFFF6E2DF), // Pale Rose Gray
    surfaceContainerHighest = Color(0xFFF1DCDA), // Rose Gray
)

private val DarkColor = darkColorScheme(
    primary = Color(0xFFE8536A), // Bright Crimson Rose
    onPrimary = Color(0xFF4A0016), // Deep Crimson
    primaryContainer = Color(0xFF6B0F26), // Dark Crimson
    onPrimaryContainer = Color(0xFFFFD9DC), // Pale Rose
    secondary = Color(0xFFE8C158), // Warm Gold
    onSecondary = Color(0xFF3D2E00), // Dark Brown
    secondaryContainer = Color(0xFF5A4400), // Bronze
    onSecondaryContainer = Color(0xFFFFE7B0), // Pale Gold
    tertiary = Color(0xFFB6A6E8), // Soft Lavender Indigo
    onTertiary = Color(0xFF2E1F5E), // Deep Indigo
    tertiaryContainer = Color(0xFF443577), // Indigo Purple
    onTertiaryContainer = Color(0xFFE3DBFF), // Pale Lavender
    error = Color(0xFFFFB4AB), // Light Red
    errorContainer = Color(0xFF93000A), // Dark Red
    onError = Color(0xFF690005), // Deep Red
    onErrorContainer = Color(0xFFFFDAD6), // Light Red
    background = Color(0xFF120D10), // Warm Near Black
    onBackground = Color(0xFFEDE0DF), // Light Warm Gray
    surface = Color(0xFF120D10), // Warm Near Black
    onSurface = Color(0xFFEDE0DF), // Light Warm Gray
    surfaceVariant = Color(0xFF2E2225), // Dark Rose Gray
    onSurfaceVariant = Color(0xFFD7C1C2), // Light Rose Gray
    outline = Color(0xFF9F8C8D), // Medium Warm Gray
    outlineVariant = Color(0xFF524345), // Dark Warm Gray
    inverseSurface = Color(0xFFEDE0DF), // Light Warm Gray
    inverseOnSurface = Color(0xFF201A1A), // Near Black
    inversePrimary = Color(0xFF8E1B2E), // Deep Crimson
    scrim = Color(0xFF000000), // Black
    surfaceTint = Color(0xFFE8536A), // Bright Crimson Rose
    surfaceContainerLowest = Color(0xFF0B0708), // Deepest Black
    surfaceContainerLow = Color(0xFF1A1416), // Very Dark Rose Gray
    surfaceContainer = Color(0xFF1E181B), // Dark Rose Gray
    surfaceContainerHigh = Color(0xFF292225), // Dark Rose Gray
    surfaceContainerHighest = Color(0xFF342C2F), // Rose Gray
)

// Semantic Colors
val colorPing = Color(0xFF4CAF7D) // Green (kept distinct for "good" status)
val colorPingRed = Color(0xFFFF4D6D) // Rose Red (kept distinct for "bad" status)
val colorConfigType = Color(0xFFE8C158) // Warm Gold
val colorFabActive = Color(0xFFE8536A) // Bright Crimson Rose
val colorFabInactiveLight = Color(0xFF9C9C9C) // Gray
val colorFabInactiveDark = Color(0xFF646464) // Dark Gray
val dividerColorLight = Color(0xFFE5D9D7) // Warm Light Gray
val dividerColorDark = Color(0xFF3A3033) // Warm Dark Gray

// Toast Colors 70%
val toastNormalBgLight = Color(0xB3353A3E) // Dark Gray
val toastNormalBgDark = Color(0xB34A4F54) // Darker Gray
val toastSuccessBg = Color(0xB3388E3C) // Green
val toastErrorBg = Color(0xB3D50000) // Red
val toastInfoBg = Color(0xB3443577) // Indigo Purple
val toastIconCircleBg = Color(0x33FFFFFF) // Semi-transparent White
val toastTextColor = Color.White // White

object ThemeManager {
    private val _themeMode = MutableStateFlow(
        MmkvManager.decodeSettingsString(AppConfig.PREF_UI_MODE_NIGHT, "0") ?: "0"
    )
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _dynamicColorEnabled = MutableStateFlow(
        MmkvManager.decodeSettingsBool(AppConfig.PREF_DYNAMIC_COLOR, false)
    )
    val dynamicColorEnabled: StateFlow<Boolean> = _dynamicColorEnabled.asStateFlow()

    fun setThemeMode(mode: String) {
        MmkvManager.encodeSettings(AppConfig.PREF_UI_MODE_NIGHT, mode)
        _themeMode.value = mode
    }

    fun setDynamicColorEnabled(enabled: Boolean) {
        MmkvManager.encodeSettings(AppConfig.PREF_DYNAMIC_COLOR, enabled)
        _dynamicColorEnabled.value = enabled
    }

    fun refresh() {
        _themeMode.value =
            MmkvManager.decodeSettingsString(AppConfig.PREF_UI_MODE_NIGHT, "0") ?: "0"
        _dynamicColorEnabled.value =
            MmkvManager.decodeSettingsBool(AppConfig.PREF_DYNAMIC_COLOR, false)
    }
}

@Composable
fun resolveDarkTheme(): Boolean {
    val mode by ThemeManager.themeMode.collectAsState()
    return when (mode) {
        "1" -> false
        "2" -> true
        else -> isSystemInDarkTheme()
    }
}

val LocalDarkTheme = compositionLocalOf { false }

@Composable
fun AppTheme(
    darkTheme: Boolean = resolveDarkTheme(),
    content: @Composable () -> Unit
) {
    val dynamicColor by ThemeManager.dynamicColorEnabled.collectAsState()
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColor
        else -> LightColor
    }
    val snackbarController = rememberAppSnackbarController()

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? Activity ?: return@SideEffect
            val window = activity.window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalDarkTheme provides darkTheme,
        LocalAppSnackbar provides snackbarController
    ) {
        MaterialTheme(
            colorScheme = colorScheme
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AppSnackbarBridge(controller = snackbarController)
                content()
                AppSnackbarHost(hostState = snackbarController.hostState)
            }
        }
    }
}
