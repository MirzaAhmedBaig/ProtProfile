package com.mab.protprofile.ui

import com.mab.protprofile.data.model.ErrorMessage
import com.mab.protprofile.domain.repository.AuthRepository
import com.mab.protprofile.ui.data.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import timber.log.Timber

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepository: AuthRepository) :
    MainViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    fun checkAuthState(showErrorSnackbar: (ErrorMessage) -> Unit) {
        Timber.d("Checking auth state")
        checkCurrentUser(showErrorSnackbar)
    }

    private fun checkCurrentUser(showErrorSnackbar: (ErrorMessage) -> Unit) {
        launchCatching(showErrorSnackbar) {
            Timber.d("Checking current user")
            val user = authRepository.currentUser
            _authState.value =
                if (user != null) AuthState.Authenticated(user) else AuthState.Unauthenticated
        }
    }
}

