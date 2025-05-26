package br.com.lucolimac.shesafe.android.framework.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SettingsFirebaseService(
    firestore: FirebaseFirestore, firebaseAuth: FirebaseAuth
) : SettingsService {
    private val userEmail = firebaseAuth.currentUser?.email
    private val settingsCollection = firestore.collection("settings").document(userEmail.toString())
    override suspend fun getToggleSetting(settingName: String): Boolean {
        return try {
            val documentSnapshot = settingsCollection.get().await()
            val settingValue = documentSnapshot.getBoolean(settingName)
            settingValue == true // Return false if the setting is not found
        } catch (e: Exception) {
            e.printStackTrace()
            false // Return false in case of an error
        }
    }

    override suspend fun setToggleSetting(settingName: String, value: Boolean): Boolean {
        return try {
            settingsCollection.update(settingName, value)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}