package com.mab.protprofile.ui.screens.addTransection

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.mab.protprofile.R
import com.mab.protprofile.data.model.ErrorMessage
import com.mab.protprofile.data.model.Transaction
import com.mab.protprofile.data.model.TransactionChange
import com.mab.protprofile.data.model.TransactionHistory
import com.mab.protprofile.data.model.UserInfo
import com.mab.protprofile.domain.repository.AuthRepository
import com.mab.protprofile.domain.repository.MyDataRepository
import com.mab.protprofile.ui.MainViewModel
import com.mab.protprofile.ui.utils.getCurrentDateTimeString
import com.mab.protprofile.ui.utils.getCurrentEpoch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AddViewTransactionViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val dataRepository: MyDataRepository,
        private val authRepository: AuthRepository,
    ) : MainViewModel() {
        init {
            Timber.d("AddEntryViewModel initialized")
        }

        /**
         *  Here pair's first flag will say work done on screen and go back
         *  Pair's second flag will tell if any changes were made
         */
        private val _navigateToHome = MutableStateFlow(Pair(false, false))
        val navigateToHome: StateFlow<Pair<Boolean, Boolean>>
            get() = _navigateToHome.asStateFlow()

        private val _userInfo = MutableStateFlow<UserInfo?>(null)
        val userInfo: StateFlow<UserInfo?>
            get() = _userInfo.asStateFlow()

        private val addEntryRoute = savedStateHandle.toRoute<AddViewTransactionRoute>()
        private val transId: String = addEntryRoute.transId

        private val _transaction = MutableStateFlow<Transaction?>(null)
        val transaction: StateFlow<Transaction?>
            get() = _transaction.asStateFlow()

        fun loadTransaction(showErrorSnackbar: (ErrorMessage) -> Unit) {
            launchCatching(showErrorSnackbar) {
                Timber.d("Loading transaction with transId: $transId")
                val userInfo = dataRepository.getUserInfo(authRepository.currentUser?.phoneNumber!!)
                if (transId.isBlank()) {
                    Timber.d("transId is blank, creating new transaction")
                    _transaction.value = Transaction()
                } else {
                    Timber.d("transId is not blank, fetching existing transaction")
                    _transaction.value = dataRepository.getTransaction(transId)
                    Timber.d("Transaction loaded: ${_transaction.value}")
                }
                _userInfo.value = userInfo
            }
            Timber.d("loadTransaction completed")
        }

        fun saveTransaction(
            transaction: Transaction,
            showErrorSnackbar: (ErrorMessage) -> Unit,
        ) {
            val userName = _userInfo.value?.name
            if (userName.isNullOrBlank()) {
                Timber.e("User is null or blank, cannot save transaction")
                showErrorSnackbar(ErrorMessage.IdError(R.string.could_not_find_account))
                return
            }
            launchCatching(showErrorSnackbar) {
                Timber.d("Saving transaction for user: $userName")
                val isChanged: Boolean
                if (transId.isBlank()) {
                    Timber.d("Creating new transaction")
                    isChanged = true
                    val newTransaction =
                        transaction.copy(
                            id = "${transaction.transactionMonth}:${transaction.transactionYear}",
                            date = getCurrentDateTimeString(),
                            addedBy = userName,
                        )
                    dataRepository.createTransaction(newTransaction)
                    Timber.d("New transaction created: $newTransaction")
                } else {
                    Timber.d("Updating existing transaction with id: $transId")
                    val changeInfo = getChangedFields(_transaction.value!!, transaction)
                    isChanged = changeInfo.second
                    if (changeInfo.second) {
                        val transactionHistory =
                            TransactionHistory(
                                transId = transaction.id,
                                date = getCurrentEpoch(),
                                editedBy = userName,
                                changes = changeInfo.first,
                            )
                        dataRepository.updateTransaction(transaction, transactionHistory)
                        Timber.d("Transaction updated. Changes: ${changeInfo.first}")
                    } else {
                        Timber.d("No changes detected in the transaction.")
                    }
                }
                _navigateToHome.value = Pair(true, isChanged)
                Timber.d("saveTransaction completed. Navigating to home with isChanged: $isChanged")
            }
        }

        private fun getChangedFields(
            old: Transaction,
            updated: Transaction,
        ): Pair<TransactionChange, Boolean> {
            Timber.d("Checking for changed fields between old and updated transaction")
            var anyChange = false
            val change =
                TransactionChange(
                    totalSale =
                        if (old.totalSale != updated.totalSale) {
                            anyChange = true
                            old.totalSale
                        } else {
                            null
                        },
                    totalPurchase =
                        if (old.totalPurchase != updated.totalPurchase) {
                            anyChange = true
                            old.totalPurchase
                        } else {
                            null
                        },
                    creditPurchase =
                        if (old.creditPurchase != updated.creditPurchase) {
                            anyChange = true
                            old.creditPurchase
                        } else {
                            null
                        },
                    totalExpense =
                        if (old.totalExpense != updated.totalExpense) {
                            anyChange = true
                            old.totalExpense
                        } else {
                            null
                        },
                    totalProfit =
                        if (old.totalProfit != updated.totalProfit) {
                            anyChange = true
                            old.totalProfit
                        } else {
                            null
                        },
                )
            Timber.d("Changed fields: $change, Any change: $anyChange")
            return Pair(change, anyChange)
        }
    }
