package br.com.lucolimac.shesafe.android.framework.service

import androidx.compose.ui.graphics.Paint
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlin.collections.mapOf

class HelpMessageFirebaseService(firestore: FirebaseFirestore, firebaseAuth: FirebaseAuth) :
    HelpMessageService {
    private val userEmail = firebaseAuth.currentUser?.email
    private val messageCollection =
        firestore.collection("personalData").document(userEmail.toString())

    override suspend fun getHelpMessage(): String {
        return try {
            val documentSnapshot = messageCollection.get().await()
            documentSnapshot.getString("message") ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    override suspend fun registerHelpMessage(message: String): Boolean {
        return try {
            messageCollection.set(mapOf(Pair("message", message))).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}