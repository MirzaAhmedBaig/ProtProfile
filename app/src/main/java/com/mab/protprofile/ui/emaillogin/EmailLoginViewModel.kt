package com.mab.protprofile.ui.emaillogin

import com.mab.protprofile.data.model.ErrorMessage
import com.mab.protprofile.domain.repository.AuthRepository
import com.mab.protprofile.ui.MainViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EmailLoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : MainViewModel() {

    private val _shouldRestartApp = MutableStateFlow(false)
    val shouldRestartApp: StateFlow<Boolean>
        get() = _shouldRestartApp.asStateFlow()

    init {
        Timber.d("EmailLoginViewModel initialized")
    }

    fun signIn(
        email: String,
        password: String,
        showErrorSnackbar: (ErrorMessage) -> Unit
    ) {
        Timber.d("Attempting to sign in with email: %s", email)
        launchCatching(showErrorSnackbar) {
            authRepository.signIn(email, password)
            Timber.i("Sign in successful for email: %s. Triggering app restart.", email)
            _shouldRestartApp.value = true
        }.invokeOnCompletion { throwable ->
            throwable?.let { Timber.e(it, "Error during sign in for email: %s", email) }
        }
    }
}
