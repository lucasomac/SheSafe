package br.com.lucolimac.shesafe.feature.auth.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.lucolimac.shesafe.AuthProviderBootstrap
import br.com.lucolimac.shesafe.core.designsystem.component.AppLogo
import br.com.lucolimac.shesafe.core.designsystem.theme.SheSafeTheme
import br.com.lucolimac.shesafe.feature.auth.presentation.viewmodel.AuthEvent
import br.com.lucolimac.shesafe.feature.auth.presentation.viewmodel.AuthUiState
import br.com.lucolimac.shesafe.feature.auth.presentation.viewmodel.AuthViewModel
import com.mmk.kmpauth.google.rememberGoogleAuthState
import com.mmk.kmpauth.uihelper.google.GoogleSignInButton
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onShowMessage: (StringResource) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = koinViewModel(),
) {
    LaunchedEffect(Unit) {
        AuthProviderBootstrap.initializeGoogleAuth()
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val googleSignIn = rememberGoogleAuthState(
        linkAccount = false,
        onResult = { result ->
            result.fold(
                onSuccess = {
                    viewModel.onExternalAuthSuccess()
                },
                onFailure = { error ->
                    viewModel.onExternalAuthFailure(error.message)
                },
            )
        },
    )

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                AuthEvent.NavigateToHome -> onNavigateToHome()
                is AuthEvent.ShowMessage -> onShowMessage(event.messageRes)
            }
        }
    }

    LoginContent(
        uiState = uiState,
        onSignInClick = { googleSignIn.launch() },
        modifier = modifier,
    )
}

@Composable
fun LoginContent(
    uiState: AuthUiState,
    onSignInClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
    ) {
        AppLogo()
        Spacer(modifier = Modifier.height(32.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            GoogleSignInButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                onClick = onSignInClick,
            )
        }

        uiState.errorResource?.let { errorRes ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(errorRes),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview
@Composable
private fun LoginContentLightPreview() {
    SheSafeTheme(darkTheme = false) {
        LoginContent(
            uiState = AuthUiState(),
            onSignInClick = {},
        )
    }
}

@Preview
@Composable
private fun LoginContentDarkPreview() {
    SheSafeTheme(darkTheme = true) {
        LoginContent(
            uiState = AuthUiState(isLoading = true),
            onSignInClick = {},
        )
    }
}
