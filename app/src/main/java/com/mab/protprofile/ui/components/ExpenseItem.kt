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
import com.mab.protprofile.data.model.Expense
import com.mab.protprofile.ui.theme.BadColor
import com.mab.protprofile.ui.utils.monthName

@Composable
fun ExpenseItem(expense: Expense, onEdit: (String) -> Unit) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = {
                        onEdit(expense.id)
                    },
                )
                .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val totalExpense = expense.expenses.values.sum()
            val expensesKind = expense.expenses.keys.joinToString(", ")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${monthName(expense.expenseMonth)} ${expense.expenseYear}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "₹$totalExpense",
                    color = BadColor,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Expenses: $expensesKind",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Updated By: ${expense.updatedBy}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
            )
        }
    }
}
