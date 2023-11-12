package com.ivkorshak.el_diaries.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ivkorshak.el_diaries.data.model.FeedBack
import com.ivkorshak.el_diaries.util.Constants
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FeedBackRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    suspend fun sendFeedback(message: String): String {
        return try {
            val uid = firebaseAuth.currentUser?.uid ?: return "User is not authorized"
            val email = firebaseAuth.currentUser?.email ?: return "User is not authorized"
            val date = Constants.getCurrentDate()
            val feedback = FeedBack(Constants.generateRandomId(), uid, email, message, date)
            firestore.collection("feedBacks")
                .add(feedback)
                .await()

            "Done"
        } catch (e: Exception) {
            Log.d("sendFeedback", e.message.toString())
            e.message.toString()
        }
    }

}