package br.com.lucolimac.shesafe.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import br.com.lucolimac.shesafe.core.designsystem.theme.RoseAction
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import shesafe.shared.generated.resources.Res
import shesafe.shared.generated.resources.cd_app_logo
import shesafe.shared.generated.resources.cd_loading
import shesafe.shared.generated.resources.label_retry
import shesafe.shared.generated.resources.logo

data class DialogModel(
    val title: String,
    val message: String,
    val confirmButtonText: String,
    val dismissButtonText: String,
    val onConfirm: () -> Unit,
    val onDismiss: () -> Unit,
    val showCheckbox: Boolean = false,
    val checkboxText: String? = null,
    val initialCheckboxValue: Boolean = false,
    val onCheckboxCheckedChange: ((Boolean) -> Unit)? = null,
)

@Composable
fun HomeHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        modifier = modifier
            .padding(16.dp)
            .semantics { heading() },
    )
}

@Composable
fun AppLogo(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "S",
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Column {
                Text(
                    text = "he",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = "afe",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
        Image(
            painter = painterResource(Res.drawable.logo),
            contentDescription = stringResource(Res.string.cd_app_logo),
            modifier = Modifier
                .size(200.dp)
                .padding(16.dp),
        )
    }
}

@Composable
fun SheSafeLoading(modifier: Modifier = Modifier) {
    val loadingDesc = stringResource(Res.string.cd_loading)
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.semantics { contentDescription = loadingDesc },
        )
    }
}

@Composable
fun ErrorStateBanner(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onRetry) {
            Text(text = stringResource(Res.string.label_retry))
        }
    }
}

@Composable
fun RoundedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
fun DialogButton(
    text: String,
    isDismiss: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (isDismiss) {
        OutlinedButton(
            onClick = onClick,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = modifier,
        ) {
            Text(text = text, color = MaterialTheme.colorScheme.onSurface)
        }
    } else {
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = RoseAction,
                contentColor = Color.White,
            ),
            modifier = modifier,
        ) {
            Text(text = text)
        }
    }
}

@Composable
fun SheSafeDialog(
    dialogModel: DialogModel,
    onDismissRequest: () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = dialogModel.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                Text(
                    text = dialogModel.message,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxWidth(),
                )

                if (dialogModel.showCheckbox && dialogModel.checkboxText != null) {
                    var checkedState by remember { mutableStateOf(dialogModel.initialCheckboxValue) }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val next = !checkedState
                                checkedState = next
                                dialogModel.onCheckboxCheckedChange?.invoke(next)
                            }
                            .padding(bottom = 12.dp),
                    ) {
                        Checkbox(
                            checked = checkedState,
                            onCheckedChange = {
                                checkedState = it
                                dialogModel.onCheckboxCheckedChange?.invoke(it)
                            },
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = dialogModel.checkboxText,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    DialogButton(
                        text = dialogModel.dismissButtonText,
                        isDismiss = true,
                        onClick = dialogModel.onDismiss,
                    )
                    DialogButton(
                        text = dialogModel.confirmButtonText,
                        isDismiss = false,
                        onClick = dialogModel.onConfirm,
                    )
                }
            }
        }
    }
}

@Composable
fun ActionIcon(
    painter: Painter,
    description: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(48.dp),
    ) {
        Icon(
            painter = painter,
            contentDescription = description,
            tint = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun SettingItem(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
    }
}
