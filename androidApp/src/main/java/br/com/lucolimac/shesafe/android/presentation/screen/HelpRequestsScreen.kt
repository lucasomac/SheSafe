package br.com.lucolimac.shesafe.android.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import br.com.lucolimac.shesafe.R
import br.com.lucolimac.shesafe.android.presentation.component.LastSentCard
import br.com.lucolimac.shesafe.android.presentation.viewModel.HelpRequestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpRequestsScreen(helpRequestViewModel: HelpRequestViewModel) {

    val helpRequestList by helpRequestViewModel.helpRequests.collectAsState()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        when {
            helpRequestList.isEmpty() -> {
                Text(
                    text = stringResource(R.string.message_nothing_send_yet),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                )
            }

            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(helpRequestList.size) { item ->
                        LastSentCard(lastSent = helpRequestList[item])
                    }
                }
            }
        }
    }

}
