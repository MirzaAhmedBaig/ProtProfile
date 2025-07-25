package com.mab.protprofile.ui.screens.addExpense

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mab.protprofile.R
import com.mab.protprofile.data.model.ErrorMessage
import com.mab.protprofile.data.model.Expense
import com.mab.protprofile.data.model.UserRole
import com.mab.protprofile.ui.components.CenterTopAppBar
import com.mab.protprofile.ui.components.ExposedDropdownField
import com.mab.protprofile.ui.components.LoadingIndicator
import com.mab.protprofile.ui.components.SingleExpenseItem
import com.mab.protprofile.ui.components.StandardButton
import com.mab.protprofile.ui.components.SwipeableItemWithActions
import com.mab.protprofile.ui.navigation.RouteInfo
import kotlinx.serialization.Serializable
import timber.log.Timber
import java.util.Calendar

@Serializable
data class AddViewExpenseRoute(val expenseId: String)

@Composable
fun AddViewExpenseScreen(
    goto: (RouteInfo) -> Unit,
    showErrorSnackbar: (ErrorMessage) -> Unit,
    viewModel: AddViewExpenseViewModel = hiltViewModel(),
) {
    Timber.d("AddViewExpenseScreen called")
    val navigateToHome by viewModel.navigateToHome.collectAsStateWithLifecycle()

    if (navigateToHome) {
        Timber.d("Navigating back to home with shouldRefresh true")
        goto(RouteInfo.OnBack(true))
    } else {
        Timber.d("Loading AddExpenseScreenLoad")

        AddExpenseScreenLoad(
            viewModel,
            showErrorSnackbar = showErrorSnackbar,
            goto = goto,
        )
    }
}

