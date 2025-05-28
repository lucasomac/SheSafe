package br.com.lucolimac.shesafe.android.presentation.component

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import br.com.lucolimac.shesafe.android.presentation.theme.SheSafeTheme
import br.com.lucolimac.shesafe.route.SheSafeDestination
import com.mmk.kmpauth.firebase.google.GoogleButtonUiContainerFirebase
import com.mmk.kmpauth.uihelper.google.GoogleSignInButton
import dev.gitlive.firebase.auth.FirebaseUser

@Composable
fun SignInArea(navController: NavController, modifier: Modifier = Modifier) {
    val onFirebaseResult: (Result<FirebaseUser?>) -> Unit = {
        // Handle the result of the sign-in process
        it.fold(onSuccess = { user ->
            //Should be navigate to home screen
            // Handle successful sign-in
            Log.d("SignInArea", "User signed in: ${user?.uid}")
            navController.navigate(SheSafeDestination.Home.route.name)

        }, onFailure = { exception ->
            Log.e("SignInArea", "Sign-in failed: $exception")
            // Handle sign-in failure
            // You can show an error message or take appropriate action
//            navController.navigate(NavigationItem.Error.route)
        })
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier
    ) {
        AppLogo()
        Spacer(modifier = Modifier.height(32.dp))
        GoogleButtonUiContainerFirebase(linkAccount = false, onResult = onFirebaseResult) {
            GoogleSignInButton(modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)) { this.onClick() }
        }
    }
}

@Preview
@Composable
fun PreviewSignInArea() {
    SheSafeTheme {
        SignInArea(navController = NavController(LocalContext.current))
    }
}