package br.com.lucolimac.shesafe.android.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.lucolimac.shesafe.R
import br.com.lucolimac.shesafe.android.domain.entity.OrderHelp
import br.com.lucolimac.shesafe.android.presentation.component.ActionIcon
import br.com.lucolimac.shesafe.android.presentation.component.LastSentCard
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    val lastSentList = listOf(
        OrderHelp(
            "Rafael Coelho", LatLng(32.0, 12.0), LocalDateTime.now()
        ), OrderHelp(
            "Lucas Lima", LatLng(32.0, 12.0), LocalDateTime.now()
        ), OrderHelp(
            "Ana Paula", LatLng(32.0, 12.0), LocalDateTime.now()
        ), OrderHelp(
            "Maria Silva", LatLng(32.0, 12.0), LocalDateTime.now()
        )
    )
    Surface(
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
    ) // Background color
    {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)
        ) {
            // Profile Section
            Text(
                text = "PROFILE",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Image(
                painter = rememberAsyncImagePainter(
                    model = "https://randomuser.me/api/portraits/women/75.jpg"
                ),
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
            )

            Text(
                text = "Theresa",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = "Estou em perigo. Por favor se puder me ajudar esta é minha localização!",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )

            //Action Icons
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                ActionIcon(Icons.Default.Settings, description = "Settings") {}
                ActionIcon(Icons.Default.Menu, description = "Menu") {}
                ActionIcon(Icons.Default.Edit, description = "Edit") {}
                ActionIcon(painter = painterResource(R.drawable.logout_24), description =  "Logout") { }
            }

            // Last Sent Section
            Text(
                text = "Últimos envios",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 8.dp)
            )

            LazyColumn {
                items(lastSentList) { item ->
                    LastSentCard(lastSent = item)
                }
            }
        }
    }
}


@Preview
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}