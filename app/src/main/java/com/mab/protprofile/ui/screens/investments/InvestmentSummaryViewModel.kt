package com.mab.protprofile.ui.screens.investments

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.mab.protprofile.data.model.ErrorMessage
import com.mab.protprofile.domain.repository.AuthRepository
import com.mab.protprofile.domain.repository.MyDataRepository
import com.mab.protprofile.ui.MainViewModel
import com.mab.protprofile.ui.data.InvestmentDetails
import com.mab.protprofile.ui.data.Partner
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class InvestmentSummaryViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
    private val dataRepository: MyDataRepository,
) : MainViewModel() {

    private val _investmentDetails = MutableStateFlow<InvestmentDetails?>(null)
    val investmentDetails: StateFlow<InvestmentDetails?>
        get() = _investmentDetails.asStateFlow()

    private val route = savedStateHandle.toRoute<InvestmentSummaryRoute>()
    private val totalProfit: Int = route.totalProfit

    init {
        Timber.d("InvestmentSummaryViewModel initialized")
    }

    fun loadData(
        showErrorSnackbar: (ErrorMessage) -> Unit,
    ) {
        launchCatching(showErrorSnackbar) {
            val number = authRepository.currentUser?.phoneNumber!!
            val users = dataRepository.getUsers()
            val investmentSummary = dataRepository.getInvestmentSummary()
            val currentUser = users.first { it.number == number }
            if (currentUser.parent == null) {
                val parentPartners = users.filter { it.parent == true }
                val childPartner = users.filter { it.parent == false }
                val otherPartner = users.filter { it.parent == null }

                val childPartnerInvestedAmount = childPartner.sumOf { it.investedAmount }
                val childPartnerProfitShare = childPartner.map { it.sharePercent }.sum()


                val partners = mutableListOf<Partner>()
                partners.add(
                    Partner(
                        parentPartners.first().name,
                        childPartnerInvestedAmount + parentPartners.first().investedAmount,
                        childPartnerProfitShare + parentPartners.first().sharePercent,
                    ),
                )
                partners.addAll(
                    otherPartner.map { user ->
                        Partner(user.name, user.investedAmount, user.sharePercent)
                    },
                )

                _investmentDetails.value = InvestmentDetails(
                    totalInvestment = investmentSummary.totalInvestment,
                    totalProfit = totalProfit,
                    assets = investmentSummary.assets,
                    yourInvestment = currentUser.investedAmount,
                    yourProfitShare = (currentUser.sharePercent * 100).toInt(),
                    partners = partners,
                )

            } else {
                _investmentDetails.value = InvestmentDetails(
                    totalInvestment = investmentSummary.totalInvestment,
                    totalProfit = totalProfit,
                    assets = investmentSummary.assets,
                    yourInvestment = currentUser.investedAmount,
                    yourProfitShare = (currentUser.sharePercent * 100).toInt(),
                    partners = users.map { user ->
                        Partner(user.name, user.investedAmount, user.sharePercent)
                    },
                )
            }
        }
    }
}
