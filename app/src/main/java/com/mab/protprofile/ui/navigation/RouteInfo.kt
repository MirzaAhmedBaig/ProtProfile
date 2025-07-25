package com.mab.protprofile.ui.navigation

import com.mab.protprofile.data.model.Transaction

sealed class RouteInfo {
    object Login : RouteInfo()
    data class OtpVerification(val verificationId: String) : RouteInfo()
    object EmailLogin : RouteInfo()
    object Home : RouteInfo()
    data class AddViewTransaction(val transId: String) : RouteInfo()
    data class ViewTransactions(val transactions: List<Transaction>) : RouteInfo()
    data class AddViewExpense(val expenseId: String) : RouteInfo()
    object ViewExpenses : RouteInfo()
    object TransactionsHistory : RouteInfo()
    data class InvestmentSummary(val totalProfit: Int) : RouteInfo()
    object AddPayment : RouteInfo()
    data class ViewPayments(val totalProfit: Int) : RouteInfo()
    data class OnBack(val shouldRefresh: Boolean = false) : RouteInfo()
}