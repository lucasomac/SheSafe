package br.com.lucolimac.shesafe.android.framework.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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
            // Create a map for the data to be set
            val settingData = mapOf(settingName to value)
            // Use set with merge to create or update the field
            settingsCollection.set(settingData, SetOptions.merge()).await()
            true
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}