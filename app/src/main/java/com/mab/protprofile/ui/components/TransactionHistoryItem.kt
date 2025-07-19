package com.mab.protprofile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import com.mab.protprofile.data.model.TransactionChange
import com.mab.protprofile.data.model.TransactionHistory
import com.mab.protprofile.ui.utils.formatDate
import com.mab.protprofile.ui.utils.monthName

@Composable
fun TransactionHistoryItem(transId: String, historyList: List<TransactionHistory>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
            })
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        val date = transId.split(":")
        Column(Modifier.padding(16.dp)) {
            Text(
                "${monthName(date.first().toInt())} ${date.last()}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            TransactionHistoryTimeline(historyList)
        }
    }
}

@Composable
fun TransactionHistoryTimeline(historyList: List<TransactionHistory>) {
    Column {
        historyList.forEachIndexed { idx, history ->
            Row(
                Modifier.height(IntrinsicSize.Min),
                verticalAlignment = Alignment.Top
            ) {
                // Timeline Section: Full height for continuous line
                Box(
                    Modifier
                        .width(24.dp)
                        .fillMaxHeight(), // Makes the Box as tall as this Row!
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(30.dp), // Adjust width as needed
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(
                                    if (idx == 0) Color.Green else Color.Gray,
                                    shape = CircleShape
                                )
                        )
                        if (idx != historyList.lastIndex) {
                            // Draw connecting line between timeline items
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .width(2.dp)
                                    .background(Color.Gray)
                            )
                        }
                    }
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        formatDate(history.date),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Changed By: ${history.editedBy}", style = MaterialTheme.typography.bodyMedium)
                    TransactionChangeText(history.changes)
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
fun TransactionChangeText(change: TransactionChange) {
    val changesDesc = listOfNotNull(
        change.totalSale?.let { "Total Sale: $it" },
        change.totalPurchase?.let { "Total Purchase: $it" },
        change.creditPurchase?.let { "Credit Purchase: $it" },
        change.totalExpense?.let { "Total Expense: $it" },
        change.totalProfit?.let { "Total Profit: $it" },
    ).joinToString(", ")
    if (changesDesc.isNotEmpty()) {
        Text(changesDesc, style = MaterialTheme.typography.bodySmall)
    }
}
