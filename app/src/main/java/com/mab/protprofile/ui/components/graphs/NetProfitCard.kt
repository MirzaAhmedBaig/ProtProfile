package com.mab.protprofile.ui.components.graphs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mab.protprofile.R
import com.mab.protprofile.ui.components.FinanceCard
import com.mab.protprofile.ui.theme.GoodColor

@Composable
fun NetProfitCard(netProfit: Int, userProfit: Int) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        FinanceCard(
            modifier = Modifier
                .weight(1f),
            title = "Net Profit",
            amount = "₹$netProfit",
            valueColor = GoodColor,
            icon = R.drawable.net_profit,
        )
        FinanceCard(
            modifier = Modifier
                .weight(1f),
            title = "Your Share",
            amount = "₹$userProfit",
            valueColor = GoodColor,
            icon = R.drawable.share_profit,
        )
    }
}
