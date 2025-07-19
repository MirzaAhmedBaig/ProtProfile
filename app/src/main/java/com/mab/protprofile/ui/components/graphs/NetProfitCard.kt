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
import com.mab.protprofile.ui.theme.GoodColor

@Composable
fun NetProfitCard(netProfit: Int, userProfit: Int) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        Card(
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .weight(1f)
                .padding(4.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.net_profit),
                        contentDescription = "Best Month"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Net Profit Till Date", style = MaterialTheme.typography.titleMedium)
                }

                Spacer(modifier = Modifier.height(4.dp))
                Box(Modifier.padding(start = 8.dp)) {
                    Text(
                        "₹${netProfit}",
                        style = MaterialTheme.typography.headlineLarge,
                        color = GoodColor
                    )
                }
            }

        }

        Card(
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .weight(1f)
                .padding(4.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.share_profit),
                        contentDescription = "Best Month"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Your Share", style = MaterialTheme.typography.titleMedium)
                }

                Spacer(modifier = Modifier.height(4.dp))
                Box(Modifier.padding(start = 8.dp)) {
                    Text(
                        "₹${userProfit}",
                        style = MaterialTheme.typography.headlineLarge,
                        color = GoodColor
                    )
                }
            }
        }
    }
}
