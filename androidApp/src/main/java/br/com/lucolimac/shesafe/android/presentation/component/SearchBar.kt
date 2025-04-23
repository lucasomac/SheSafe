package br.com.lucolimac.shesafe.android.presentation.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.lucolimac.shesafe.android.presentation.theme.SheSafeTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    modifier: Modifier = Modifier, onSearch: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        value = text,
        onValueChange = { newText ->
            text = newText
            onSearch(newText)
        },
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Digite o nome do local") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search Icon",
                tint = Color.Gray
            )
        },
        shape = RoundedCornerShape(32.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
            focusedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface
        ),
    )
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    SheSafeTheme {
        SearchBar(
            modifier = Modifier
        ) {}
    }
}