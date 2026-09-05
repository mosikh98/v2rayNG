package com.v2ray.ang.ui.compose

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import coil.compose.AsyncImage
import com.v2ray.ang.AppConfig
import com.v2ray.ang.handler.MmkvManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Palette mood: midnight navy + glossy black surfaces, soft ice-blue highlights,
// moonlit warm gold accents, restrained deep crimson for destructive/active actions —
// inspired by a moonlit night-port skyline and a blue-dressed, gold-accented figure.
private val LightColor = lightColorScheme(
    primary = Color(0xFF8E1B2E), // Deep Crimson (destructive / selection accent)
    onPrimary = Color(0xFFFFFFFF), // White
    primaryContainer = Color(0xFFFFD9DC), // Pale Rose
    onPrimaryContainer = Color(0xFF3B0711), // Dark Crimson
    secondary = Color(0xFFA9790B), // Warm Gold (tabs / selected elements)
    onSecondary = Color(0xFFFFFFFF), // White
    secondaryContainer = Color(0xFFFFE7B0), // Pale Gold
    onSecondaryContainer = Color(0xFF3D2E00), // Dark Brown
    tertiary = Color(0xFF1D7FB7), // Soft Ice Blue (highlights / active elements)
    onTertiary = Color(0xFFFFFFFF), // White
    tertiaryContainer = Color(0xFFD3EBFC), // Pale Ice Blue
    onTertiaryContainer = Color(0xFF063049), // Deep Blue
    error = Color(0xFFBA1A1A), // Red
    errorContainer = Color(0xFFFFDAD6), // Light Red
    onError = Color(0xFFFFFFFF), // White
    onErrorContainer = Color(0xFF410002), // Dark Red
    background = Color(0xFFF5F7FB), // Soft Warm White / Blue-Gray
    onBackground = Color(0xFF171B26), // Deep Navy-Black
    surface = Color(0xFFF5F7FB), // Soft Warm White / Blue-Gray
    onSurface = Color(0xFF171B26), // Deep Navy-Black
    surfaceVariant = Color(0xFFE4E8F2), // Pale Blue-Gray
    onSurfaceVariant = Color(0xFF474C5C), // Dark Blue-Gray
    outline = Color(0xFF787E90), // Medium Blue-Gray
    outlineVariant = Color(0xFFC9CEDC), // Light Blue-Gray
    inverseSurface = Color(0xFF262B3A), // Dark Navy
    inverseOnSurface = Color(0xFFF1F3FA), // Very Light Blue-White
    inversePrimary = Color(0xFFFFB3B8), // Light Rose
    scrim = Color(0xFF000000), // Black
    surfaceTint = Color(0xFF1D7FB7), // Ice Blue
    surfaceContainerLowest = Color(0xFFFFFFFF), // White
    surfaceContainerLow = Color(0xFFEFF2F8), // Very Pale Blue
    surfaceContainer = Color(0xFFE9ECF5), // Pale Blue-Gray Card
    surfaceContainerHigh = Color(0xFFE1E5F0), // Blue-Gray Card Elevated
    surfaceContainerHighest = Color(0xFFD9DEEC), // Blue-Gray
)

private val DarkColor = darkColorScheme(
    primary = Color(0xFFE8536A), // Bright Crimson (destructive / selection accent)
    onPrimary = Color(0xFF4A0016), // Deep Crimson
    primaryContainer = Color(0xFF6B0F26), // Dark Crimson
    onPrimaryContainer = Color(0xFFFFD9DC), // Pale Rose
    secondary = Color(0xFFE8C158), // Warm Gold (tabs / selected elements)
    onSecondary = Color(0xFF3D2E00), // Dark Brown
    secondaryContainer = Color(0xFF5A4400), // Bronze
    onSecondaryContainer = Color(0xFFFFE7B0), // Pale Gold
    tertiary = Color(0xFF7DD3FC), // Soft Ice Blue (highlights / active elements)
    onTertiary = Color(0xFF00344D), // Deep Blue
    tertiaryContainer = Color(0xFF0C4A6E), // Midnight Blue
    onTertiaryContainer = Color(0xFFD3EBFC), // Pale Ice Blue
    error = Color(0xFFFFB4AB), // Light Red
    errorContainer = Color(0xFF93000A), // Dark Red
    onError = Color(0xFF690005), // Deep Red
    onErrorContainer = Color(0xFFFFDAD6), // Light Red
    background = Color(0xFF0A0E1A), // Deep Midnight Navy
    onBackground = Color(0xFFE4E8F5), // Light Blue-White
    surface = Color(0xFF0A0E1A), // Deep Midnight Navy
    onSurface = Color(0xFFE4E8F5), // Light Blue-White
    surfaceVariant = Color(0xFF232A44), // Glossy Navy-Black
    onSurfaceVariant = Color(0xFFC7CDE0), // Light Blue-Gray
    outline = Color(0xFF8D93A8), // Medium Blue-Gray
    outlineVariant = Color(0xFF3A4160), // Dark Blue-Gray
    inverseSurface = Color(0xFFE4E8F5), // Light Blue-White
    inverseOnSurface = Color(0xFF171B26), // Deep Navy-Black
    inversePrimary = Color(0xFF8E1B2E), // Deep Crimson
    scrim = Color(0xFF000000), // Black
    surfaceTint = Color(0xFF7DD3FC), // Ice Blue
    surfaceContainerLowest = Color(0xFF05070F), // Deepest Navy-Black
    surfaceContainerLow = Color(0xFF10142A), // Very Dark Navy Card
    surfaceContainer = Color(0xFF161B33), // Glossy Navy-Black Card
    surfaceContainerHigh = Color(0xFF1D2440), // Elevated Navy Card
    surfaceContainerHighest = Color(0xFF262E4D), // Elevated Navy Card, Higher
)

