package com.mab.protprofile.ui.screens.viewTransactions

import com.mab.protprofile.data.model.ErrorMessage
import com.mab.protprofile.data.model.Transaction
import com.mab.protprofile.domain.repository.MyDataRepository
import com.mab.protprofile.ui.MainViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ViewTransectionsViewModel
@Inject
constructor(
    private val dataRepository: MyDataRepository,
) : MainViewModel() {
    init {
        Timber.d("ViewEntriesViewModel initialized")
    }

    private val _transactions = MutableStateFlow<List<Transaction>?>(null)
    val transactions: StateFlow<List<Transaction>?>
        get() = _transactions.asStateFlow()

    fun fetchAllTransactions(showErrorSnackbar: (ErrorMessage) -> Unit) {
        launchCatching(showErrorSnackbar) {
            _transactions.value = dataRepository.getTransactions()
            Timber.i("Fetched all transactions")
        }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("ViewEntriesViewModel cleared")
    }
}