@Composable
fun AddExpenseScreenLoad(
    viewModel: AddViewExpenseViewModel,
    showErrorSnackbar: (ErrorMessage) -> Unit,
    goto: (RouteInfo) -> Unit,
) {
    Timber.d("AddExpenseScreenLoad called")
    val expense by viewModel.expense.collectAsStateWithLifecycle()
    val userInfo by viewModel.userInfo.collectAsStateWithLifecycle()
    val expenseTypes = viewModel.expenseTypes
    if (expense == null || userInfo == null) {
        Timber.d("Showing LoadingIndicator as expense or userInfo is null")
        LoadingIndicator()
    } else {
        AddExpenseScreenContent(
            // Expense = Expense!!, // This can cause NPE if Expense is null
            expense = expense!!,
            expenseTypes = expenseTypes!!,
            userInfo!!.role,
            showErrorSnackbar = showErrorSnackbar,
            saveExpense = viewModel::saveExpense,
            goto = goto,
        )
    }

    LaunchedEffect(true) {
        Timber.d("LaunchedEffect to load Expense")
        viewModel.loadExpense(showErrorSnackbar)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreenContent(
    expense: Expense,
    expenseTypes: List<String>,
    role: UserRole,
    goto: (RouteInfo) -> Unit,
    showErrorSnackbar: (ErrorMessage) -> Unit,
    saveExpense: (Expense, (ErrorMessage) -> Unit) -> Unit,
) {
    Timber.d("AddExpenseScreenContent called with Expense: $Expense, role: $role")
    val focusManager = LocalFocusManager.current
    val editAllowed = role == UserRole.ADMIN
    val titleStringId =
        remember { mutableIntStateOf(if (expense.id.isBlank()) R.string.add_expenses else R.string.expenses) }
    Scaffold(
        topBar = {
            CenterTopAppBar(
                title = stringResource(titleStringId.intValue),
                onBack = { goto(RouteInfo.OnBack()) },
            )
        },
    ) { innerPadding ->
        ConstraintLayout(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                focusManager.clearFocus()
                            },
                        )
                    }
                    .fillMaxSize(),
        ) {
            val editableExpense = remember { mutableStateOf(expense) }

            val editableItemList =
                remember {
                    mutableStateListOf<ExpenseItem>().apply {
                        if (editAllowed) {
                            add(
                                ExpenseItem(name = "", amount = null),
                            )
                        }

                        addAll(
                            expense.expenses.map {
                                ExpenseItem(
                                    name = it.key,
                                    amount = it.value,
                                )
                            },
                        )
                    }
                }

            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val years = (currentYear - 1)..(currentYear)
            val months = (1..12).map { it.toString() }

            var showMonthMenu by remember { mutableStateOf(false) }
            var showYearMenu by remember { mutableStateOf(false) }

            val (form) = createRefs()

            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp)
                        .constrainAs(form) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ExposedDropdownField(
                        label = "Month",
                        options = months,
                        editable = expense.id.isBlank(),
                        selectedOption =
                            editableExpense.value.expenseMonth?.toString()
                                ?: "",
                        onOptionSelected = {
                            editableExpense.value =
                                editableExpense.value.copy(expenseMonth = it.toInt())
                        },
                        expanded = showMonthMenu,
                        onExpandedChange = { showMonthMenu = it },
                        modifier = Modifier.weight(1f),
                    )

                    Spacer(Modifier.size(16.dp))

                    ExposedDropdownField(
                        label = "Year",
                        options = years.map { it.toString() },
                        editable = expense.id.isBlank(),
                        selectedOption =
                            editableExpense.value.expenseYear?.toString()
                                ?: "",
                        onOptionSelected = {
                            editableExpense.value =
                                editableExpense.value.copy(expenseYear = it.toInt())
                        },
                        expanded = showYearMenu,
                        onExpandedChange = { showYearMenu = it },
                        modifier = Modifier.weight(1f),
                    )
                }

                LazyColumn(
                    modifier =
                        Modifier
                            .padding(horizontal = 24.dp)
                            .fillMaxWidth(),
                ) {
                    itemsIndexed(
                        editableItemList,
                        key = { index, item -> item.name + (item.amount?.toString() ?: "null") },
                    ) { index, item ->
                        if (item.name.isBlank() || !editAllowed) {
                            SingleExpenseItem(
                                expenseTypes = expenseTypes,
                                expense = item.name,
                                amount = item.amount,
                                showErrorSnackbar = showErrorSnackbar,
                                onSave = { name, amount ->
                                    if (editableItemList.map { it.name }.contains(name)) {
                                        editableItemList.removeIf { it.name == name }
                                    }
                                    editableItemList.add(ExpenseItem(name, amount))
//                                        editableItems[name] = amount
                                    Timber.d("onSaved : $editableItemList")
                                },
                            )
                        } else {
                            SwipeableItemWithActions(
                                isRevealed = item.isOptionsRevealed,
                                onExpanded = {
                                    editableItemList[index] = item.copy(isOptionsRevealed = true)
                                },
                                onCollapsed = {
                                    editableItemList[index] = item.copy(isOptionsRevealed = false)
                                },
                                actions = {
                                    IconButton(
                                        onClick = {
                                            editableItemList.remove(item)
                                        },
                                        modifier =
                                            Modifier
                                                .clip(CircleShape)
                                                .background(Color.Red),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = Color.White,
                                        )
                                    }
                                },
                            ) {
                                SingleExpenseItem(
                                    expenseTypes = expenseTypes,
                                    expense = item.name,
                                    amount = item.amount,
                                    showErrorSnackbar = showErrorSnackbar,
                                    onSave = { name, amount ->
                                        if (editableItemList.map { it.name }.contains(name)) {
                                            editableItemList.removeIf { it.name == name }
                                        }
                                        editableItemList.add(ExpenseItem(name, amount))
//                                        editableItems[name] = amount
                                        Timber.d("onSaved : $editableItemList")
                                    },
                                )
                            }
                        }
                    }
                }

                if (editableItemList.size > 1) {
                    Row(
                        modifier =
                            Modifier
                                .padding(24.dp)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                                    shape = OutlinedTextFieldDefaults.shape,
                                )
                                .fillMaxWidth(),
                    ) {
                        Text(
                            "Total",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier =
                                Modifier
                                    .padding(8.dp)
                                    .weight(1f),
                        )
                        Text(
                            "${
                                editableItemList.filter { it.amount != null }.sumOf { it.amount!! }
                            }",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.End,
                            modifier =
                                Modifier
                                    .padding(8.dp)
                                    .weight(1f),
                        )
                    }
                }

                OutlinedTextField(
                    value = editableExpense.value.notes ?: "",
                    onValueChange = {
                        editableExpense.value =
                            editableExpense.value.copy(notes = it)
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text("Details") },
                    minLines = 2,
                    readOnly = !editAllowed,
                )

                Spacer(Modifier.size(16.dp))

                if (editAllowed) {
                    StandardButton(
                        label = R.string.submit,
                        onButtonClick = {
                            Timber.i("Saving Expense: ${editableExpense.value}")
                            Timber.i("EditableItemList: $editableItemList")

                            if (editableExpense.value.expenseMonth == null ||
                                editableExpense.value.expenseYear == null ||
                                editableItemList.size < 2
                            ) {
                                Timber.w("Validation failed: Not all fields are filled")
                                showErrorSnackbar(ErrorMessage.StringError("Please fill all the fields"))
                            } else {
                                Timber.i("Validation successful, saving Expense: ${editableExpense.value}")
                                val finalExpenses =
                                    editableItemList.filter { it.amount != null }
                                        .associate { it.name to it.amount!! }
                                saveExpense(
                                    editableExpense.value.copy(expenses = finalExpenses),
                                    showErrorSnackbar,
                                )
                            }
                        },
                    )
                }
            }
        }
    }
}

data class ExpenseItem(
    val name: String,
    val amount: Int?,
    val isOptionsRevealed: Boolean = false,
)
