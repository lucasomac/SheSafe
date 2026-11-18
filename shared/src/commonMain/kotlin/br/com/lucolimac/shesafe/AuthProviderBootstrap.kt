package br.com.lucolimac.shesafe

import br.com.lucolimac.shesafe.core.logging.logWarn
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider

object AuthProviderBootstrap {
    private var initialized = false

    fun initializeGoogleAuth() {
        if (initialized) return
        runCatching {
            GoogleAuthProvider.create(
                credentials = GoogleAuthCredentials(
                    serverId = BuildKonfig.GOOGLE_WEB_CLIENT_ID,
                ),
            )
            initialized = true
        }.onFailure { error ->
            logWarn("AuthProviderBootstrap", "GoogleAuthProvider init deferred: ${error.message}")
        }
    }
}
