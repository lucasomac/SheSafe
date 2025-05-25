package br.com.lucolimac.shesafe.android.framework.service

import br.com.lucolimac.shesafe.android.data.model.OrderHelpModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class OrderHelpFirebaseService(firestore: FirebaseFirestore, firebaseAuth: FirebaseAuth) :
    OrderHelpService {
    private val userEmail = firebaseAuth.currentUser?.email
    private val orderHelpCollection =
        firestore.collection("ordersHelp").document(userEmail.toString()).collection("orders")

    override suspend fun getOrdersHelp(): List<OrderHelpModel> {
        var orderHelpList = mutableListOf<OrderHelpModel>()
        try {
            val querySnapshot = orderHelpCollection.get().await() // Use .await() here
            orderHelpList = querySnapshot.documents.mapNotNull {
                it.toObject(OrderHelpModel::class.java)
            }.toMutableList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return orderHelpList
    }

    override suspend fun registerOrderHelp(orderHelpModel: OrderHelpModel): Boolean {
        return try {
            orderHelpCollection.document().set(orderHelpModel)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}