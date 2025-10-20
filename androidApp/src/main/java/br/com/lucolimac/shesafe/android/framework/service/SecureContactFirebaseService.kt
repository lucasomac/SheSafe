package br.com.lucolimac.shesafe.android.framework.service

import br.com.lucolimac.shesafe.android.data.model.SecureContactModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SecureContactFirebaseService(private val firestore: FirebaseFirestore, private val firebaseAuth: FirebaseAuth) :
    SecureContactService {
    // Remove fixed userEmail and secureContactCollection initialization and resolve collection dynamically
    private fun getSecureContactCollection(): CollectionReference? {
        val userEmail = firebaseAuth.currentUser?.email
        return if (userEmail.isNullOrBlank()) {
            null
        } else {
            firestore.collection("secureContacts").document(userEmail).collection("contacts")
        }
    }

    override suspend fun getSecureContacts(): List<SecureContactModel> {
        val collection = getSecureContactCollection() ?: return emptyList()
        var secureContactList = mutableListOf<SecureContactModel>()
        try {
            val querySnapshot = collection.get().await()
            secureContactList = querySnapshot.documents.mapNotNull {
                it.toObject(SecureContactModel::class.java)
            }.toMutableList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return secureContactList
    }

    override suspend fun getSecureContactByPhoneNumber(phoneNumber: String): SecureContactModel? {
        val collection = getSecureContactCollection() ?: return null
        return try {
            val documentSnapshot =
                collection.whereEqualTo("phoneNumber", phoneNumber).get()
                    .await().documents
            documentSnapshot.firstOrNull()?.toObject(SecureContactModel::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun registerSecureContact(secureContactModel: SecureContactModel): Boolean {
        val collection = getSecureContactCollection() ?: return false
        return try {
            collection.document().set(secureContactModel).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun updateSecureContact(
        phoneNumber: String, secureContactModel: SecureContactModel
    ): Boolean {
        val collection = getSecureContactCollection() ?: return false
        return try {
            val querySnapshot =
                collection.whereEqualTo("phoneNumber", phoneNumber).get().await()

            val document = querySnapshot.documents.firstOrNull()
            if (document != null) {
                document.reference.set(secureContactModel).await()
                true
            } else {
                false // No document found with the given phoneNumber
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun deleteSecureContact(phoneNumber: String): Boolean {
        val collection = getSecureContactCollection() ?: return false
        return try {
            val querySnapshot =
                collection.whereEqualTo("phoneNumber", phoneNumber).get().await()
            val document = querySnapshot.documents.firstOrNull()
            if (document != null) {
                document.reference.delete().await()
                true
            } else {
                false // No document found with the given phoneNumber
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}