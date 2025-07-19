package com.mab.protprofile.domain.repository

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
}
