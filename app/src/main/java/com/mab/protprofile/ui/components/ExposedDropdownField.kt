package com.mab.protprofile.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction

@Composable
fun ExposedDropdownField(
    label: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    editable: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier.clickable {
            onExpandedChange(!expanded)
        },
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = selectedOption ?: "",
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            singleLine = true,
            enabled = editable,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            trailingIcon = {
                if (editable) {
                    IconButton(onClick = { onExpandedChange(!expanded) }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                    }
                }
            },
        )
        if (editable) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) },
            ) {
                options.forEach { option ->
                    DropdownMenuItem(onClick = {
                        onOptionSelected(option)
                        onExpandedChange(false)
                    }, text = {
                        Text(option)
                    })
                }
            }
        }
    }
}
