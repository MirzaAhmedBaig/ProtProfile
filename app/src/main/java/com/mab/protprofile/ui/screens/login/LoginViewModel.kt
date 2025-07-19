package com.mab.protprofile.ui.screens.login

import android.app.Activity
import com.mab.protprofile.data.model.ErrorMessage
import com.mab.protprofile.data.model.Resource
import com.mab.protprofile.domain.repository.AuthRepository
import com.mab.protprofile.ui.MainViewModel
import com.mab.protprofile.ui.data.AuthStateNew
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : MainViewModel() {
    private val _authState = MutableStateFlow<AuthStateNew>(AuthStateNew.Idle)
    val authState: StateFlow<AuthStateNew> = _authState

    init {
        Timber.d("LoginViewModel initialized")
    }

    fun startPhoneNumberVerification(
        phone: String,
        activity: Activity,
        showErrorSnackbar: (ErrorMessage) -> Unit
    ) {
        Timber.d("Starting phone number verification for phone: %s", phone)
        launchCatching(showErrorSnackbar) {
            authRepository.startPhoneNumberVerification(phone, activity).collect { result ->
                when (result) {
                    is Resource.Loading -> _authState.value = AuthStateNew.Loading
                    is Resource.Success -> {
                        Timber.i("OTP sent successfully. Verification ID: %s", result.data)
                        _authState.value = AuthStateNew.CodeSent(result.data)
                    }

                    is Resource.Error -> {
                        Timber.e(result.exception, "Error during phone number verification")
                        _authState.value =
                            AuthStateNew.Error(result.exception.message ?: "Error")
                    }
                }
            }
        }
    }

}
