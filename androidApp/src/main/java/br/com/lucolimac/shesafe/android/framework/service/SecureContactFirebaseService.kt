package br.com.lucolimac.shesafe.android.framework.service

import br.com.lucolimac.shesafe.android.data.model.SecureContactModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SecureContactFirebaseService(firestore: FirebaseFirestore, firebaseAuth: FirebaseAuth) :
    SecureContactService {
    private val userEmail = firebaseAuth.currentUser?.email
    private val secureContactCollection =
        firestore.collection("secureContacts").document(userEmail.toString()).collection("contacts")

    override suspend fun getSecureContacts(): List<SecureContactModel> {
        val secureContactList = mutableListOf<SecureContactModel>()
        secureContactCollection.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val secureContact = document.toObject(SecureContactModel::class.java)
                secureContactList.add(secureContact)
            }
        }.addOnFailureListener {
            // Handle the error
        }
        return secureContactList
    }

    override suspend fun getSecureContactByPhone(phone: String): SecureContactModel? {
        var secureContact: SecureContactModel? = SecureContactModel()

        secureContactCollection.document(phone).get().addOnSuccessListener { document ->
                secureContact = document?.toObject(SecureContactModel::class.java)
            }.addOnFailureListener {
                secureContact = null
                it.printStackTrace()
            }
        return secureContact
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