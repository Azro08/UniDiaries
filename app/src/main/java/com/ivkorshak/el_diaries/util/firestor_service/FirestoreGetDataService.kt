package com.ivkorshak.el_diaries.util.firestor_service

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ivkorshak.el_diaries.data.Users
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreGetDataService @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {
    suspend fun getUsers(): List<Users> {
        val usersCollection = firestore.collection("users")
        try {
            val querySnapshot = usersCollection.get().await()
            val usersList = mutableListOf<Users>()
            for (document in querySnapshot) {
                val user = document.toObject(Users::class.java)
                if (user.role != "admin") {
                    usersList.add(user)
                }
            }
            return usersList
        } catch (e: Exception) {
            // Handle any errors or exceptions here
            throw e
        }
    }
    suspend fun getUserRole(): String? {
        val uid = firebaseAuth.currentUser?.uid
        if (uid != null) {
            return try {
                val userDocument = firestore.collection("users").document(uid)
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