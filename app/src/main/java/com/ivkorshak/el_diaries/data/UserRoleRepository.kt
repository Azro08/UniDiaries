package com.ivkorshak.el_diaries.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRoleRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) {

    suspend fun getUserRole(): String? {
        val uid = firebaseAuth.currentUser?.uid
        Log.d("getUserRole", uid.toString())
        if (uid != null) {
            return try {
                val userDocument = firebaseFirestore.collection("users").document(uid)
                val documentSnapshot = userDocument.get().await()
                if (documentSnapshot.exists()) {
                    val userData = documentSnapshot.toObject(Users::class.java)
                    Log.d("getUserRole", userData.toString())
                    userData?.role
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
        // Return null if the user is not logged in or any error occurs
        return null
    }
}
