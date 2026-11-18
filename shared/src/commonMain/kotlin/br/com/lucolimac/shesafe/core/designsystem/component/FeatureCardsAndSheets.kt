package br.com.lucolimac.shesafe.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import br.com.lucolimac.shesafe.core.designsystem.theme.RoseAction
import br.com.lucolimac.shesafe.feature.contacts.domain.model.SecureContact
import br.com.lucolimac.shesafe.feature.helprequests.domain.model.HelpRequest
import br.com.lucolimac.shesafe.feature.profile.domain.model.SettingKey
import br.com.lucolimac.shesafe.feature.profile.domain.model.UserSettings
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import shesafe.shared.generated.resources.Res
import shesafe.shared.generated.resources.cd_more_options
import shesafe.shared.generated.resources.default_message_label
import shesafe.shared.generated.resources.description_input_your_message
import shesafe.shared.generated.resources.label_cancel
import shesafe.shared.generated.resources.label_change
import shesafe.shared.generated.resources.label_close
import shesafe.shared.generated.resources.label_delete
import shesafe.shared.generated.resources.label_edit
import shesafe.shared.generated.resources.label_setting_send_confirmation
import shesafe.shared.generated.resources.message_delete_secure_contact
import shesafe.shared.generated.resources.message_location_unavailable
import shesafe.shared.generated.resources.more_vert_24px
import shesafe.shared.generated.resources.title_delete_secure_contact
import shesafe.shared.generated.resources.title_input_your_message
import shesafe.shared.generated.resources.title_settings
import kotlin.time.Instant

/**
 * Displays a list item representing a trusted [SecureContact] with an overflow menu for edit and delete actions.
 *
 * @param secureContact Trusted contact domain model whose name and formatted phone number are shown.
 * @param onEditClick Callback invoked with [secureContact] when the user selects the edit menu option.
 * @param onDeleteClick Callback invoked with [secureContact] when the user selects the delete menu option.
 * @param modifier Optional [Modifier] applied to the outer column container.
 */
@Composable
fun SecureContactCard(
    secureContact: SecureContact,
    onEditClick: (SecureContact) -> Unit,
    onDeleteClick: (SecureContact) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .semantics(mergeDescendants = true) {},
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = secureContact.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                )
                Text(
                    text = formatPhoneNumber(secureContact.phoneNumber),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        painter = painterResource(Res.drawable.more_vert_24px),
                        contentDescription = stringResource(Res.string.cd_more_options),
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.label_edit)) },
                        onClick = {
                            expanded = false
                            onEditClick(secureContact)
                        },
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.label_delete)) },
                        onClick = {
                            expanded = false
                            onDeleteClick(secureContact)
                        },
                    )
                }
            }
        }
        HorizontalDivider()
    }
}

/** Expected digit count for a Brazilian mobile number including a two-digit area code (DDD). */
private const val BR_MOBILE_LEN = 11

/** Expected digit count for a Brazilian landline number including a two-digit area code (DDD). */
private const val BR_LANDLINE_LEN = 10

/** Minimum digit count required to format a local phone number without an area code. */
private const val MIN_LOCAL_LEN = 7

/** Length of the Brazilian area code (DDD) or country code prefix (`55`). */
private const val AREA_CODE_LEN = 2

/** Exclusive end index of the 5-digit subscriber prefix in an 11-digit Brazilian mobile number. */
private const val MOBILE_PREFIX_END = 7

/** Exclusive end index of the 4-digit subscriber prefix in a 10-digit Brazilian landline number. */
private const val LANDLINE_PREFIX_END = 6

/** Length of the trailing subscriber suffix separated by a hyphen. */
private const val SUFFIX_LEN = 4

/**
 * Formats a raw phone number string into standard Brazilian mobile, landline, or local display notation.
 *
 * Strips non-digit characters and an optional leading `55` country code before grouping digits.
 * Returns [raw] unchanged when fewer than [MIN_LOCAL_LEN] digits are present.
 *
 * @param raw Unformatted or partially formatted phone number string.
 * @return Human-readable formatted phone number string, or [raw] if formatting does not apply.
 */
fun formatPhoneNumber(raw: String): String {
    val digits = raw.filter(Char::isDigit)
    if (digits.isEmpty()) return raw
    val clean = if (digits.length > BR_MOBILE_LEN && digits.startsWith("55")) {
        digits.substring(AREA_CODE_LEN)
    } else {
        digits
    }
    return when {
        clean.length >= BR_MOBILE_LEN -> {
            val ddd = clean.take(AREA_CODE_LEN)
            val pre = clean.substring(AREA_CODE_LEN, MOBILE_PREFIX_END)
            val suf = clean.substring(MOBILE_PREFIX_END, BR_MOBILE_LEN)
            "($ddd) $pre-$suf"
        }
        clean.length == BR_LANDLINE_LEN -> {
            val ddd = clean.take(AREA_CODE_LEN)
            val pre = clean.substring(AREA_CODE_LEN, LANDLINE_PREFIX_END)
            val suf = clean.substring(LANDLINE_PREFIX_END, BR_LANDLINE_LEN)
            "($ddd) $pre-$suf"
        }
        clean.length >= MIN_LOCAL_LEN -> {
            val split = clean.length - SUFFIX_LEN
            "${clean.take(split)}-${clean.substring(split)}"
        }
        else -> raw
    }
}

