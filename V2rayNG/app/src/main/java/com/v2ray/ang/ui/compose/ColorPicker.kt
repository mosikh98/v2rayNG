package com.v2ray.ang.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.v2ray.ang.R

/** Curated preset swatches spanning the app's ice-blue / gold / crimson / neutral palette. */
val ColorPickerPresets: List<Color> = listOf(
    Color(0xFF4FC3F7), // Ice Blue
    Color(0xFF7DD3FC), // Soft Ice Blue
    Color(0xFF1D7FB7), // Deep Sky Blue
    Color(0xFFE8C158), // Warm Gold
    Color(0xFFA9790B), // Antique Gold
    Color(0xFFE8536A), // Bright Crimson
    Color(0xFF8E1B2E), // Deep Crimson
    Color(0xFF4CAF7D), // Green
    Color(0xFF66BB6A), // Soft Green
    Color(0xFFFFB74D), // Amber
    Color(0xFFB6A6E8), // Lavender
    Color(0xFF9C9C9C), // Gray
)

/**
 * A dialog to pick a theme color: preset swatches plus a manual hex entry field.
 * [initialHex] may be blank to indicate "use default".
 */
@Composable
fun ColorPickerDialog(
    title: String,
    initialHex: String,
    defaultColor: Color,
    onDismiss: () -> Unit,
    onConfirm: (hex: String) -> Unit,
    onResetToDefault: () -> Unit
) {
    var hexInput by remember { mutableStateOf(initialHex.ifBlank { defaultColor.toHexString() }) }
    val previewColor = parseHexColorOrNull(hexInput) ?: defaultColor

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(6),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ColorPickerPresets) { swatch ->
                        val selected = parseHexColorOrNull(hexInput)?.toHexString() == swatch.toHexString()
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(swatch)
                                .border(
                                    width = if (selected) 2.dp else 1.dp,
                                    color = if (selected) MaterialTheme.colorScheme.onSurface
                                    else MaterialTheme.colorScheme.outlineVariant,
                                    shape = CircleShape
                                )
                                .clickable { hexInput = swatch.toHexString() },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selected) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_fab_check),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(previewColor)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.size(width = 10.dp, height = 0.dp))
                    OutlinedTextField(
                        value = hexInput,
                        onValueChange = { hexInput = it },
                        label = { Text("#RRGGBB") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(hexInput) }) {
                Text(stringResource(R.string.action_ok))
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onResetToDefault) {
                    Text(stringResource(R.string.action_reset_default))
                }
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        }
    )
}
