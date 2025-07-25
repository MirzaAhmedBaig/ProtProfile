package com.mab.protprofile.ui.screens.payments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mab.protprofile.R
import com.mab.protprofile.data.model.ErrorMessage
import com.mab.protprofile.ui.components.CenterTopAppBar
import com.mab.protprofile.ui.components.ExposedDropdownField
import com.mab.protprofile.ui.components.FinanceCard
import com.mab.protprofile.ui.components.LoadingIndicator
import com.mab.protprofile.ui.components.PaymentItem
import com.mab.protprofile.ui.data.PaymentsScreenInfo
import com.mab.protprofile.ui.navigation.RouteInfo
import com.mab.protprofile.ui.theme.BadColor
import com.mab.protprofile.ui.theme.GoodColor
import com.mab.protprofile.ui.theme.YourProfitShare
import kotlinx.serialization.Serializable
import timber.log.Timber

@Serializable
data class PaymentsRoute(val totalProfit: Int)

@Composable
fun PaymentsScreen(
    showErrorSnackbar: (ErrorMessage) -> Unit,
    goto: (RouteInfo) -> Unit,
    viewModel: PaymentsViewModel = hiltViewModel(),
) {
    Timber.d("ViewExpensesScreen Composable launched")
    val paymentInfo = viewModel.paymentInfo.collectAsStateWithLifecycle().value

    if (paymentInfo == null) {
        LoadingIndicator()
    } else {
        PaymentsScreenContent(
            paymentInfo = paymentInfo,
            goto = goto,
        )
    }

    LaunchedEffect(true) {
        viewModel.fetchPayments(showErrorSnackbar)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentsScreenContent(
    paymentInfo: PaymentsScreenInfo,
    goto: (RouteInfo) -> Unit,
) {
    Timber.d("ViewExpensesScreenContent Composable launched")
    Scaffold(
        topBar = {
            CenterTopAppBar(
                title = stringResource(R.string.payments),
                onBack = { goto(RouteInfo.OnBack()) },
            )
        },
    ) { innerPadding ->
        ConstraintLayout(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        start = 4.dp,
                        end = 4.dp,
                        bottom = 4.dp,
                    ),
        ) {
            val (body) = createRefs()

            val paidToOptions =
                mutableListOf<String>().apply {
                    add("All")
                    addAll(paymentInfo.payments.map { it.paidTo!! }.distinct())
                }
            val enableFilter = paidToOptions.count() > 2

            val allPayments = remember { mutableStateOf(paymentInfo.payments) }

            Column(
                modifier =
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                        .constrainAs(body) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            height = Dimension.fillToConstraints
                        },
                horizontalAlignment = Alignment.End,
            ) {
                val initialProfitValue = "Net Profit:₹${paymentInfo.totalProfit}"
                val profitValue = remember { mutableStateOf(initialProfitValue) }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    FinanceCard(
                        modifier =
                            Modifier
                                .weight(1f)
                                .clickable(
                                    onClick = {
                                        if (paymentInfo.parentProfit != null) {
                                            if (profitValue.value == initialProfitValue) {
                                                profitValue.value =
                                                    "Your Profit:₹${paymentInfo.parentProfit}"
                                            } else {
                                                profitValue.value = initialProfitValue
                                            }
                                        }
                                    },
                                ),
                        title = profitValue.value.split(":").first(),
                        amount = profitValue.value.split(":").last(),
                        valueColor = GoodColor,
                    )
                    FinanceCard(
                        modifier =
                            Modifier
                                .weight(1f),
                        title = "Profit Received",
                        amount = "₹${paymentInfo.profitReceived}",
                        valueColor = GoodColor,
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    FinanceCard(
                        modifier =
                            Modifier
                                .weight(1f),
                        title = "Outstanding Profit",
                        amount = "₹${paymentInfo.outstandingProfit}",
                        valueColor = BadColor,
                    )
                    if (paymentInfo.childPartnerProfitReceived != null) {
                        FinanceCard(
                            modifier =
                                Modifier
                                    .weight(1f),
                            title = "Partner Transfer",
                            amount = "₹${paymentInfo.childPartnerProfitReceived}",
                            valueColor = YourProfitShare,
                        )
                    } else {
                        Box(
                            modifier =
                                Modifier
                                    .weight(1f),
                        )
                    }
                }

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, end = 4.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Text(
                        "Payment History",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp).weight(1f),
                    )

                    if (enableFilter) {
                        var expanded by remember { mutableStateOf(false) }
                        val selectedFilter = remember { mutableStateOf(paidToOptions.first()) }
                        ExposedDropdownField(
                            label = "Filter",
                            options = paidToOptions,
                            selectedOption = selectedFilter.value,
                            onOptionSelected = { option ->
                                selectedFilter.value = option
                                if (option == "All") {
                                    allPayments.value = paymentInfo.payments
                                } else {
                                    allPayments.value =
                                        paymentInfo.payments.filter { it.paidTo == option }
                                }
                            },
                            modifier =
                                Modifier
                                    .weight(1f),
                            expanded = expanded,
                            onExpandedChange = { expanded = it },
                        )
                    }
                }

                allPayments.value.forEach { payment ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        PaymentItem(payment, paymentInfo.childPartnerProfitReceived != null)
                    }
                }
            }
        }
    }
}
