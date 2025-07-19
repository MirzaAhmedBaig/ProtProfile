package com.mab.protprofile.domain.repository

import android.app.Activity
import com.google.firebase.auth.FirebaseUser
import com.google.rpc.context.AttributeContext
import com.mab.protprofile.data.model.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    val currentUser: FirebaseUser?
    val currentUserIdFlow: Flow<String?>

    suspend fun signIn(email: String, password: String)

    suspend fun startPhoneNumberVerification(phone: String, activity: Activity): Flow<Resource<String>>
    suspend fun verifyOtp(verificationId: String, otp: String): Flow<Resource<FirebaseUser>>

    fun signOut()
}