package com.mab.protprofile.ui.data

data class FinanceData(
    val netProfitTillDate: Int,
    val userProfit: Int,
    val monthlyNetProfit: List<Pair<String, Int>>,
    val monthlySalesPurchases: List<Triple<String, Int, Int>>,
    val cashCreditPurchases: List<Triple<String, Int, Int>>,
    val monthlyExpenses: List<Pair<String, Int>>,
    val grossNetProfit: List<Triple<String, Int, Int>>,
    val cumulativeOverview: List<OverviewData>,
    val highlightMonths: List<Pair<String, Int>>,
)
