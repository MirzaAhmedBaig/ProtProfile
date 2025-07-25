package com.mab.protprofile.ui.screens.pay

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mab.protprofile.R
import com.mab.protprofile.data.model.ErrorMessage
import com.mab.protprofile.data.model.Payment
import com.mab.protprofile.ui.components.CenterTopAppBar
import com.mab.protprofile.ui.components.ExposedDropdownField
import com.mab.protprofile.ui.components.LoadingIndicator
import com.mab.protprofile.ui.components.StandardButton
import com.mab.protprofile.ui.navigation.RouteInfo
import kotlinx.serialization.Serializable
import timber.log.Timber
import java.util.Calendar
import kotlin.collections.map

@Serializable
object PayRoute

@Composable
fun PayScreen(
    showErrorSnackbar: (ErrorMessage) -> Unit,
    goto: (RouteInfo) -> Unit,
    viewModel: PayViewModel = hiltViewModel(),
) {
    Timber.d("PayScreen called")
    val done by viewModel.done.collectAsStateWithLifecycle()

    if (done) {
        Timber.d("Navigating back to home")
        goto(RouteInfo.OnBack())
    } else {
        Timber.d("Loading PayScreen")

        PayScreenContent(
            viewModel,
            showErrorSnackbar = showErrorSnackbar,
            goto = goto,
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayScreenContent(
    viewModel: PayViewModel,
    showErrorSnackbar: (ErrorMessage) -> Unit,
    goto: (RouteInfo) -> Unit,
) {
    val users by viewModel.users.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    if (users == null) {
        LoadingIndicator()
    } else {
        Scaffold(
            topBar = {
                CenterTopAppBar(
                    title = stringResource(R.string.pay),
                    onBack = { goto(RouteInfo.OnBack()) },
                )
            },
        ) { innerPadding ->
            ConstraintLayout(
                modifier = Modifier
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
                val payment = remember { mutableStateOf(Payment()) }

                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                val years = (currentYear - 1)..(currentYear)
                val months = (1..12).map { it.toString() }

                var showMonthMenu by remember { mutableStateOf(false) }
                var showYearMenu by remember { mutableStateOf(false) }
                var showPayToMenu by remember { mutableStateOf(false) }

                val (form) = createRefs()

                Column(
                    modifier = Modifier
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ExposedDropdownField(
                            label = "Month",
                            options = months,
                            selectedOption = payment.value.paymentMonth?.toString()
                                ?: "",
                            onOptionSelected = {
                                payment.value =
                                    payment.value.copy(paymentMonth = it.toInt())
                            },
                            expanded = showMonthMenu,
                            onExpandedChange = { showMonthMenu = it },
                            modifier = Modifier.weight(1f),

                            )

                        Spacer(Modifier.size(16.dp))

                        ExposedDropdownField(
                            label = "Year",
                            options = years.map { it.toString() },
                            selectedOption = payment.value.paymentYear?.toString()
                                ?: "",
                            onOptionSelected = {
                                payment.value =
                                    payment.value.copy(paymentYear = it.toInt())
                            },
                            expanded = showYearMenu,
                            onExpandedChange = { showYearMenu = it },
                            modifier = Modifier.weight(1f),

                            )
                    }

                    ExposedDropdownField(
                        label = "Pay To",
                        options = users!!.map { it.name },
                        selectedOption = payment.value.paidTo,
                        onOptionSelected = {
                            payment.value =
                                payment.value.copy(paidTo = it)
                        },
                        expanded = showPayToMenu,
                        onExpandedChange = { showPayToMenu = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                    )

                    OutlinedTextField(
                        value = payment.value.amount?.toString() ?: "",
                        onValueChange = {
                            payment.value =
                                payment.value.copy(amount = it.toIntOrNull())
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        label = { Text("Amount") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done,
                        ),
                        singleLine = true,
                    )

                    Spacer(Modifier.size(16.dp))

                    StandardButton(
                        label = R.string.add_payment,

                        onButtonClick = {
                            if (payment.value.paymentMonth == null ||
                                payment.value.paymentYear == null ||
                                payment.value.paidTo == null ||
                                payment.value.amount == null
                            ) {
                                Timber.w("Validation failed: Not all fields are filled")
                                showErrorSnackbar(ErrorMessage.StringError("Please fill all the fields"))
                            } else {
                                Timber.i("Validation successful, saving payment: ${payment.value}")
                                viewModel.addPayment(payment.value, showErrorSnackbar)
                            }

                        },
                    )
                }

            }
        }
    }
    LaunchedEffect(true) {
        Timber.d("InvestmentSummaryScreen: LaunchedEffect triggered to load data")
        viewModel.loadUsers(showErrorSnackbar)
    }
}