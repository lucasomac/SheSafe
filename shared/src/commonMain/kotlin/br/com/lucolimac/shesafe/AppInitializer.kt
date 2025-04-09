package br.com.lucolimac.shesafe

import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider

object AppInitializer {
    fun onApplicationStart() {
        GoogleAuthProvider.create(credentials = GoogleAuthCredentials(serverId = "712393037708-sl6oh64v4v7aogq139caheuas1gk282d.apps.googleusercontent.com"))
    }
}