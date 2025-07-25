package com.mab.protprofile.ui.data

import com.mab.protprofile.data.model.Payment

data class PaymentsScreenInfo(
    val totalProfit: Int,
    val parentProfit: Int?,
    val profitReceived: Int,
    val outstandingProfit: Int?,
    val childPartnerProfitReceived: Int?,
    val payments: List<Payment>,
)
