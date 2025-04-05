package br.com.lucolimac.shesafe.android.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.lucolimac.shesafe.android.presentation.theme.SheSafeTheme
import com.mmk.kmpauth.firebase.apple.AppleButtonUiContainer
import com.mmk.kmpauth.firebase.google.GoogleButtonUiContainerFirebase
import com.mmk.kmpauth.uihelper.apple.AppleSignInButton
import com.mmk.kmpauth.uihelper.google.GoogleSignInButton
import dev.gitlive.firebase.auth.FirebaseUser

@Composable
fun SignInArea(modifier: Modifier = Modifier) {
    val onFirebaseResult: (Result<FirebaseUser?>) -> Unit = {}
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier
    ) {
        AppLogo()
        Spacer(modifier = Modifier.height(32.dp))
        GoogleButtonUiContainerFirebase(linkAccount = true, onResult = onFirebaseResult) {
            GoogleSignInButton(modifier = Modifier.fillMaxWidth()) { this.onClick() }
        }
        Spacer(modifier = Modifier.height(16.dp))
        AppleButtonUiContainer(linkAccount = true, onResult = onFirebaseResult) {
            AppleSignInButton(modifier = Modifier.fillMaxWidth()) { this.onClick() }
        }
    }
}

@Preview
@Composable
fun PreviewSignInArea() {
    SheSafeTheme {
        SignInArea()
    }
}