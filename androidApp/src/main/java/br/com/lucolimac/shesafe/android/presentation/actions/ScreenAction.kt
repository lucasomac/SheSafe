package br.com.lucolimac.shesafe.android.presentation.actions

import br.com.lucolimac.shesafe.android.presentation.model.DialogModel

class ScreenAction() {
    fun chooseDialogModel(
        hasSecureContacts: Boolean,
        onConfirm: () -> Unit,
        onDismiss: () -> Unit,
        onCheckboxCheckedChange: (Boolean) -> Unit = {}
    ): DialogModel {
        val initialDialogModel = DialogModel(
            title = "Atenção",
            message = "Você ainda não possui contatos seguros cadastrados. Gostaria de ir para o cadastro?",
            confirmButtonText = "Cadastrar",
            dismissButtonText = "Cancelar",
            onConfirm = {
//                showDialog = false
                onConfirm.invoke()
            },
            onDismiss = {
//                showDialog = false
                onDismiss.invoke()
            })

        val helpConfirmationDialogModel = DialogModel(
            title = "Confirmar pedido de ajuda?",
            message = "Ao confirmar seu pedido de ajuda toda a sua lista de contatos seguros receberá um SMS informando seu pedido e sua localização. Deseja enviar?",
            confirmButtonText = "Enviar",
            dismissButtonText = "Cancelar",
            onConfirm = {
                onConfirm.invoke()
//                showHelpConfirmation = false
            },
            onDismiss = {
//                showHelpConfirmation = false
                onDismiss.invoke()
            },
            showCheckbox = true,
            checkboxText = "Não perguntar novamente",
            onCheckboxCheckedChange = { isChecked ->
//                doNotAskAgain = isChecked
                onCheckboxCheckedChange.invoke(isChecked)

            },
            initialCheckboxValue = false
        )

        return if (hasSecureContacts) {
            helpConfirmationDialogModel
        } else {
            initialDialogModel
        }
    }
}