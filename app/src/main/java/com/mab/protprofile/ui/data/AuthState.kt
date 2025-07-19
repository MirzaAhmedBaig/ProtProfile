package com.mab.protprofile.ui.data

import com.google.firebase.auth.FirebaseUser

sealed class AuthState {
    object Loading : AuthState()

    data class Authenticated(
        val user: FirebaseUser,
    ) : AuthState()

    object Unauthenticated : AuthState()
}

sealed class AuthStateNew {
    object Idle : AuthStateNew()

    object Loading : AuthStateNew()

    data class CodeSent(
        val verificationId: String,
    ) : AuthStateNew()

    data class Verified(
        val user: FirebaseUser,
    ) : AuthStateNew()

    data class Error(
        val message: String,
    ) : AuthStateNew()
}
