package br.com.lucolimac.shesafe.android.framework.util

import br.com.lucolimac.shesafe.android.FirebaseProvider

object FireStoreWrapper {
    suspend inline fun <reified A : Any> loadDocuments(collectionPath: String): List<A?> {
        val firestore = FirebaseProvider.firestore
        return firestore.collection(collectionPath).get().result.documents.map {
            it.toObject(A::class.java)
        }
    }
}