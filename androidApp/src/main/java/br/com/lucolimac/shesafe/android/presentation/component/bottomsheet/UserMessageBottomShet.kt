package br.com.lucolimac.shesafe.android.presentation.component.bottomsheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.com.lucolimac.shesafe.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserMessageBottomSheet(
    actualMessage: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    sheetState: SheetState,
) {
    var userMessage by remember { mutableStateOf(actualMessage) }
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.title_input_your_message),
            style = MaterialTheme.typography.headlineSmall // Use Material3 typography
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.description_input_your_message),
            style = MaterialTheme.typography.bodyMedium, // Use Material3 typography
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), //Use ColorScheme
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            userMessage,
            { userMessage = it },
            maxLines = 5,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(onClick = {
                scope.launch { sheetState.hide() }
                onDismiss()
            }) {
                Text(
                    stringResource(R.string.label_cancel),
                    color = MaterialTheme.colorScheme.onSurface
                ) //Color Scheme
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    onConfirm(userMessage)
                    scope.launch { sheetState.hide() }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFC8758F), contentColor = Color.White
                ),
            ) {
                Text(
                    stringResource(R.string.label_change), color = Color.White
                )
            }
        }
    }
}