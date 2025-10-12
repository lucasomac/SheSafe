package br.com.lucolimac.shesafe.android.framework.service

import br.com.lucolimac.shesafe.android.data.model.HelpRequestModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class HelpRequestFirebaseService(firestore: FirebaseFirestore, firebaseAuth: FirebaseAuth) :
    HelpRequestService {
    private val userEmail = firebaseAuth.currentUser?.email
    private val helpRequestCollection =
        firestore.collection("helpRequests").document(userEmail.toString()).collection("helps")

    override suspend fun getOrdersHelp(): List<HelpRequestModel> {
        var helpRequestList = mutableListOf<HelpRequestModel>()
        try {
            val querySnapshot = helpRequestCollection.get().await() // Use .await() here
            helpRequestList = querySnapshot.documents.mapNotNull { document ->
                document.toObject(HelpRequestModel::class.java)
            }.toMutableList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return helpRequestList
    }

    override suspend fun registerOrderHelp(helpRequestModel: HelpRequestModel): Boolean {
        return try {
            helpRequestCollection.document().set(helpRequestModel)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}