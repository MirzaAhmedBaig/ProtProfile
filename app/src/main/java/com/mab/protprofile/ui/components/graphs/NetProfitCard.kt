package com.mab.protprofile.ui.components.graphs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mab.protprofile.R
import com.mab.protprofile.ui.components.FinanceCard
import com.mab.protprofile.ui.theme.GoodColor

@Composable
fun NetProfitCard(netProfit: Int, userProfit: Int) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        FinanceCard(
            modifier =
                Modifier
                    .weight(1f),
            title = "Net Profit",
            amount = "₹$netProfit",
            valueColor = GoodColor,
            icon = R.drawable.net_profit,
        )
        FinanceCard(
            modifier =
                Modifier
                    .weight(1f),
            title = "Your Share",
            amount = "₹$userProfit",
            valueColor = GoodColor,
            icon = R.drawable.share_profit,
        )
    }
}
