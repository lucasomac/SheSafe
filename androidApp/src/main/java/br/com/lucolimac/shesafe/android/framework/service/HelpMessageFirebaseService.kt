package br.com.lucolimac.shesafe.android.framework.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlin.collections.mapOf

class HelpMessageFirebaseService(private val firestore: FirebaseFirestore, private val firebaseAuth: FirebaseAuth) :
    HelpMessageService {

    private fun getMessageDocument(): DocumentReference? {
        val userEmail = firebaseAuth.currentUser?.email
        return if (userEmail.isNullOrBlank()) {
            null
        } else {
            firestore.collection("personalData").document(userEmail)
        }
    }

    override suspend fun getHelpMessage(): String {
        val messageDoc = getMessageDocument() ?: return ""
        return try {
            val documentSnapshot = messageDoc.get().await()
            documentSnapshot.getString("message") ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    override suspend fun registerHelpMessage(message: String): Boolean {
        val messageDoc = getMessageDocument() ?: return false
        return try {
            messageDoc.set(mapOf(Pair("message", message))).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}