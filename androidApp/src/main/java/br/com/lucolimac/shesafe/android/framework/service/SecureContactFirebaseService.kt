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

    override suspend fun getSecureContactByPhone(phone: String): SecureContactModel? {
        return try {
            val documentSnapshot = secureContactCollection.document(phone).get().await()
            documentSnapshot.toObject(SecureContactModel::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null // Return null if there's an error
        }
    }

    override suspend fun registerSecureContact(secureContactModel: SecureContactModel): Boolean {
        return try {
            secureContactCollection.document(secureContactModel.phone).set(secureContactModel)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun updateSecureContact(
        phone: String, secureContactModel: SecureContactModel
    ): Boolean {
        return try {
            secureContactCollection.document(phone).set(secureContactModel)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun deleteSecureContact(phone: String): Boolean {
        return try {
            secureContactCollection.document(phone).delete()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}