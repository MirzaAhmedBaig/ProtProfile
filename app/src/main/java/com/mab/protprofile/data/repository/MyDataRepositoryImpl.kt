package com.mab.protprofile.data.repository

import com.mab.protprofile.data.model.Transaction
import com.mab.protprofile.data.model.TransactionHistory
import com.mab.protprofile.data.model.UserInfo
import com.mab.protprofile.data.remote.FireStoreDataSource
import com.mab.protprofile.domain.repository.MyDataRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyDataRepositoryImpl
    @Inject
    constructor(
        private val fireStoreDataSource: FireStoreDataSource,
    ) : MyDataRepository {
        override suspend fun getTransactions(): List<Transaction> = fireStoreDataSource.getTransactions()

        override suspend fun getTransaction(transId: String): Transaction? = fireStoreDataSource.getTransaction(transId)

        override suspend fun createTransaction(transaction: Transaction) {
            fireStoreDataSource.createTransaction(transaction)
        }

        override suspend fun updateTransaction(
            transaction: Transaction,
            transactionHistory: TransactionHistory,
        ) {
            fireStoreDataSource.updateTransaction(transaction, transactionHistory)
        }

        override suspend fun deleteTransaction(transId: String) {
            fireStoreDataSource.deleteTransaction(transId)
        }

        override suspend fun getTransactionsHistory(): List<TransactionHistory> = fireStoreDataSource.getTransactionsHistory()

        override suspend fun getUserInfo(number: String): UserInfo = fireStoreDataSource.getUserInfo(number)
    }
