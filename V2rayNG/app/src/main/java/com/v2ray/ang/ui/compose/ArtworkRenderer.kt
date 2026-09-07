package com.v2ray.ang.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

/**
 * Renders the user's Custom Artwork image with their saved crop/zoom/position/blur/brightness,
 * plus a dark-or-light scrim on top so UI drawn above it stays readable.
 *
 * This is the single shared rendering pipeline used everywhere the artwork appears (the app-wide
 * background in [AppTheme] and the stronger composition behind the navigation drawer header), so
 * the same image always looks like one consistent, intentional composition rather than a
 * differently-cropped copy on every screen.
 *
 * [overlayMultiplier] and [opacityMultiplier] let each call site adapt the same saved settings to
 * its own context (e.g. the drawer header renders the artwork more prominently with a lighter
 * overlay, while text-dense screens lean on the user's overlay strength as-is).
 */
@Composable
fun ArtworkLayer(
    imageUri: String,
    settings: ArtworkSettings,
    darkTheme: Boolean,
    modifier: Modifier = Modifier,
    overlayMultiplier: Float = 1f,
    opacityMultiplier: Float = 1f
) {
    if (imageUri.isBlank()) return

    val brightnessFilter = remember(settings.brightness) {
        val offset = (settings.brightness * 255f).coerceIn(-255f, 255f)
        val matrix = ColorMatrix().apply {
            this[0, 4] = offset
            this[1, 4] = offset
            this[2, 4] = offset
        }
        ColorFilter.colorMatrix(matrix)
    }

    Box(modifier = modifier.clipToBounds()) {
        AsyncImage(
            model = imageUri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            colorFilter = brightnessFilter,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = settings.zoom
                    scaleY = settings.zoom
                    translationX = settings.offsetXFraction * (size.width * (settings.zoom - 1f) / 2f)
                    translationY = settings.offsetYFraction * (size.height * (settings.zoom - 1f) / 2f)
                    alpha = (settings.opacity * opacityMultiplier).coerceIn(0f, 1f)
                }
                .then(
                    if (settings.blurDp > 0.5f) Modifier.blur(settings.blurDp.dp) else Modifier
                )
        )
        val scrimColor = if (darkTheme) Color.Black else Color.White
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(scrimColor.copy(alpha = (settings.overlayStrength * overlayMultiplier).coerceIn(0f, 1f)))
        )
    }
}