// Semantic Colors
val colorPing = Color(0xFF4FC3F7) // Soft Ice Blue — matches the light-blue dress accent ("good" status)
val colorPingRed = Color(0xFFFF4D6D) // Rose Red (kept distinct for "bad" status)
val colorConfigType = Color(0xFFE8C158) // Warm Gold
val colorFabActive = Color(0xFFE8536A) // Bright Crimson Rose
val colorFabInactiveLight = Color(0xFF9C9C9C) // Gray
val colorFabInactiveDark = Color(0xFF646464) // Dark Gray
val dividerColorLight = Color(0xFFDDE1EC) // Blue-Gray
val dividerColorDark = Color(0xFF272E4A) // Dark Navy-Gray

// Toast Colors 70%
val toastNormalBgLight = Color(0xB3353A3E) // Dark Gray
val toastNormalBgDark = Color(0xB34A4F54) // Darker Gray
val toastSuccessBg = Color(0xB3388E3C) // Green
val toastErrorBg = Color(0xB3D50000) // Red
val toastInfoBg = Color(0xB30C4A6E) // Midnight Blue
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

    private val _pingColorHex = MutableStateFlow(
        MmkvManager.decodeSettingsString(AppConfig.PREF_CUSTOM_PING_COLOR, "") ?: ""
    )
    val pingColorHex: StateFlow<String> = _pingColorHex.asStateFlow()

    private val _accentColorHex = MutableStateFlow(
        MmkvManager.decodeSettingsString(AppConfig.PREF_CUSTOM_ACCENT_COLOR, "") ?: ""
    )
    val accentColorHex: StateFlow<String> = _accentColorHex.asStateFlow()

    private val _primaryColorHex = MutableStateFlow(
        MmkvManager.decodeSettingsString(AppConfig.PREF_CUSTOM_PRIMARY_COLOR, "") ?: ""
    )
    val primaryColorHex: StateFlow<String> = _primaryColorHex.asStateFlow()

    private val _backgroundImageUri = MutableStateFlow(
        MmkvManager.decodeSettingsString(AppConfig.PREF_CUSTOM_BACKGROUND_URI, "") ?: ""
    )
    val backgroundImageUri: StateFlow<String> = _backgroundImageUri.asStateFlow()

    fun setThemeMode(mode: String) {
        MmkvManager.encodeSettings(AppConfig.PREF_UI_MODE_NIGHT, mode)
        _themeMode.value = mode
    }

    fun setDynamicColorEnabled(enabled: Boolean) {
        MmkvManager.encodeSettings(AppConfig.PREF_DYNAMIC_COLOR, enabled)
        _dynamicColorEnabled.value = enabled
    }

    fun setPingColorHex(hex: String) {
        MmkvManager.encodeSettings(AppConfig.PREF_CUSTOM_PING_COLOR, hex)
        _pingColorHex.value = hex
    }

    fun setAccentColorHex(hex: String) {
        MmkvManager.encodeSettings(AppConfig.PREF_CUSTOM_ACCENT_COLOR, hex)
        _accentColorHex.value = hex
    }

    fun setPrimaryColorHex(hex: String) {
        MmkvManager.encodeSettings(AppConfig.PREF_CUSTOM_PRIMARY_COLOR, hex)
        _primaryColorHex.value = hex
    }

    fun setBackgroundImageUri(uri: String) {
        MmkvManager.encodeSettings(AppConfig.PREF_CUSTOM_BACKGROUND_URI, uri)
        _backgroundImageUri.value = uri
    }

    fun refresh() {
        _themeMode.value =
            MmkvManager.decodeSettingsString(AppConfig.PREF_UI_MODE_NIGHT, "0") ?: "0"
        _dynamicColorEnabled.value =
            MmkvManager.decodeSettingsBool(AppConfig.PREF_DYNAMIC_COLOR, false)
        _pingColorHex.value =
            MmkvManager.decodeSettingsString(AppConfig.PREF_CUSTOM_PING_COLOR, "") ?: ""
        _accentColorHex.value =
            MmkvManager.decodeSettingsString(AppConfig.PREF_CUSTOM_ACCENT_COLOR, "") ?: ""
        _primaryColorHex.value =
            MmkvManager.decodeSettingsString(AppConfig.PREF_CUSTOM_PRIMARY_COLOR, "") ?: ""
        _backgroundImageUri.value =
            MmkvManager.decodeSettingsString(AppConfig.PREF_CUSTOM_BACKGROUND_URI, "") ?: ""
    }
}

