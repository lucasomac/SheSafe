package br.com.lucolimac.shesafe.android.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import br.com.lucolimac.shesafe.android.presentation.model.DialogModel
import br.com.lucolimac.shesafe.android.presentation.theme.SheSafeTheme

@Composable
fun SheSafeDialog(
    dialogModel: DialogModel, onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = dialogModel.title,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = dialogModel.message,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxWidth()
                )

                if (dialogModel.showCheckbox && dialogModel.checkboxText != null) {
                    var checkedState by remember { mutableStateOf(dialogModel.initialCheckboxValue) }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = checkedState, onCheckedChange = {
                                checkedState = it
                                dialogModel.onCheckboxCheckedChange?.invoke(it)
                            })
                        Text(text = dialogModel.checkboxText)
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DialogButton(
                        dialogModel.dismissButtonText, true, dialogModel.onDismiss
                    )
                    DialogButton(
                        dialogModel.confirmButtonText, false, dialogModel.onConfirm
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SheSafeDialogPreview() {
    SheSafeTheme {
        var showDialog by remember { mutableStateOf(true) }
        if (showDialog) {
            SheSafeDialog(
                dialogModel = DialogModel(
                    title = "Atenção",
                    message = "Você ainda não possui contatos seguros cadastrados. Gostaria de ir para o cadastro?",
                    confirmButtonText = "Cadastrar",
                    dismissButtonText = "Cancelar",
                    onConfirm = {
                        showDialog = false
                    },
                    onDismiss = {
                        showDialog = false
                    })
            ) {
                showDialog = false
            }
        }
    }
}