/**
 * Formats an [Instant] in the device's current system time zone using the `dd/MM/yyyy HH:mm:ss` pt-BR pattern.
 *
 * @param instant Point in time to format.
 * @return Formatted date-time string in `dd/MM/yyyy HH:mm:ss` notation.
 */
fun formatInstantPtBr(instant: Instant): String {
    val dt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    fun Int.pad2() = toString().padStart(2, '0')
    return "${dt.day.pad2()}/${dt.month.number.pad2()}/${dt.year} " +
        "${dt.hour.pad2()}:${dt.minute.pad2()}:${dt.second.pad2()}"
}

/**
 * Displays a summary card for a previously dispatched [HelpRequest], including recipients, map link, and timestamp.
 *
 * @param lastSent Dispatched help request record to display.
 * @param modifier Optional [Modifier] applied to the [Card].
 */
@Composable
fun LastSentCard(
    lastSent: HelpRequest,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    val mapLink = lastSent.mapLink
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text(
                    text = lastSent.phoneNumbers.joinToString(", ") { formatPhoneNumber(it) },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (mapLink != null) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = MaterialTheme.colorScheme.primary,
                                    textDecoration = TextDecoration.Underline,
                                ),
                            ) {
                                append(mapLink)
                            }
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clickable { uriHandler.openUri(mapLink) },
                    )
                } else {
                    Text(
                        text = stringResource(Res.string.message_location_unavailable),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
            Text(
                text = formatInstantPtBr(lastSent.createdAt),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

/**
 * Bottom sheet content asking the user to confirm deletion of a trusted contact.
 *
 * Hides [sheetState] before invoking either [onDismiss] or [onConfirmDelete].
 *
 * @param contactName Display name of the contact interpolated into the confirmation prompt.
 * @param sheetState Material 3 [SheetState] controlling the enclosing modal bottom sheet visibility.
 * @param onConfirmDelete Callback invoked after the sheet finishes hiding when deletion is confirmed.
 * @param onDismiss Callback invoked after the sheet finishes hiding when deletion is cancelled.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmDeleteBottomSheet(
    contactName: String,
    sheetState: SheetState,
    onConfirmDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.title_delete_secure_contact),
            style = MaterialTheme.typography.headlineSmall,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.message_delete_secure_contact, contactName),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            OutlinedButton(
                onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                },
            ) {
                Text(
                    text = stringResource(Res.string.label_cancel),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion { onConfirmDelete() }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = RoseAction,
                    contentColor = Color.White,
                ),
            ) {
                Text(
                    text = stringResource(Res.string.label_delete),
                    color = Color.White,
                )
            }
        }
    }
}

/**
 * Bottom sheet content allowing the user to customize the default emergency SOS message text.
 *
 * @param actualMessage Currently saved emergency message pre-populated into the text field.
 * @param sheetState Material 3 [SheetState] controlling the enclosing modal bottom sheet visibility.
 * @param onDismiss Callback invoked after the sheet hides when the user cancels editing.
 * @param onConfirm Callback invoked with the updated non-blank message after the sheet hides on confirmation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserMessageBottomSheet(
    actualMessage: String,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var userMessage by remember(actualMessage) { mutableStateOf(actualMessage) }
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.title_input_your_message),
            style = MaterialTheme.typography.headlineSmall,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(Res.string.description_input_your_message),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = userMessage,
            onValueChange = { userMessage = it },
            label = { Text(stringResource(Res.string.default_message_label)) },
            maxLines = 5,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            OutlinedButton(
                onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                },
            ) {
                Text(
                    text = stringResource(Res.string.label_cancel),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion { onConfirm(userMessage) }
                },
                enabled = userMessage.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RoseAction,
                    contentColor = Color.White,
                ),
            ) {
                Text(
                    text = stringResource(Res.string.label_change),
                    color = Color.White,
                )
            }
        }
    }
}

/**
 * Bottom sheet content presenting toggleable user preferences such as emergency send confirmation.
 *
 * @param settings Current [UserSettings] snapshot backing the displayed switches.
 * @param sheetState Material 3 [SheetState] controlling the enclosing modal bottom sheet visibility.
 * @param onToggleSetting Callback invoked with the modified [SettingKey] and new boolean value on toggle.
 * @param onDismiss Callback invoked after the sheet hides when the user taps the close button.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    settings: UserSettings,
    sheetState: SheetState,
    onToggleSetting: (SettingKey, Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier.padding(16.dp),
    ) {
        Text(
            text = stringResource(Res.string.title_settings),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp),
        )
        SettingItem(
            text = stringResource(Res.string.label_setting_send_confirmation),
            checked = settings.isEnabled(SettingKey.SEND_CONFIRMATION),
            onCheckedChange = { enabled ->
                onToggleSetting(SettingKey.SEND_CONFIRMATION, enabled)
            },
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        ) {
            Text(text = stringResource(Res.string.label_close))
        }
    }
}
