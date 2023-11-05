package com.ivkorshak.el_diaries.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ivkorshak.el_diaries.data.UserService
import com.ivkorshak.el_diaries.util.AuthManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAuthManager(@ApplicationContext context: Context): AuthManager =
        AuthManager(context)

    @Provides
    @Singleton
    fun provideAdminApiRetrofitInstance() : UserService = Retrofit.Builder()
        .baseUrl("http://192.168.100.38:8080")
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .build()
        .create(UserService::class.java)


}