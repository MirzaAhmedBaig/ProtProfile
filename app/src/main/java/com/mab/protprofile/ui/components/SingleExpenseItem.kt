package com.mab.protprofile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mab.protprofile.data.model.ErrorMessage

sealed class ExpenseState() {
    object Edition : ExpenseState()
    object Saved : ExpenseState()
    object Cleared : ExpenseState()
}

@Composable
fun SingleExpenseItem(
    expenseTypes: List<String>,
    expense: String = "",
    amount: Int? = null,
    onSave: (String, Int) -> Unit,
    showErrorSnackbar: (ErrorMessage) -> Unit,
) {
    var showExpenseMenu by remember { mutableStateOf(false) }
    var selectedExpense by remember { mutableStateOf(expense) }
    var expenseAmount by remember { mutableStateOf(amount) }

    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ExposedDropdownField(
            label = "Expense",
            options = expenseTypes,
            editable = expense.isBlank(),
            selectedOption = selectedExpense,
            onOptionSelected = { selectedExpense = it },
            expanded = showExpenseMenu,
            onExpandedChange = { showExpenseMenu = it },
            modifier = Modifier.weight(1f),
        )

        Spacer(modifier = Modifier.size(16.dp))

        OutlinedTextField(
            value = expenseAmount?.toString() ?: "",
            onValueChange = { expenseAmount = it.toIntOrNull() },
            modifier = Modifier
                .weight(1f),
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done,
            ),
            enabled = expense.isBlank(),
            singleLine = true,
        )
        if (expense.isBlank()) {
            Spacer(modifier = Modifier.size(16.dp))

            IconButton(
                modifier = Modifier.weight(0.3f),
                onClick = {
                    if (selectedExpense.isBlank() || expenseAmount?.toString().isNullOrEmpty()) {
                        showErrorSnackbar(ErrorMessage.StringError("Please fill all the fields"))
                        return@IconButton
                    }
                    onSave(selectedExpense, expenseAmount!!)
                    selectedExpense = ""
                    expenseAmount = null

                },
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Save",
                )
            }
        }
    }
}