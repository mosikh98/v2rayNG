package com.v2ray.ang.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.v2ray.ang.R
import com.v2ray.ang.ui.base.BaseComponentActivity
import com.v2ray.ang.ui.compose.AppTopBar
import com.v2ray.ang.ui.compose.ArtworkLayer
import com.v2ray.ang.ui.compose.ArtworkSettings
import com.v2ray.ang.ui.compose.LocalDarkTheme
import com.v2ray.ang.ui.compose.NavigationBarsSpacer
import com.v2ray.ang.ui.compose.PreferenceGroupHeader
import com.v2ray.ang.ui.compose.ThemeManager

/**
 * Real, in-app Custom Artwork picker + live editor.
 *
 * Flow: pick any image from the device via the Storage Access Framework (persists read access
 * across restarts, no broad storage permission needed) → live-preview it composed with the app's
 * own translucent card style → drag to reposition / pinch to zoom, plus fine sliders for zoom,
 * opacity, blur, brightness and overlay strength → Apply persists everything to [ThemeManager],
 * which every screen already observes, so the whole app updates immediately without a restart.
 */
class ArtworkEditorActivity : BaseComponentActivity() {

    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                runCatching {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }
                pendingPickedUri = uri.toString()
            }
        }

    // Bridged into the composition below via a mutableState the screen reads each recomposition.
    private var pendingPickedUri: String? by mutableStateOf(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @Composable
    override fun ScreenContent() {
        ArtworkEditorScreen(
            pickedUri = pendingPickedUri,
            onConsumePickedUri = { pendingPickedUri = null },
            onPickImage = { imagePicker.launch(arrayOf("image/*")) },
            onBackClick = { finish() },
            onApplied = { finish() }
        )
    }
}

@Composable
private fun ArtworkEditorScreen(
    pickedUri: String?,
    onConsumePickedUri: () -> Unit,
    onPickImage: () -> Unit,
    onBackClick: () -> Unit,
    onApplied: () -> Unit
) {
    val savedUri by ThemeManager.backgroundImageUri.collectAsState()
    val savedSettings by ThemeManager.artworkSettings.collectAsState()

    var draftUri by rememberSaveable { mutableStateOf(savedUri) }
    var offsetX by rememberSaveable { mutableStateOf(savedSettings.offsetXFraction) }
    var offsetY by rememberSaveable { mutableStateOf(savedSettings.offsetYFraction) }
    var zoom by rememberSaveable { mutableStateOf(savedSettings.zoom) }
    var opacity by rememberSaveable { mutableStateOf(savedSettings.opacity) }
    var blur by rememberSaveable { mutableStateOf(savedSettings.blurDp) }
    var brightness by rememberSaveable { mutableStateOf(savedSettings.brightness) }
    var overlay by rememberSaveable { mutableStateOf(savedSettings.overlayStrength) }

    if (pickedUri != null && pickedUri != draftUri) {
        draftUri = pickedUri
        // A newly picked image starts from a clean, centered composition rather than
        // inheriting whatever pan/zoom happened to be dialed in for the previous image.
        offsetX = ArtworkSettings.Default.offsetXFraction
        offsetY = ArtworkSettings.Default.offsetYFraction
        zoom = ArtworkSettings.Default.zoom
        onConsumePickedUri()
    }

    val draftSettings = ArtworkSettings(
        offsetXFraction = offsetX,
        offsetYFraction = offsetY,
        zoom = zoom,
        opacity = opacity,
        blurDp = blur,
        brightness = brightness,
        overlayStrength = overlay
    )
    val darkTheme = LocalDarkTheme.current

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.title_custom_artwork),
                onBackClick = onBackClick
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Live preview: drag to reposition, pinch to zoom — updates instantly.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.8f)
                    .clip(RoundedCornerShape(20.dp))
                    .pointerInput(draftUri) {
                        detectTransformGestures { _, pan, gestureZoom, _ ->
                            zoom = (zoom * gestureZoom).coerceIn(1f, 3f)
                            val range = (size.width * (zoom - 1f) / 2f).coerceAtLeast(1f)
                            offsetX = (offsetX + pan.x / range).coerceIn(-1f, 1f)
                            val rangeY = (size.height * (zoom - 1f) / 2f).coerceAtLeast(1f)
                            offsetY = (offsetY + pan.y / rangeY).coerceIn(-1f, 1f)
                        }
                    }
            ) {
                if (draftUri.isNotBlank()) {
                    ArtworkLayer(
                        imageUri = draftUri,
                        settings = draftSettings,
                        darkTheme = darkTheme,
                        modifier = Modifier.fillMaxSize()
                    )
                    // A sample translucent card, so the user sees exactly how the artwork
                    // will look composed with the application's own UI style.
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.46f))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.title_custom_artwork),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.summary_artwork_no_image),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            if (draftUri.isNotBlank()) {
                Text(
                    text = stringResource(R.string.summary_artwork_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            OutlinedButton(
                onClick = onPickImage,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_image_24dp),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = stringResource(R.string.action_choose_image),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            if (draftUri.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                PreferenceGroupHeader(title = stringResource(R.string.title_artwork_adjustments))
                ArtworkSlider(
                    label = stringResource(R.string.label_artwork_zoom),
                    value = zoom,
                    valueRange = 1f..3f,
                    onValueChange = { zoom = it }
                )
                ArtworkSlider(
                    label = stringResource(R.string.label_artwork_opacity),
                    value = opacity,
                    valueRange = 0f..1f,
                    onValueChange = { opacity = it }
                )
                ArtworkSlider(
                    label = stringResource(R.string.label_artwork_blur),
                    value = blur,
                    valueRange = 0f..24f,
                    onValueChange = { blur = it }
                )
                ArtworkSlider(
                    label = stringResource(R.string.label_artwork_brightness),
                    value = brightness,
                    valueRange = -0.5f..0.5f,
                    onValueChange = { brightness = it }
                )
                ArtworkSlider(
                    label = stringResource(R.string.label_artwork_overlay),
                    value = overlay,
                    valueRange = 0f..1f,
                    onValueChange = { overlay = it }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        draftUri = ""
                        offsetX = ArtworkSettings.Default.offsetXFraction
                        offsetY = ArtworkSettings.Default.offsetYFraction
                        zoom = ArtworkSettings.Default.zoom
                        opacity = ArtworkSettings.Default.opacity
                        blur = ArtworkSettings.Default.blurDp
                        brightness = ArtworkSettings.Default.brightness
                        overlay = ArtworkSettings.Default.overlayStrength
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.action_remove_artwork))
                }
                Button(
                    onClick = {
                        ThemeManager.applyArtwork(draftUri, draftSettings)
                        onApplied()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(stringResource(R.string.action_apply))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onBackClick, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.action_cancel))
            }
            NavigationBarsSpacer()
        }
    }
}

@Composable
private fun ArtworkSlider(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange
        )
    }
}
