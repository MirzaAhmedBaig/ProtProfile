package com.mab.protprofile.ui.screens.addTransection

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mab.protprofile.R
import com.mab.protprofile.data.model.ErrorMessage
import com.mab.protprofile.data.model.Transaction
import com.mab.protprofile.data.model.UserRole
import com.mab.protprofile.ui.components.CenterTopAppBar
import com.mab.protprofile.ui.components.ExposedDropdownField
import com.mab.protprofile.ui.components.LoadingIndicator
import com.mab.protprofile.ui.components.StandardButton
import com.mab.protprofile.ui.navigation.RouteInfo
import com.mab.protprofile.ui.theme.ProtProfileTheme
import kotlinx.serialization.Serializable
import timber.log.Timber
import java.util.Calendar

@Serializable
data class AddViewTransactionRoute(val transId: String)

@Composable
fun AddViewTransactionScreen(
    goto: (RouteInfo) -> Unit,
    showErrorSnackbar: (ErrorMessage) -> Unit,
    viewModel: AddViewTransactionViewModel = hiltViewModel(),
) {
    Timber.d("AddEntryScreen called")
    val navigateToHome by viewModel.navigateToHome.collectAsStateWithLifecycle()

    if (navigateToHome.first) {
        Timber.d("Navigating back to home with shouldRefresh: ${navigateToHome.second}")
        goto(RouteInfo.OnBack(navigateToHome.second))
    } else {
        Timber.d("Loading AddEntryScreenLoad")

        AddEntryScreenLoad(
            viewModel,
            showErrorSnackbar = showErrorSnackbar,
            goto = goto,
        )
    }
}

