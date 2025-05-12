package br.com.lucolimac.shesafe.android.presentation.component.contact

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.lucolimac.shesafe.android.domain.entity.SecureContact
import br.com.lucolimac.shesafe.android.presentation.theme.SheSafeTheme

@Composable
fun SecureContactCard(
    secureContact: SecureContact,
    onEditClick: (SecureContact) -> Unit,
    onDeleteClick: (SecureContact) -> Unit,
    modifier: Modifier = Modifier
) {

    var expanded by remember { mutableStateOf(false) } //For the dropdown
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = secureContact.name,
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = secureContact.phoneNumber,
                style = TextStyle(fontSize = 14.sp, color = Color.Gray)
            )
        }
        Box {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Filled.MoreVert, contentDescription = "More options")
            }

            DropdownMenu(
                expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(text = {
                    Text("Edit")
                }, onClick = {
                    onEditClick(secureContact)
                    expanded = false //close after click
                })
                DropdownMenuItem(text = {
                    Text("Delete")
                }, onClick = {
                    onDeleteClick(secureContact)
                    expanded = false //close after click
                })
                // Add more menu items as needed
            }
        }
    }
    HorizontalDivider()
}

@Composable
@Preview(showBackground = true)
fun ContactListItemPreview() {
    SheSafeTheme {
        SecureContactCard(SecureContact("John", "123-456-7890"), {}, {})
    }
}