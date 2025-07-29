package com.mab.protprofile.ui.screens.pay

import com.mab.protprofile.data.model.ErrorMessage
import com.mab.protprofile.data.model.Payment
import com.mab.protprofile.data.model.UserInfo
import com.mab.protprofile.domain.repository.AuthRepository
import com.mab.protprofile.domain.repository.MyDataRepository
import com.mab.protprofile.ui.MainViewModel
import com.mab.protprofile.ui.utils.getCurrentDateTimeString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PayViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        private val dataRepository: MyDataRepository,
    ) : MainViewModel() {
        private val _done = MutableStateFlow(false)
        val done: StateFlow<Boolean>
            get() = _done.asStateFlow()

        private val _users = MutableStateFlow<List<UserInfo>?>(null)
        val users: StateFlow<List<UserInfo>?>
            get() = _users.asStateFlow()

        private lateinit var currentUser: UserInfo

        init {
            Timber.d("PayViewModel initialized")
        }

        fun loadUsers(showErrorSnackbar: (ErrorMessage) -> Unit) {
            launchCatching(showErrorSnackbar) {
                currentUser = dataRepository.getUserInfo(authRepository.currentUser?.phoneNumber!!)
                val users = dataRepository.getUsers()
                if (currentUser.parent != true) {
                    _users.value = users.filter { it.parent != false }
                } else {
                    _users.value = users
                }
            }
        }

        fun addPayment(payment: Payment, showErrorSnackbar: (ErrorMessage) -> Unit) {
            launchCatching(showErrorSnackbar) {
                val paymentData =
                    payment.copy(
                        id = "${payment.paymentMonth}:${payment.paymentYear}:${payment.paidTo}",
                        date = getCurrentDateTimeString(),
                        addedBy = currentUser.name,
                    )
                dataRepository.createPayment(paymentData)
                Timber.i("Successfully Added new payment")
                _done.value = true
            }
        }
    }
