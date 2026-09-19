package br.com.lucolimac.shesafe.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.lucolimac.shesafe.platform.LocationCoordinates
import br.com.lucolimac.shesafe.platform.LocationResult
import br.com.lucolimac.shesafe.platform.LocationService

data class HomeScreenState(
    val userName: String? = null,
    val contactCount: Int = 0,
    val helpMessage: String = "",
)

@Composable
fun HomeScreen(
    state: HomeScreenState,
    locationService: LocationService,
    onHelpRequested: (LocationCoordinates?) -> Unit,
    onManageContacts: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    var locationResult by remember { mutableStateOf<LocationResult?>(null) }
    LaunchedEffect(locationService) {
        locationService.requestCurrentLocation { locationResult = it }
    }

    MaterialTheme {
        Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Text(
                    text = state.userName?.let { "Olá, $it" } ?: "Olá",
                    style = MaterialTheme.typography.h4,
                )
                Text(
                    text = "Como podemos ajudar você hoje?",
                    style = MaterialTheme.typography.body1,
                )

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Localização", style = MaterialTheme.typography.h6)
                        when (val result = locationResult) {
                            null -> {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    CircularProgressIndicator()
                                    Text("Obtendo sua localização...")
                                }
                            }
                            is LocationResult.Available -> Text(
                                "Localização disponível (${result.coordinates.latitude}, ${result.coordinates.longitude})",
                            )
                            LocationResult.PermissionRequired -> Text(
                                "Permita o acesso à localização para incluir sua posição no pedido de ajuda.",
                            )
                            LocationResult.Unavailable -> Text("Localização indisponível.")
                            is LocationResult.Failed -> Text("Não foi possível obter a localização.")
                        }
                    }
                }

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Contatos de segurança", style = MaterialTheme.typography.h6)
                        Text(
                            text = if (state.contactCount == 0) {
                                "Nenhum contato cadastrado"
                            } else {
                                "${state.contactCount} contato(s) cadastrado(s)"
                            },
                            modifier = Modifier.padding(top = 8.dp),
                        )
                        Button(
                            onClick = onManageContacts,
                            modifier = Modifier.padding(top = 12.dp),
                        ) {
                            Text("Gerenciar contatos")
                        }
                    }
                }

                Button(
                    onClick = {
                        val coordinates = (locationResult as? LocationResult.Available)?.coordinates
                        onHelpRequested(coordinates)
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Pedir ajuda")
                }
            }
        }
    }
}
