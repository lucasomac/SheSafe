package br.com.lucolimac.shesafe.android.framework.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class SettingsFirebaseService(
    private val firestore: FirebaseFirestore, private val firebaseAuth: FirebaseAuth
) : SettingsService {
    // Resolve the settings document dynamically from the currently authenticated user
    private fun getSettingsDocument() = firebaseAuth.currentUser?.email?.let { email ->
        firestore.collection("settings").document(email)
    }

    override suspend fun getToggleSetting(settingName: String): Boolean {
        val settingsDoc = getSettingsDocument() ?: return false
        return try {
            val documentSnapshot = settingsDoc.get().await()
            val settingValue = documentSnapshot.getBoolean(settingName)
            settingValue == true // Return false if the setting is not found
        } catch (e: Exception) {
            e.printStackTrace()
            false // Return false in case of an error
        }
    }

    override suspend fun setToggleSetting(settingName: String, value: Boolean): Boolean {
        val settingsDoc = getSettingsDocument() ?: return false
        return try {
            // Create a map for the data to be set
            val settingData = mapOf(settingName to value)
            // Use set with merge to create or update the field
            settingsDoc.set(settingData, SetOptions.merge()).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}