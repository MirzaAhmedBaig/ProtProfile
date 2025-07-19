package com.mab.protprofile.ui.screens.history

import com.mab.protprofile.data.model.ErrorMessage
import com.mab.protprofile.data.model.TransactionHistory
import com.mab.protprofile.domain.repository.MyDataRepository
import com.mab.protprofile.ui.MainViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel
    @Inject
    constructor(
        private val dataRepository: MyDataRepository,
    ) : MainViewModel() {
        private val _transactionsHistory =
            MutableStateFlow<Map<String, List<TransactionHistory>>?>(null)
        val transactionsHistory: StateFlow<Map<String, List<TransactionHistory>>?>
            get() = _transactionsHistory.asStateFlow()

        fun fetchAllHistory(showErrorSnackbar: (ErrorMessage) -> Unit) {
            Timber.d("fetchAllHistory called")
            launchCatching(showErrorSnackbar) {
                _transactionsHistory.value =
                    dataRepository
                        .getTransactionsHistory()
                        .groupBy { it.transId }
                        .mapValues { entry -> entry.value.sortedByDescending { it.date } }
                Timber.d("Successfully fetched transaction history")
            }
        }
    }