@Composable
fun AddEntryScreenLoad(
    viewModel: AddViewTransactionViewModel,
    showErrorSnackbar: (ErrorMessage) -> Unit,
    goto: (RouteInfo) -> Unit,
) {
    Timber.d("AddEntryScreenLoad called")
    val transaction by viewModel.transaction.collectAsStateWithLifecycle()
    val userInfo by viewModel.userInfo.collectAsStateWithLifecycle()
    if (transaction == null || userInfo == null) {
        Timber.d("Showing LoadingIndicator as transaction or userInfo is null")
        LoadingIndicator()
    } else {
        AddEntryScreenContent(
            // transaction = transaction!!, // This can cause NPE if transaction is null
            transaction = transaction!!,
            userInfo!!.role,
            showErrorSnackbar = showErrorSnackbar,
            saveTransaction = viewModel::saveTransaction,
            goto = goto,
        )
    }

    LaunchedEffect(true) {
        Timber.d("LaunchedEffect to load transaction")
        viewModel.loadTransaction(showErrorSnackbar)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEntryScreenContent(
    transaction: Transaction,
    role: UserRole,
    goto: (RouteInfo) -> Unit,
    showErrorSnackbar: (ErrorMessage) -> Unit,
    saveTransaction: (Transaction, (ErrorMessage) -> Unit) -> Unit,
) {
    Timber.d("AddEntryScreenContent called with transaction: $transaction, role: $role")
    val focusManager = LocalFocusManager.current
    val editEnabled = remember { mutableStateOf(transaction.id.isBlank()) }
    val titleStringId =
        remember { mutableIntStateOf(if (transaction.id.isBlank()) R.string.add_new_month else R.string.details) }
    Scaffold(
        topBar = {
            CenterTopAppBar(
                title = stringResource(titleStringId.intValue),
                actions = {
                    if (role == UserRole.ADMIN) {
                        GetToBarMenu(
                            onClick = {
                                Timber.d("Edit button clicked")
                                editEnabled.value = true
                                titleStringId.intValue = R.string.edit_details
                            },
                            editEnabled.value,
                        )
                    }
                },
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
            val editableTransaction = remember { mutableStateOf(transaction) }

            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val years = (currentYear - 1)..(currentYear)
            val months = (1..12).map { it.toString() }

            var showMonthMenu by remember { mutableStateOf(false) }
            var showYearMenu by remember { mutableStateOf(false) }
            var isSubmitted by remember { mutableStateOf(false) }

            val (form) = createRefs()

            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(top = 24.dp)
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
                        editable = transaction.id.isBlank(),
                        selectedOption =
                            editableTransaction.value.transactionMonth?.toString()
                                ?: "",
                        onOptionSelected = {
                            editableTransaction.value =
                                editableTransaction.value.copy(transactionMonth = it.toInt())
                        },
                        expanded = showMonthMenu,
                        onExpandedChange = { showMonthMenu = it },
                        modifier = Modifier.weight(1f),
                    )

                    Spacer(Modifier.size(16.dp))

                    ExposedDropdownField(
                        label = "Year",
                        options = years.map { it.toString() },
                        editable = transaction.id.isBlank(),
                        selectedOption =
                            editableTransaction.value.transactionYear?.toString()
                                ?: "",
                        onOptionSelected = {
                            editableTransaction.value =
                                editableTransaction.value.copy(transactionYear = it.toInt())
                        },
                        expanded = showYearMenu,
                        onExpandedChange = { showYearMenu = it },
                        modifier = Modifier.weight(1f),
                    )
                }

                OutlinedTextField(
                    value = editableTransaction.value.totalSale?.toString() ?: "",
                    onValueChange = {
                        editableTransaction.value =
                            editableTransaction.value.copy(totalSale = it.toIntOrNull())
                    },
                    isError =
                        editableTransaction.value.totalSale?.toString()
                            .isNullOrBlank() && isSubmitted,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                    label = { Text("Total Sale") },
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next,
                        ),
                    singleLine = true,
                    readOnly = !editEnabled.value,
                )

                OutlinedTextField(
                    value = editableTransaction.value.totalPurchase?.toString() ?: "",
                    onValueChange = {
                        editableTransaction.value =
                            editableTransaction.value.copy(
                                totalPurchase = it.toIntOrNull(),
                            )
                    },
                    isError =
                        editableTransaction.value.totalPurchase?.toString()
                            .isNullOrBlank() && isSubmitted,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                    label = { Text("Total Purchase") },
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next,
                        ),
                    singleLine = true,
                    readOnly = !editEnabled.value,
                )

                OutlinedTextField(
                    value = editableTransaction.value.creditPurchase?.toString() ?: "",
                    onValueChange = {
                        editableTransaction.value =
                            editableTransaction.value.copy(
                                creditPurchase = it.toIntOrNull(),
                            )
                    },
                    isError =
                        editableTransaction.value.creditPurchase?.toString()
                            .isNullOrBlank() && isSubmitted,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                    label = { Text("Credit Purchase") },
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next,
                        ),
                    singleLine = true,
                    readOnly = !editEnabled.value,
                )

                OutlinedTextField(
                    value = editableTransaction.value.totalExpense?.toString() ?: "",
                    onValueChange = {
                        editableTransaction.value =
                            editableTransaction.value.copy(
                                totalExpense = it.toIntOrNull(),
                            )
                    },
                    isError =
                        editableTransaction.value.totalExpense?.toString()
                            .isNullOrBlank() && isSubmitted,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                    label = { Text("Total Expense") },
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next,
                        ),
                    singleLine = true,
                    readOnly = !editEnabled.value,
                )

                OutlinedTextField(
                    value = editableTransaction.value.totalProfit?.toString() ?: "",
                    onValueChange = {
                        editableTransaction.value =
                            editableTransaction.value.copy(totalProfit = it.toIntOrNull())
                    },
                    isError =
                        editableTransaction.value.totalProfit?.toString()
                            .isNullOrBlank() && isSubmitted,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text("Total Profit") },
                    singleLine = true,
                    readOnly = !editEnabled.value,
                )

                Spacer(Modifier.size(16.dp))

                if (editEnabled.value) {
                    StandardButton(
                        label = R.string.submit,
                        onButtonClick = {
                            isSubmitted = true

                            if (editableTransaction.value.transactionMonth == null ||
                                editableTransaction.value.transactionYear == null ||
                                editableTransaction.value.totalSale == null ||
                                editableTransaction.value.totalPurchase == null ||
                                editableTransaction.value.creditPurchase == null ||
                                editableTransaction.value.totalExpense == null ||
                                editableTransaction.value.totalProfit == null
                            ) {
                                Timber.w("Validation failed: Not all fields are filled")
                                showErrorSnackbar(ErrorMessage.StringError("Please fill all the fields"))
                            } else {
                                Timber.i("Validation successful, saving transaction: ${editableTransaction.value}")
                                saveTransaction(editableTransaction.value, showErrorSnackbar)
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun GetToBarMenu(onClick: () -> Unit, editEnabled: Boolean) {
    Timber.d("GetToBarMenu called with editEnabled: $editEnabled")
    if (!editEnabled) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = stringResource(R.string.edit),
            )
        }
    }
}

@Composable
@Preview(showSystemUi = true)
fun AddEntryScreenPreview() {
    ProtProfileTheme(darkTheme = true) {
        AddEntryScreenContent(
            Transaction(),
            goto = {},
            role = UserRole.ADMIN,
            showErrorSnackbar = {},
            saveTransaction = { _, _ -> },
        )
    }
}
