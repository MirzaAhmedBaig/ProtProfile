package com.mab.protprofile.ui.screens.addExpense

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.mab.protprofile.R
import com.mab.protprofile.data.model.ErrorMessage
import com.mab.protprofile.data.model.Expense
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
class AddViewExpenseViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val dataRepository: MyDataRepository,
        private val authRepository: AuthRepository,
    ) : MainViewModel() {
        init {
            Timber.d("AddViewExpenseViewModel initialized")
        }

        private val _navigateToHome = MutableStateFlow(false)
        val navigateToHome: StateFlow<Boolean>
            get() = _navigateToHome.asStateFlow()

        private val _userInfo = MutableStateFlow<UserInfo?>(null)
        val userInfo: StateFlow<UserInfo?>
            get() = _userInfo.asStateFlow()

        private val route = savedStateHandle.toRoute<AddViewExpenseRoute>()
        private val expenseId: String = route.expenseId

        private val _expense = MutableStateFlow<Expense?>(null)
        val expense: StateFlow<Expense?>
            get() = _expense.asStateFlow()

        var expenseTypes: List<String>? = null

        fun loadExpense(showErrorSnackbar: (ErrorMessage) -> Unit) {
            launchCatching(showErrorSnackbar) {
                Timber.d("Loading expense with transId: $expenseId")
                val userInfo = dataRepository.getUserInfo(authRepository.currentUser?.phoneNumber!!)
                val appConfigs = dataRepository.getAppConfigs()
                expenseTypes = appConfigs.expenseTypes
                if (expenseId.isBlank()) {
                    Timber.d("transId is blank, creating new expense")
                    _expense.value = Expense()
                } else {
                    Timber.d("transId is not blank, fetching existing expense")
                    _expense.value = dataRepository.getExpense(expenseId)
                    Timber.d("Expense loaded: ${_expense.value}")
                }
                _userInfo.value = userInfo
            }
            Timber.d("loadExpense completed")
        }

        fun saveExpense(
            expense: Expense,
            showErrorSnackbar: (ErrorMessage) -> Unit,
        ) {
            val userName = _userInfo.value?.name
            if (userName.isNullOrBlank()) {
                Timber.e("User is null or blank, cannot save expense")
                showErrorSnackbar(ErrorMessage.IdError(R.string.could_not_find_account))
                return
            }
            launchCatching(showErrorSnackbar) {
                Timber.d("Saving expense for user: $userName")
                if (expenseId.isBlank()) {
                    Timber.d("Creating new expense")
                    val newExpense =
                        expense.copy(
                            id = "${expense.expenseMonth}:${expense.expenseYear}",
                            updatedAt = getCurrentDateTimeString(),
                            updatedBy = userName,
                        )
                    dataRepository.createExpense(newExpense)
                    _navigateToHome.value = true
                    Timber.d("New expense created: $newExpense")
                } else {
                    Timber.d("Updating existing expense with id: $expenseId")
                    dataRepository.updateExpense(expense)
                    _navigateToHome.value = true
                    Timber.d("Expense updated.")
                }
                Timber.d("saveExpense completed. Navigating to home.")
            }
        }
    }
