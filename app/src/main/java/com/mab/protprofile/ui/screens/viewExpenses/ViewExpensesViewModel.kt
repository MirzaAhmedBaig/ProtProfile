package com.mab.protprofile.ui.screens.viewExpenses

import com.mab.protprofile.data.model.ErrorMessage
import com.mab.protprofile.data.model.Expense
import com.mab.protprofile.domain.repository.MyDataRepository
import com.mab.protprofile.ui.MainViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ViewExpensesViewModel
@Inject
constructor(
    private val dataRepository: MyDataRepository,
) : MainViewModel() {
    init {
        Timber.d("ViewExpensesViewModel initialized")
    }

    private val _expenses = MutableStateFlow<List<Expense>?>(null)
    val expenses: StateFlow<List<Expense>?>
        get() = _expenses.asStateFlow()

    fun fetchExpenses(showErrorSnackbar: (ErrorMessage) -> Unit) {
        launchCatching(showErrorSnackbar) {
            _expenses.value = dataRepository.getExpenses()
            Timber.i("Fetched all expenses")
        }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("ViewEntriesViewModel cleared")
    }
}