package com.mab.protprofile.ui.screens.payments

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.mab.protprofile.data.model.ErrorMessage
import com.mab.protprofile.data.model.Expense
import com.mab.protprofile.data.model.Payment
import com.mab.protprofile.data.model.UserRole
import com.mab.protprofile.domain.repository.AuthRepository
import com.mab.protprofile.domain.repository.MyDataRepository
import com.mab.protprofile.ui.MainViewModel
import com.mab.protprofile.ui.data.PaymentsScreenInfo
import com.mab.protprofile.ui.screens.addExpense.AddViewExpenseRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PaymentsViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
    private val dataRepository: MyDataRepository,
) : MainViewModel() {
    init {
        Timber.d("PaymentsViewModel initialized")
    }

    private val route = savedStateHandle.toRoute<PaymentsRoute>()
    private val totalProfit: Int = route.totalProfit

    private val _paymentsInfo = MutableStateFlow<PaymentsScreenInfo?>(null)
    val paymentInfo: StateFlow<PaymentsScreenInfo?>
        get() = _paymentsInfo.asStateFlow()

    fun fetchPayments(showErrorSnackbar: (ErrorMessage) -> Unit) {
        launchCatching(showErrorSnackbar) {
            val payments = dataRepository.gePayments()
            val users = dataRepository.getUsers()

            Timber.i("Fetched all payments")

            val currentUser = users.first { it.number == authRepository.currentUser?.phoneNumber }
            val myPayments = payments.filter { it.paidTo == currentUser.name }
            val childUser = users.first { it.parent == false }

            val totalNetProfit = if (currentUser.parent == true) {
                totalProfit * (currentUser.sharePercent + childUser.sharePercent)
            } else {
                totalProfit * currentUser.sharePercent
            }

            val allPayments = if (currentUser.role == UserRole.USER) {
                myPayments
            } else {
                payments
            }

            val childPartnerProfitReceived = if (currentUser.parent == true) {
                allPayments.filter { it.paidTo == childUser.name }.sumOf { it.amount!! }
            } else {
                null
            }

            val parentProfit = if (currentUser.parent == true) {
                (totalProfit * currentUser.sharePercent).toInt()
            } else {
                null
            }

            val profitReceived = if (childPartnerProfitReceived != null) {
                myPayments.sumOf { it.amount!! } - childPartnerProfitReceived
            } else {
                myPayments.sumOf { it.amount!! }
            }

            val outstandingProfit = totalNetProfit.toInt() - myPayments.sumOf { it.amount!! }

            _paymentsInfo.value = PaymentsScreenInfo(
                totalProfit = totalNetProfit.toInt(),
                parentProfit = parentProfit,
                profitReceived = profitReceived,
                outstandingProfit = outstandingProfit,
                childPartnerProfitReceived = childPartnerProfitReceived,
                payments = allPayments,
            )

        }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("PaymentsViewModel cleared")
    }
}

