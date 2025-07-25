package com.mab.protprofile.domain.repository

import com.mab.protprofile.data.model.AppConfigs
import com.mab.protprofile.data.model.Expense
import com.mab.protprofile.data.model.InvestmentSummary
import com.mab.protprofile.data.model.Payment
import com.mab.protprofile.data.model.Transaction
import com.mab.protprofile.data.model.TransactionHistory
import com.mab.protprofile.data.model.UserInfo

interface MyDataRepository {
    suspend fun getTransactions(): List<Transaction>

    suspend fun getTransaction(transId: String): Transaction?

    suspend fun createTransaction(transaction: Transaction)

    suspend fun updateTransaction(
        transaction: Transaction,
        transactionHistory: TransactionHistory,
    )

    suspend fun deleteTransaction(transId: String)

    suspend fun getTransactionsHistory(): List<TransactionHistory>

    suspend fun getUserInfo(number: String): UserInfo

    suspend fun getUsers(): List<UserInfo>

    suspend fun getAppConfigs(): AppConfigs

    suspend fun getInvestmentSummary(): InvestmentSummary

    suspend fun getExpenses(): List<Expense>

    suspend fun getExpense(expenseId: String): Expense?

    suspend fun createExpense(expense: Expense)

    suspend fun updateExpense(expense: Expense)

    suspend fun gePayments(): List<Payment>

    suspend fun getPayment(paymentId: String): Payment?

    suspend fun createPayment(payment: Payment)
}
