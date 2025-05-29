package br.com.lucolimac.shesafe.android.framework.service

import br.com.lucolimac.shesafe.android.data.model.SecureContactModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SecureContactFirebaseService(firestore: FirebaseFirestore, firebaseAuth: FirebaseAuth) :
    SecureContactService {
    private val userEmail = firebaseAuth.currentUser?.email
    private val secureContactCollection =
        firestore.collection("secureContacts").document(userEmail.toString()).collection("contacts")

    override suspend fun getSecureContacts(): List<SecureContactModel> {
        var secureContactList = mutableListOf<SecureContactModel>()
        try {
            val querySnapshot = secureContactCollection.get().await() // Use .await() here
            secureContactList = querySnapshot.documents.mapNotNull {
                it.toObject(SecureContactModel::class.java)
            }.toMutableList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return secureContactList
    }

    override suspend fun getSecureContactByPhoneNumber(phoneNumber: String): SecureContactModel? {
        return try {
            val documentSnapshot =
                secureContactCollection.whereEqualTo("phoneNumber", phoneNumber).get()
                    .await().documents
            documentSnapshot.firstOrNull()?.toObject(SecureContactModel::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun registerSecureContact(secureContactModel: SecureContactModel): Boolean {
        return try {
            secureContactCollection.document().set(secureContactModel).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun updateSecureContact(
        phoneNumber: String, secureContactModel: SecureContactModel
    ): Boolean {
        return try {
            val querySnapshot =
                secureContactCollection.whereEqualTo("phoneNumber", phoneNumber).get().await()

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
        return try {
            val querySnapshot =
                secureContactCollection.whereEqualTo("phoneNumber", phoneNumber).get().await()
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