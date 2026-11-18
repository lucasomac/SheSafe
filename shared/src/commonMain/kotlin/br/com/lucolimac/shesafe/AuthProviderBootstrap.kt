package br.com.lucolimac.shesafe

import br.com.lucolimac.shesafe.core.logging.logWarn
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider

/**
 * Bootstrap helper responsible for one-time initialization of third-party authentication providers.
 */
object AuthProviderBootstrap {
    /**
     * Tracks whether the Google authentication provider has already been initialized in this process.
     */
    private var initialized = false

    /**
     * Initializes [GoogleAuthProvider] idempotently using the configured OAuth web client identifier.
     *
     * Any initialization failure (such as missing platform context early in startup) is caught and
     * logged as a warning so initialization can be retried later before sign-in.
     */
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
