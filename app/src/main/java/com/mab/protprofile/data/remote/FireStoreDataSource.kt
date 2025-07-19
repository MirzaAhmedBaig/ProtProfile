package com.mab.protprofile.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.mab.protprofile.BuildConfig
import com.mab.protprofile.data.model.Transaction
import com.mab.protprofile.data.model.TransactionHistory
import com.mab.protprofile.data.model.UserInfo
import com.mab.protprofile.exceptions.IdAlreadyExistsException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import timber.log.Timber

class FireStoreDataSource @Inject constructor(private val firestore: FirebaseFirestore) {

    suspend fun getTransactions(): List<Transaction> {
        Timber.d("Fetching all transactions")
        return try {
            firestore.collection(TRANSACTIONS_COLLECTION).get().await()?.toObjects<Transaction>()
                ?: emptyList<Transaction>().also { Timber.w("No transactions found, returning empty list.") }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching transactions")
            emptyList()
        }
    }

    suspend fun getTransaction(transId: String): Transaction? {
        Timber.d("Fetching transaction with id: $transId")
        return try {
            firestore.collection(TRANSACTIONS_COLLECTION).document(transId).get().await()
                .toObject<Transaction>()
        } catch (e: Exception) {
            Timber.e(e, "Error fetching transaction with id: $transId")
            null
        }
    }

    suspend fun createTransaction(transaction: Transaction) {
        Timber.d("Attempting to create transaction with id: ${transaction.id}")
        val transId = transaction.id
        try {
            getTransaction(transId)?.let {
                Timber.w("Transaction with id: $transId already exists.")
                throw IdAlreadyExistsException(transId)
            }
            firestore.collection(TRANSACTIONS_COLLECTION).document(transaction.id).set(transaction)
                .await()
            Timber.i("Transaction with id: ${transaction.id} created successfully.")
        } catch (e: IdAlreadyExistsException) {
            throw e
        } catch (e: Exception) {
            Timber.e(e, "Error creating transaction with id: ${transaction.id}")
            throw e
        }
    }

    suspend fun updateTransaction(
        transaction: Transaction,
        transactionHistory: TransactionHistory
    ) {
        Timber.d("Updating transaction with id: ${transaction.id}")
        try {
            firestore.collection(TRANSACTIONS_HISTORY_COLLECTION).add(transactionHistory).await()
            firestore.collection(TRANSACTIONS_COLLECTION).document(transaction.id).set(transaction)
                .await()
            Timber.i("Transaction with id: ${transaction.id} updated successfully.")
        } catch (e: Exception) {
            Timber.e(e, "Error updating transaction with id: ${transaction.id}")
        }
    }

    suspend fun deleteTransaction(transId: String) {
        Timber.d("Deleting transaction with id: $transId")
        try {
            firestore.collection(TRANSACTIONS_COLLECTION).document(transId).delete().await()
            Timber.i("Transaction with id: $transId deleted successfully.")
        } catch (e: Exception) {
            Timber.e(e, "Error deleting transaction with id: $transId")
        }
    }

    suspend fun getTransactionsHistory(): List<TransactionHistory> {
        Timber.d("Fetching all transaction history")
        return try {
            firestore.collection(TRANSACTIONS_HISTORY_COLLECTION).get().await()
                ?.toObjects<TransactionHistory>()
                ?: emptyList<TransactionHistory>().also { Timber.w("No transaction history found, returning empty list.") }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching transaction history")
            emptyList()
        }
    }

    suspend fun getUserInfo(number: String): UserInfo {
        Timber.d("Fetching user info for number: $number")
        return try {
            firestore.collection(USER_INFO).document(number).get().await().toObject<UserInfo>()!!
        } catch (e: Exception) {
            Timber.e(e, "Error fetching user info for number: $number")
            throw e
        }
    }

    companion object {
        private const val TRANSACTIONS_COLLECTION = "transactions-${BuildConfig.BUILD_TYPE}"
        private const val TRANSACTIONS_HISTORY_COLLECTION =
            "transactions-history-${BuildConfig.BUILD_TYPE}"
        private const val USER_INFO = "user-info"
    }
}