package com.mab.protprofile.data.remote

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.mab.protprofile.data.model.Resource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import timber.log.Timber

class AuthRemoteDataSource @Inject constructor(private val auth: FirebaseAuth) {
    val currentUser: FirebaseUser?
        get() {
            Timber.d("Fetching current user")
            return auth.currentUser
        }

    val currentUserIdFlow: Flow<String?>
        get() = callbackFlow {
            Timber.d("Initializing currentUserIdFlow")
            val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                Timber.d("AuthState changed, new user ID: ${firebaseAuth.currentUser?.uid}")
                this.trySend(firebaseAuth.currentUser?.uid)
            }
            auth.addAuthStateListener(listener)
            awaitClose {
                Timber.d("Closing currentUserIdFlow, removing AuthStateListener")
                auth.removeAuthStateListener(listener)
            }
        }

    suspend fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun startPhoneNumberVerification(
        phone: String,
        activity: Activity
    ): Flow<Resource<String>> = callbackFlow {
        trySend(Resource.Loading())
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            task.result?.user
                            trySend(Resource.Success(""))
                            close()
                        } else {
                            trySend(Resource.Error(task.exception ?: Exception("Unknown error")))
                            close()
                        }
                    }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                trySend(Resource.Error(e))
                close(e)
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                trySend(Resource.Success(verificationId))
                close()
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)

        awaitClose {

        }
    }

    suspend fun verifyOtp(
        verificationId: String,
        otp: String
    ): Flow<Resource<FirebaseUser>> = flow {
        emit(Resource.Loading())
        try {
            val credential = PhoneAuthProvider.getCredential(verificationId, otp)
            val result = auth.signInWithCredential(credential).await()
            emit(Resource.Success(result.user!!))
        } catch (e: Exception) {
            emit(Resource.Error(e))
        }
    }

    fun signOut() {
        Timber.d("Signing out current user")
        auth.signOut()
    }
}