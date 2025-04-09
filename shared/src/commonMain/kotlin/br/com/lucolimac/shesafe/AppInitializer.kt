package br.com.lucolimac.shesafe

import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider

object AppInitializer {
    fun onApplicationStart() {
        GoogleAuthProvider.create(credentials = GoogleAuthCredentials(serverId = "398784472416-rh6o06780p9hjio385jb34vubtqgohmm.apps.googleusercontent.com"))
    }
}