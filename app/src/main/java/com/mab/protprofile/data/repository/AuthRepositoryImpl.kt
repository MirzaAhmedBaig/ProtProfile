package com.mab.protprofile.data.repository

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.mab.protprofile.data.model.Resource
import com.mab.protprofile.data.remote.AuthRemoteDataSource
import com.mab.protprofile.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource
) : AuthRepository {
    override val currentUser: FirebaseUser?
        get() {
            return authRemoteDataSource.currentUser
        }
    override val currentUserIdFlow: Flow<String?> = authRemoteDataSource.currentUserIdFlow

    override suspend fun signIn(email: String, password: String) {
        authRemoteDataSource.signIn(email, password)
    }

    override suspend fun startPhoneNumberVerification(
        phone: String,
        activity: Activity
    ): Flow<Resource<String>> {
        return authRemoteDataSource.startPhoneNumberVerification(phone, activity)
    }

    override suspend fun verifyOtp(
        verificationId: String,
        otp: String
    ): Flow<Resource<FirebaseUser>> {
        return authRemoteDataSource.verifyOtp(verificationId, otp)
    }

    override fun signOut() {
        authRemoteDataSource.signOut()
    }
}