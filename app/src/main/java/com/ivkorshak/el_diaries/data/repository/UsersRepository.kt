package com.ivkorshak.el_diaries.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ivkorshak.el_diaries.data.api.UserService
import com.ivkorshak.el_diaries.data.model.Users
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UsersRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val api : UserService
) {

    private val usersCollection = firestore.collection("users")
    suspend fun getUsers(): List<Users> {
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

    suspend fun deleteUser(userId: String) : String{
        val userDocument = usersCollection.document(userId)
        return try {
            userDocument.delete().await()
            "Done"
        } catch (e: Exception) {
            e.message.toString()
        }
    }
    suspend fun deleteAccount(uid : String) = api.deleteUser(uid)

    suspend fun saveUser(user: Users): String {
        return try {
            // Firestore-specific logic here
            val userDocRef = usersCollection.document(user.id)
            userDocRef.set(user).await()
            "Done"
        } catch (e: Exception) {
            e.message.toString()
        }
    }

    suspend fun getUserRole(): String? {
        val uid = firebaseAuth.currentUser?.uid
        if (uid != null) {
            return try {
                val userDocument = usersCollection.document(uid)
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
