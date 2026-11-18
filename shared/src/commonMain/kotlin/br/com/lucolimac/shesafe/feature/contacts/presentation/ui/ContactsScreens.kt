package br.com.lucolimac.shesafe.feature.contacts.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.lucolimac.shesafe.core.designsystem.component.ConfirmDeleteBottomSheet
import br.com.lucolimac.shesafe.core.designsystem.component.ErrorStateBanner
import br.com.lucolimac.shesafe.core.designsystem.component.HomeHeader
import br.com.lucolimac.shesafe.core.designsystem.component.SecureContactCard
import br.com.lucolimac.shesafe.core.designsystem.component.SheSafeLoading
import br.com.lucolimac.shesafe.feature.contacts.domain.model.SecureContact
import br.com.lucolimac.shesafe.feature.contacts.presentation.viewmodel.RegisterContactEvent
import br.com.lucolimac.shesafe.feature.contacts.presentation.viewmodel.RegisterContactUiState
import br.com.lucolimac.shesafe.feature.contacts.presentation.viewmodel.RegisterSecureContactViewModel
import br.com.lucolimac.shesafe.feature.contacts.presentation.viewmodel.SecureContactsEvent
import br.com.lucolimac.shesafe.feature.contacts.presentation.viewmodel.SecureContactsUiState
import br.com.lucolimac.shesafe.feature.contacts.presentation.viewmodel.SecureContactsViewModel
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import shesafe.shared.generated.resources.Res
import shesafe.shared.generated.resources.label_secure_contact_name
import shesafe.shared.generated.resources.label_secure_contact_phone
import shesafe.shared.generated.resources.label_secure_contact_phone_prefix
import shesafe.shared.generated.resources.label_secure_contact_save
import shesafe.shared.generated.resources.label_secure_contact_update
import shesafe.shared.generated.resources.message_no_secure_contacts
import shesafe.shared.generated.resources.title_secure_contacts

/**
 * Stateful screen composable displaying the user's trusted emergency contacts list and handling
 * edit navigation and deletion confirmation events from [SecureContactsViewModel].
 *
 * @param onNavigateToEditContact Callback invoked with the contact's phone number to open edit mode.
 * @param onShowMessage Callback invoked to display a localized feedback message to the user.
 * @param modifier Optional [Modifier] applied to the root layout.
 * @param viewModel [SecureContactsViewModel] managing contacts list state and events.
 */
@Composable
fun SecureContactsScreen(
    onNavigateToEditContact: (String) -> Unit,
    onShowMessage: (StringResource) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SecureContactsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is SecureContactsEvent.NavigateToEdit -> onNavigateToEditContact(event.phoneNumber)
                is SecureContactsEvent.ShowMessage -> onShowMessage(event.messageRes)
            }
        }
    }

    SecureContactsContent(
        uiState = uiState,
        onEditClick = viewModel::onEditContactClicked,
        onDeleteClick = viewModel::onRequestDeleteContact,
        onConfirmDelete = viewModel::onConfirmDeleteContact,
        onDismissDelete = viewModel::onDismissDeleteSheet,
        onRetry = viewModel::refresh,
        modifier = modifier,
    )
}

/**
 * Stateless presentation composable rendering loading, error, empty, or populated states of the
 * trusted contacts list along with an optional modal confirmation sheet for deletions.
 *
 * @param uiState Current [SecureContactsUiState] driving the UI.
 * @param onEditClick Callback invoked when the edit action on a [SecureContact] card is clicked.
 * @param onDeleteClick Callback invoked when the delete action on a [SecureContact] card is clicked.
 * @param onConfirmDelete Callback invoked when the user confirms deletion in the bottom sheet.
 * @param onDismissDelete Callback invoked when the delete confirmation bottom sheet is dismissed.
 * @param onRetry Callback invoked when the user taps retry on the error banner.
 * @param modifier Optional [Modifier] applied to the root [Column].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecureContactsContent(
    uiState: SecureContactsUiState,
    onEditClick: (SecureContact) -> Unit,
    onDeleteClick: (SecureContact) -> Unit,
    onConfirmDelete: () -> Unit,
    onDismissDelete: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState()

    Column(modifier = modifier.fillMaxSize()) {
        HomeHeader(
            title = stringResource(Res.string.title_secure_contacts),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        when {
            uiState.isLoading -> {
                SheSafeLoading()
            }
            uiState.errorResource != null && uiState.contacts.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    ErrorStateBanner(
                        message = stringResource(uiState.errorResource),
                        onRetry = onRetry,
                    )
                }
            }
            uiState.contacts.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(Res.string.message_no_secure_contacts),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp),
                ) {
                    items(
                        items = uiState.contacts,
                        key = { it.phoneNumber },
                    ) { contact ->
                        SecureContactCard(
                            secureContact = contact,
                            onEditClick = onEditClick,
                            onDeleteClick = onDeleteClick,
                        )
                    }
                }
            }
        }
    }

    val pendingDelete = uiState.contactPendingDelete
    if (pendingDelete != null) {
        ModalBottomSheet(
            onDismissRequest = onDismissDelete,
            sheetState = sheetState,
        ) {
            ConfirmDeleteBottomSheet(
                contactName = pendingDelete.name,
                sheetState = sheetState,
                onConfirmDelete = onConfirmDelete,
                onDismiss = onDismissDelete,
            )
        }
    }
}

/**
 * Stateful screen composable for registering a new trusted emergency contact or editing an
 * existing one identified by [phoneNumberArg].
 *
 * @param phoneNumberArg Optional phone number of an existing contact to edit; `null` for creation mode.
 * @param onNavigateBack Callback invoked to pop back to the previous destination.
 * @param onShowMessage Callback invoked to display a localized feedback message to the user.
 * @param modifier Optional [Modifier] applied to the root layout.
 * @param viewModel [RegisterSecureContactViewModel] managing form state, validation, and save actions.
 */
