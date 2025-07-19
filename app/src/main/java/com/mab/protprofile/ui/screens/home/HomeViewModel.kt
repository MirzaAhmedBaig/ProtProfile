package com.mab.protprofile.ui.screens.home

import com.mab.protprofile.data.model.ErrorMessage
import com.mab.protprofile.data.model.Transaction
import com.mab.protprofile.data.model.UserInfo
import com.mab.protprofile.domain.repository.AuthRepository
import com.mab.protprofile.domain.repository.MyDataRepository
import com.mab.protprofile.ui.MainViewModel
import com.mab.protprofile.ui.data.FinanceData
import com.mab.protprofile.ui.data.OverviewData
import com.mab.protprofile.ui.utils.monthName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.min

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        private val dataRepository: MyDataRepository,
    ) : MainViewModel() {
        init {
            Timber.d("HomeViewModel initialized")
        }

        private val _shouldRestartApp = MutableStateFlow(false)
        val shouldRestartApp: StateFlow<Boolean>
            get() = _shouldRestartApp.asStateFlow()

        private val _transactions = MutableStateFlow<List<Transaction>?>(null)
        val transactions: StateFlow<List<Transaction>?>
            get() = _transactions.asStateFlow()

        private val _financeData = MutableStateFlow<FinanceData?>(null)
        val financeData: StateFlow<FinanceData?>
            get() = _financeData.asStateFlow()

        private val _userInfo = MutableStateFlow<UserInfo?>(null)
        val userInfo: StateFlow<UserInfo?>
            get() = _userInfo.asStateFlow()

        fun signOut(showErrorSnackbar: (ErrorMessage) -> Unit) {
            Timber.i("signOut called")
            launchCatching(showErrorSnackbar) {
                authRepository.signOut()
                _shouldRestartApp.value = true
            }
        }

        fun fetchAllTransactions(showErrorSnackbar: (ErrorMessage) -> Unit) {
            Timber.i("fetchAllTransactions called")
            launchCatching(showErrorSnackbar) {
                Timber.d("Fetching user info... ${authRepository.currentUser?.phoneNumber}")
                val userInfo = dataRepository.getUserInfo(authRepository.currentUser?.phoneNumber!!)
                Timber.d("User info fetched: $userInfo")
                Timber.d("Fetching transactions...")
                val result = dataRepository.getTransactions()
                Timber.d("Transactions fetched, count: ${result.size}")
                _userInfo.value = userInfo
                _financeData.value = generateInsightData(result)
                _transactions.value = result
                Timber.i("Data processed for UI")
            }
        }

        private fun generateInsightData(transactions: List<Transaction>): FinanceData? {
            if (transactions.isEmpty()) {
                return null
            }
            val sorted =
                transactions.sortedWith(compareBy({ it.transactionYear }, { it.transactionMonth }))

            val monthlyNetProfit = mutableListOf<Pair<String, Int>>()
            val monthlySalesPurchases = mutableListOf<Triple<String, Int, Int>>()
            val cashCreditPurchases = mutableListOf<Triple<String, Int, Int>>()
            val monthlyExpenses = mutableListOf<Pair<String, Int>>()
            val grossNetProfit = mutableListOf<Triple<String, Int, Int>>()
            val cumulativeOverview = mutableListOf<OverviewData>()

            var netProfitTillDate = 0
            var cumulativeSale = 0
            var cumulativePurchase = 0
            var cumulativeExpense = 0
            var cumulativeProfit = 0

            for (tx in sorted) {
                val year = tx.transactionYear ?: continue
                val month = tx.transactionMonth ?: continue

                val sale = tx.totalSale ?: 0
                val purchase = tx.totalPurchase ?: 0
                val credit = tx.creditPurchase ?: 0
                val expense = tx.totalExpense ?: 0
                val profit = tx.totalProfit ?: 0

                val netProfit = profit - expense
                netProfitTillDate += netProfit

                val monthLabel = "${monthName(month)} $year"

                monthlyNetProfit.add(monthLabel to netProfit)
                monthlySalesPurchases.add(Triple(monthLabel, sale, purchase))
                cashCreditPurchases.add(Triple(monthLabel, purchase - credit, credit))
                monthlyExpenses.add(monthLabel to expense)
                grossNetProfit.add(Triple(monthLabel, profit, netProfit))

                cumulativeSale += sale
                cumulativePurchase += purchase
                cumulativeExpense += expense
                cumulativeProfit += netProfit

                cumulativeOverview.add(
                    OverviewData(
                        monthLabel,
                        cumulativeSale,
                        cumulativePurchase,
                        cumulativeExpense,
                        cumulativeProfit,
                    ),
                )
            }

            // For highlightMonths
            val netProfits = monthlyNetProfit.sortedByDescending { it.second }
            val minSize = netProfits.size / 2
            val maxSize = 3
            val window = min(minSize, maxSize)
            val isValidHighlights = netProfits.size > window

            val highlightMonths =
                if (isValidHighlights) {
                    netProfits.take(window) + netProfits.takeLast(window)
                } else {
                    listOf()
                }

            Timber.d("FinanceData generated")
            return FinanceData(
                netProfitTillDate,
                (netProfitTillDate * _userInfo.value!!.sharePercent).toInt(),
                monthlyNetProfit,
                monthlySalesPurchases,
                cashCreditPurchases,
                monthlyExpenses,
                grossNetProfit,
                cumulativeOverview,
                highlightMonths,
            )
        }
    }
