package com.mab.protprofile.ui.data

data class InvestmentDetails(
    val totalInvestment: Int,
    val totalProfit: Int,
    val assets: Map<String, Int>,
    val yourInvestment: Int,
    val yourProfitShare: Int,
    val partners: List<Partner>,
)

data class Partner(
    val name: String,
    val investment: Int,
    val profitShare: Float,
)
