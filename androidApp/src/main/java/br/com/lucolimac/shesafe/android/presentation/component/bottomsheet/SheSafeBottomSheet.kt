import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch
import androidx.compose.ui.text.style.TextAlign
import br.com.lucolimac.shesafe.R
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheSafeBottomSheet(
    contactName: String,
    onConfirmDelete: () -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState // Use SheetState from material3
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.title_delete_secure_contact),
            style = MaterialTheme.typography.headlineSmall // Use Material3 typography
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.message_delete_secure_contact, contactName),
            style = MaterialTheme.typography.bodyMedium, // Use Material3 typography
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), //Use ColorScheme
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(onClick = {
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
                    onConfirmDelete()
                    scope.launch { sheetState.hide() }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFC8758F), contentColor = Color.White
                ), //Optional: change the colors, using ButtonDefaults
            ) {
                Text(stringResource(R.string.label_delete), color = Color.White) // Optional: Use TextButton for consistent style
            }
        }
    }
}
