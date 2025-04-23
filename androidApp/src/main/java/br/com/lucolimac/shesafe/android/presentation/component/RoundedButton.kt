package br.com.lucolimac.shesafe.android.presentation.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.lucolimac.shesafe.android.presentation.theme.SheSafeTheme

@Composable
fun RoundedButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary) // Replace with your color resource
    ) {
        Text(text = text, color = Color.White, style = TextStyle(fontSize = 16.sp))
    }
}

@Preview(showBackground = true)
@Composable
fun RoundedButtonPreview() {
    SheSafeTheme {
        RoundedButton(
            text = "Clique aqui",
            onClick = { /*TODO*/ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        )
    }
}
