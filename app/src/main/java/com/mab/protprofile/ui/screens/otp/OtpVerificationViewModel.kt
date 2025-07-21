package com.mab.protprofile.ui.screens.otp

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
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
class OtpVerificationViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val authRepository: AuthRepository,
    ) : MainViewModel() {
        private val _authState = MutableStateFlow<AuthStateNew>(AuthStateNew.Idle)
        val authState: StateFlow<AuthStateNew> = _authState

        private val addEntryRoute = savedStateHandle.toRoute<OtpVerificationRoute>()
        private val verificationId: String = addEntryRoute.verificationId

        init {
            Timber.d("OtpVerificationViewModel initialized")
        }

        fun verifyOtp(
            otp: String,
            showErrorSnackbar: (ErrorMessage) -> Unit,
        ) {
            Timber.d("Attempting to verify OTP: %s with verificationId: %s", otp, verificationId)
            launchCatching(showErrorSnackbar) {
                authRepository.verifyOtp(verificationId, otp).collect { result ->
                    when (result) {
                        is Resource.Loading -> _authState.value = AuthStateNew.Loading
                        is Resource.Success -> {
                            Timber.i(
                                "OTP verification successful for verificationId: %s",
                                verificationId,
                            )
                            _authState.value = AuthStateNew.Verified(result.data)
                        }

                        is Resource.Error -> {
                            Timber.e(
                                result.exception,
                                "Error during OTP verification for verificationId: %s",
                                verificationId,
                            )
                            _authState.value =
                                AuthStateNew.Error(result.exception.message ?: "Error")
                        }
                    }
                }
            }
        }
    }
