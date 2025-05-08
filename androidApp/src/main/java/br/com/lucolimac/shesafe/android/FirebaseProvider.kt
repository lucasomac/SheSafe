package br.com.lucolimac.shesafe.android

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.initialize

object FirebaseProvider {
    fun initialize(context: Context) {
        Firebase.initialize(context = context)
    }

    val firestore: FirebaseFirestore
        get() = Firebase.firestore
    val auth: FirebaseAuth
        get() = Firebase.auth
}
