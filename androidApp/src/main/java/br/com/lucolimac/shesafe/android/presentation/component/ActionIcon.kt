package br.com.lucolimac.shesafe.android.presentation.component

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun ActionIcon(
    imageVector: ImageVector? = null,
    painter: Painter? = null,
    description: String,
    onClick: () -> Unit
) {
    val tintColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
    IconButton(onClick = onClick) {
        if (imageVector != null) {
            Icon(imageVector, contentDescription = description, tint = tintColor)
        } else if (painter != null) {
            Icon(painter = painter, contentDescription = description, tint = tintColor)
        }
    }
}