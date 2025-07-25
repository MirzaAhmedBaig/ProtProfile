package com.mab.protprofile.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.mab.protprofile.BuildConfig
import com.mab.protprofile.data.model.AppConfigs
import com.mab.protprofile.data.model.Expense
import com.mab.protprofile.data.model.InvestmentSummary
import com.mab.protprofile.data.model.Payment
import com.mab.protprofile.data.model.Transaction
import com.mab.protprofile.data.model.TransactionHistory
import com.mab.protprofile.data.model.UserInfo
import com.mab.protprofile.exceptions.ItemAlreadyExistsException
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class FireStoreDataSource
    @Inject
    constructor(
        private val firestore: FirebaseFirestore,
    ) {
        suspend fun getTransactions(): List<Transaction> {
            Timber.d("Fetching all transactions")
            return try {
                firestore
                    .collection(TRANSACTIONS_COLLECTION)
                    .get()
                    .await()
                    ?.toObjects<Transaction>()
                    ?: emptyList<Transaction>().also { Timber.w("No transactions found, returning empty list.") }
            } catch (e: Exception) {
                Timber.e(e, "Error fetching transactions")
                emptyList()
            }
        }

        suspend fun getTransaction(transId: String): Transaction? {
            Timber.d("Fetching transaction with id: $transId")
            return try {
                firestore
                    .collection(TRANSACTIONS_COLLECTION)
                    .document(transId)
                    .get()
                    .await()
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
                    throw ItemAlreadyExistsException("Entry already added for given month and year")
                }
                Timber.e("Error code")
                firestore
                    .collection(TRANSACTIONS_COLLECTION)
                    .document(transaction.id)
                    .set(transaction)
                    .await()
                Timber.i("Transaction with id: ${transaction.id} created successfully.")
            } catch (e: ItemAlreadyExistsException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, "Error creating transaction with id: ${transaction.id}")
                throw e
            }
        }

        suspend fun updateTransaction(
            transaction: Transaction,
            transactionHistory: TransactionHistory,
        ) {
            Timber.d("Updating transaction with id: ${transaction.id}")
            try {
                firestore.collection(TRANSACTIONS_HISTORY_COLLECTION).add(transactionHistory).await()
                firestore
                    .collection(TRANSACTIONS_COLLECTION)
                    .document(transaction.id)
                    .set(transaction)
                    .await()
                Timber.i("Transaction with id: ${transaction.id} updated successfully.")
            } catch (e: Exception) {
                Timber.e(e, "Error updating transaction with id: ${transaction.id}")
            }
        }

        suspend fun deleteTransaction(transId: String) {
            Timber.d("Deleting transaction with id: $transId")
            try {
                firestore
                    .collection(TRANSACTIONS_COLLECTION)
                    .document(transId)
                    .delete()
                    .await()
                Timber.i("Transaction with id: $transId deleted successfully.")
            } catch (e: Exception) {
                Timber.e(e, "Error deleting transaction with id: $transId")
            }
        }

        suspend fun getTransactionsHistory(): List<TransactionHistory> {
            Timber.d("Fetching all transaction history")
            return try {
                firestore
                    .collection(TRANSACTIONS_HISTORY_COLLECTION)
                    .get()
                    .await()
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
                firestore
                    .collection(USER_INFO)
                    .document(number)
                    .get()
                    .await()
                    .toObject<UserInfo>()!!
            } catch (e: Exception) {
                Timber.e(e, "Error fetching user info for number: $number")
                throw e
            }
        }

        suspend fun getUsers(): List<UserInfo> {
            Timber.d("Fetching all users")
            return try {
                firestore
                    .collection(USER_INFO)
                    .get()
                    .await()
                    ?.toObjects<UserInfo>()
                    ?: emptyList<UserInfo>().also { Timber.w("No transaction history found, returning empty list.") }
            } catch (e: Exception) {
                Timber.e(e, "Error fetching all users")
                throw e
            }
        }

        suspend fun getAppConfigs(): AppConfigs {
            Timber.d("Fetching app configs")
            return try {
                firestore
                    .collection(APP_CONFIGS)
                    .get()
                    .await()
                    ?.toObjects<AppConfigs>()?.firstOrNull()!!
            } catch (e: Exception) {
                Timber.e(e, "Error fetching app info")
                throw e
            }
        }

        suspend fun getInvestmentSummary(): InvestmentSummary {
            Timber.d("Fetching investment summary")
            return try {
                firestore
                    .collection(INVESTMENT_SUMMARY)
                    .get()
                    .await()
                    ?.toObjects<InvestmentSummary>()?.firstOrNull()!!
            } catch (e: Exception) {
                Timber.e(e, "Error fetching investment summary")
                throw e
            }
        }

        suspend fun getExpenses(): List<Expense> {
            Timber.d("Fetching all expenses")
            return try {
                firestore
                    .collection(EXPENSES)
                    .get()
                    .await()
                    ?.toObjects<Expense>()
                    ?: emptyList<Expense>().also { Timber.w("No expenses found, returning empty list.") }
            } catch (e: Exception) {
                Timber.e(e, "Error fetching expenses")
                emptyList()
            }
        }

        suspend fun getExpense(expenseId: String): Expense? {
            Timber.d("Fetching expense with id: $expenseId")
            return try {
                firestore
                    .collection(EXPENSES)
                    .document(expenseId)
                    .get()
                    .await()
                    .toObject<Expense>()
            } catch (e: Exception) {
                Timber.e(e, "Error fetching expense with id: $expenseId")
                null
            }
        }

        suspend fun createExpense(expense: Expense) {
            Timber.d("Attempting to create expense with id: ${expense.id}")
            val expenseId = expense.id
            try {
                getExpense(expenseId)?.let {
                    Timber.w("Expense with id: $expenseId already exists.")
                    throw ItemAlreadyExistsException("Entry already added for given month and year")
                }
                firestore
                    .collection(EXPENSES)
                    .document(expense.id)
                    .set(expense)
                    .await()
                Timber.i("Expense with id: ${expense.id} created successfully.")
            } catch (e: ItemAlreadyExistsException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, "Error creating expense with id: ${expense.id}")
                throw e
            }
        }

        suspend fun updateExpense(expense: Expense) {
            Timber.d("Updating expense with id: ${expense.id}")
            try {
                firestore
                    .collection(EXPENSES)
                    .document(expense.id)
                    .set(expense)
                    .await()
                Timber.i("Expense with id: ${expense.id} updated successfully.")
            } catch (e: Exception) {
                Timber.e(e, "Error updating expense with id: ${expense.id}")
            }
        }

        suspend fun getPayment(paymentId: String): Payment? {
            Timber.d("Fetching payment with id: $paymentId")
            return try {
                firestore
                    .collection(PAYMENTS)
                    .document(paymentId)
                    .get()
                    .await()
                    .toObject<Payment>()
            } catch (e: Exception) {
                Timber.e(e, "Error fetching payment with id: $paymentId")
                null
            }
        }

        suspend fun getPayments(): List<Payment> {
            Timber.d("Fetching all payments")
            return try {
                firestore
                    .collection(PAYMENTS)
                    .get()
                    .await()
                    ?.toObjects<Payment>()
                    ?: emptyList<Payment>().also { Timber.w("No payments found, returning empty list.") }
            } catch (e: Exception) {
                Timber.e(e, "Error fetching payments")
                emptyList()
            }
        }

        suspend fun createPayment(payment: Payment) {
            Timber.d("Attempting to create payment with id: ${payment.id}")
            val paymentId = payment.id
            try {
                getPayment(paymentId)?.let {
                    Timber.w("payment with id: $paymentId already exists.")
                    throw ItemAlreadyExistsException("Entry already added for given month and year")
                }
                firestore
                    .collection(PAYMENTS)
                    .document(payment.id)
                    .set(payment)
                    .await()
                Timber.i("payment with id: ${payment.id} created successfully.")
            } catch (e: ItemAlreadyExistsException) {
                throw e
            } catch (e: Exception) {
                Timber.e(e, "Error creating payment with id: ${payment.id}")
                throw e
            }
        }

        companion object {
            private const val TRANSACTIONS_COLLECTION = "transactions-${BuildConfig.BUILD_TYPE}"
            private const val TRANSACTIONS_HISTORY_COLLECTION =
                "transactions-history-${BuildConfig.BUILD_TYPE}"
            private const val EXPENSES = "expenses-${BuildConfig.BUILD_TYPE}"
            private const val PAYMENTS = "payments-${BuildConfig.BUILD_TYPE}"

            // Universal
            private const val USER_INFO = "user-info"
            private const val APP_CONFIGS = "app-configs"
            private const val INVESTMENT_SUMMARY = "investment_summary"
        }
    }
