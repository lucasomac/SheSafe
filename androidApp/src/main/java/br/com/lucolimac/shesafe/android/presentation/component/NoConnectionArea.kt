package br.com.lucolimac.shesafe.android.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun NoConnectionArea(onClickListener: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier
    ) {
        Text("Sem conexão com a internet")
        Button(onClick = onClickListener) {
            Text("Tentar Novamente")
        }
    }
}
