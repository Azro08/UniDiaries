package com.ivkorshak.el_diaries.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ivkorshak.el_diaries.util.firestor_service.FirestoreAddService
import com.ivkorshak.el_diaries.util.firestor_service.FirestoreDeleteService
import com.ivkorshak.el_diaries.util.firestor_service.FirestoreGetDataService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth =
        FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFireStore(): FirebaseFirestore =
        FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirestoreDeleteService(firestore: FirebaseFirestore) : FirestoreDeleteService =
        FirestoreDeleteService(firestore)

    @Provides
    @Singleton
    fun provideFirestoreAddService(firestore: FirebaseFirestore) : FirestoreAddService =
        FirestoreAddService(firestore)

    @Provides
    @Singleton
    fun provideFirestoreGetDataService(firestore: FirebaseFirestore, firebaseAuth: FirebaseAuth) : FirestoreGetDataService =
        FirestoreGetDataService(firestore, firebaseAuth)

}