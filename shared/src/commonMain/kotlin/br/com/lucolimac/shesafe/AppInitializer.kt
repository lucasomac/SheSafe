package br.com.lucolimac.shesafe

import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider

object AppInitializer {
    fun onApplicationStart() {
        onApplicationStartPlatformSpecific()
        GoogleAuthProvider.create(credentials = GoogleAuthCredentials(serverId = "908009107434-k8no09hf5btvfk8j7738jih0scutc16e.apps.googleusercontent.com"))
    }
}