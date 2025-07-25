package com.mab.protprofile.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mab.protprofile.data.model.Transaction
import com.mab.protprofile.ui.theme.BadColor
import com.mab.protprofile.ui.theme.GoodColor
import com.mab.protprofile.ui.utils.monthName

@Composable
fun TransactionItem(transaction: Transaction, onEdit: (String) -> Unit) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = {
                    onEdit(transaction.id)
                })
                .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val netProfit = (transaction.totalProfit ?: 0) - (transaction.totalExpense ?: 0)
            val color = if (netProfit >= 0) GoodColor else BadColor

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${monthName(transaction.transactionMonth)} ${transaction.transactionYear}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Net Profit : ₹$netProfit",
                    color = color,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "Total Purchase: ₹${transaction.totalPurchase ?: 0}")
                Text(text = "Total Sale: ₹${transaction.totalSale ?: 0}")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "Total Expense: ₹${transaction.totalExpense ?: 0}")
                Text(text = "Gross Profit: ₹${transaction.totalProfit ?: 0}")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Added By: ${transaction.addedBy}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
            )
        }
    }
}
