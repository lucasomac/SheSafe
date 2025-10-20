package br.com.lucolimac.shesafe.android.framework.service

import br.com.lucolimac.shesafe.android.data.model.HelpRequestModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class HelpRequestFirebaseService(private val firestore: FirebaseFirestore, private val firebaseAuth: FirebaseAuth) :
    HelpRequestService {

    private fun getHelpRequestCollection(): CollectionReference? {
        val userEmail = firebaseAuth.currentUser?.email
        return if (userEmail.isNullOrBlank()) {
            null
        } else {
            firestore.collection("helpRequests").document(userEmail).collection("helps")
        }
    }

    override suspend fun getOrdersHelp(): List<HelpRequestModel> {
        val collection = getHelpRequestCollection() ?: return emptyList()
        var helpRequestList = mutableListOf<HelpRequestModel>()
        try {
            val querySnapshot = collection.get().await()
            helpRequestList = querySnapshot.documents.mapNotNull { document ->
                document.toObject(HelpRequestModel::class.java)
            }.toMutableList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return helpRequestList
    }

    override suspend fun registerOrderHelp(helpRequestModel: HelpRequestModel): Boolean {
        val collection = getHelpRequestCollection() ?: return false
        return try {
            collection.document().set(helpRequestModel).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}