/** Parses a "#RRGGBB" / "#AARRGGBB" hex string into a Color, or null if blank/invalid. */
fun parseHexColorOrNull(hex: String): Color? {
    if (hex.isBlank()) return null
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: IllegalArgumentException) {
        null
    }
}

/** Formats a Color back into a "#RRGGBB" hex string (alpha dropped). */
fun Color.toHexString(): String {
    val argb = this.toArgb()
    return String.format("#%06X", 0xFFFFFF and argb)
}

/**
 * Fill color for a card/panel: a translucent "glass" tint over the background image when
 * [LocalGlassEffect] is active, or the normal opaque surface color otherwise.
 */
@Composable
fun glassPanelColor(base: Color = MaterialTheme.colorScheme.surfaceContainer, glassAlpha: Float = 0.46f): Color =
    if (LocalGlassEffect.current) base.copy(alpha = glassAlpha) else base

/**
 * Border color for a card/panel. Deliberately brighter/crisper than the translucent fill so the
 * frame stays clearly readable against a busy background image ("glass, but with a clear edge").
 */
@Composable
fun glassPanelBorderColor(): Color =
    if (LocalGlassEffect.current) {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.32f)
    } else {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
    }

/** Border width for a card/panel — a touch bolder in glass mode so the frame reads clearly. */
@Composable
fun glassPanelBorderWidth(): Dp = if (LocalGlassEffect.current) 1.3.dp else 1.dp

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
val LocalPingColor = compositionLocalOf { colorPing }
/** True when a custom background image is active — surfaces should render as translucent "glass" panels over it. */
val LocalGlassEffect = compositionLocalOf { false }

@Composable
fun AppTheme(
    darkTheme: Boolean = resolveDarkTheme(),
    content: @Composable () -> Unit
) {
    val dynamicColor by ThemeManager.dynamicColorEnabled.collectAsState()
    val pingColorHex by ThemeManager.pingColorHex.collectAsState()
    val accentColorHex by ThemeManager.accentColorHex.collectAsState()
    val primaryColorHex by ThemeManager.primaryColorHex.collectAsState()
    val context = LocalContext.current
    val baseColorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColor
        else -> LightColor
    }
    val customPrimary = parseHexColorOrNull(primaryColorHex)
    val customAccent = parseHexColorOrNull(accentColorHex)
    val backgroundImageUri by ThemeManager.backgroundImageUri.collectAsState()
    val hasBackgroundImage = backgroundImageUri.isNotBlank()
    val colorScheme = if (customPrimary != null || customAccent != null || hasBackgroundImage) {
        baseColorScheme.copy(
            primary = customPrimary ?: baseColorScheme.primary,
            secondary = customAccent ?: baseColorScheme.secondary,
            // Let the custom background image show through every screen's default
            // Scaffold container instead of being covered by a solid background.
            background = if (hasBackgroundImage) Color.Transparent else baseColorScheme.background
        )
    } else {
        baseColorScheme
    }
    val resolvedPingColor = parseHexColorOrNull(pingColorHex) ?: colorPing
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
        LocalPingColor provides resolvedPingColor,
        LocalGlassEffect provides hasBackgroundImage,
        LocalAppSnackbar provides snackbarController
    ) {
        MaterialTheme(
            colorScheme = colorScheme
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(baseColorScheme.surface)
            ) {
                if (hasBackgroundImage) {
                    AsyncImage(
                        model = backgroundImageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(baseColorScheme.surface.copy(alpha = if (darkTheme) 0.72f else 0.82f))
                    )
                }
                AppSnackbarBridge(controller = snackbarController)
                content()
                AppSnackbarHost(hostState = snackbarController.hostState)
            }
        }
    }
}