@Composable
fun RegisterSecureContactScreen(
    phoneNumberArg: String?,
    onNavigateBack: () -> Unit,
    onShowMessage: (StringResource) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegisterSecureContactViewModel = koinViewModel(),
) {
    LaunchedEffect(phoneNumberArg) {
        viewModel.initialize(phoneNumberArg)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                RegisterContactEvent.NavigateBack -> onNavigateBack()
                is RegisterContactEvent.ShowMessage -> onShowMessage(event.messageRes)
            }
        }
    }

    RegisterSecureContactContent(
        uiState = uiState,
        onNameChanged = viewModel::onNameChanged,
        onPhoneChanged = viewModel::onPhoneChanged,
        onSaveClicked = viewModel::onSaveClicked,
        modifier = modifier,
    )
}

/**
 * Stateless form composable for entering or updating a trusted contact's name and phone number,
 * complete with inline validation errors, accessibility error semantics, and keyboard focus chaining.
 *
 * @param uiState Current [RegisterContactUiState] representing field values and validation state.
 * @param onNameChanged Callback invoked when the contact name input changes.
 * @param onPhoneChanged Callback invoked when the phone number input changes.
 * @param onSaveClicked Callback invoked when the user submits the form.
 * @param modifier Optional [Modifier] applied to the root container.
 */
@Composable
fun RegisterSecureContactContent(
    uiState: RegisterContactUiState,
    onNameChanged: (String) -> Unit,
    onPhoneChanged: (String) -> Unit,
    onSaveClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (uiState.isLoading) {
        SheSafeLoading(modifier = modifier)
        return
    }

    val phoneFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val nameErrorText = uiState.nameErrorRes?.let { stringResource(it) }
    val phoneErrorText = uiState.phoneErrorRes?.let { stringResource(it) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedTextField(
            value = uiState.name,
            onValueChange = onNameChanged,
            label = { Text(stringResource(Res.string.label_secure_contact_name)) },
            isError = nameErrorText != null,
            supportingText = nameErrorText?.let {
                { Text(text = it, color = MaterialTheme.colorScheme.error) }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(
                onNext = { phoneFocusRequester.requestFocus() },
            ),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    if (nameErrorText != null) error(nameErrorText)
                },
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            prefix = { Text(stringResource(Res.string.label_secure_contact_phone_prefix)) },
            value = uiState.phoneNumber,
            onValueChange = onPhoneChanged,
            label = { Text(stringResource(Res.string.label_secure_contact_phone)) },
            isError = phoneErrorText != null,
            supportingText = phoneErrorText?.let {
                { Text(text = it, color = MaterialTheme.colorScheme.error) }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    if (uiState.isFormValid) {
                        onSaveClicked()
                    }
                },
            ),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(phoneFocusRequester)
                .semantics {
                    if (phoneErrorText != null) error(phoneErrorText)
                },
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                focusManager.clearFocus()
                onSaveClicked()
            },
            enabled = uiState.isFormValid,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(
                    if (uiState.isEditMode) {
                        Res.string.label_secure_contact_update
                    } else {
                        Res.string.label_secure_contact_save
                    },
                ),
            )
        }
    }
}

/**
 * Light-theme Compose preview for [SecureContactsContent] populated with sample contacts.
 */
@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
private fun SecureContactsLightPreview() {
    br.com.lucolimac.shesafe.core.designsystem.theme.SheSafeTheme(darkTheme = false) {
        SecureContactsContent(
            uiState = SecureContactsUiState(
                isLoading = false,
                contacts = listOf(
                    SecureContact(name = "Maria Silva", phoneNumber = "11999998888"),
                    SecureContact(name = "Ana Costa", phoneNumber = "21988887777"),
                ),
            ),
            onEditClick = {},
            onDeleteClick = {},
            onConfirmDelete = {},
            onDismissDelete = {},
            onRetry = {},
        )
    }
}

/**
 * Dark-theme Compose preview for [RegisterSecureContactContent] with valid pre-filled form values.
 */
@org.jetbrains.compose.ui.tooling.preview.Preview
@Composable
private fun RegisterSecureContactDarkPreview() {
    br.com.lucolimac.shesafe.core.designsystem.theme.SheSafeTheme(darkTheme = true) {
        RegisterSecureContactContent(
            uiState = RegisterContactUiState(
                name = "Maria Silva",
                phoneNumber = "11999998888",
            ),
            onNameChanged = {},
            onPhoneChanged = {},
            onSaveClicked = {},
        )
    }
}
