package br.com.lucolimac.shesafe.android.presentation.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DialogButton(label: String, isCancelButton: Boolean, onClick: () -> Unit) {
    when (isCancelButton) {
        true -> OutlinedButton(
            onClick = onClick, modifier = Modifier.padding(end = 8.dp)
        ) {
            Text(text = label)
        }

        false -> Button(onClick = onClick) {
            Text(text = label)
        }
    }
}