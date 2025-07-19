package com.mab.protprofile.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mab.protprofile.ui.components.CenterTopAppBar
import kotlinx.serialization.Serializable
import com.mab.protprofile.R
import com.mab.protprofile.data.model.ErrorMessage
import com.mab.protprofile.data.model.Transaction
import com.mab.protprofile.data.model.UserInfo
import com.mab.protprofile.data.model.UserRole
import com.mab.protprofile.ui.components.LoadingIndicator
import com.mab.protprofile.ui.components.TopAppBarDropdownMenu
import com.mab.protprofile.ui.components.graphs.CashCreditPurchasesChart
import com.mab.protprofile.ui.components.graphs.ChartSectionTitle
import com.mab.protprofile.ui.components.graphs.CumulativeOverviewChart
import com.mab.protprofile.ui.components.graphs.ExpensesChart
import com.mab.protprofile.ui.components.graphs.GrossNetProfitChart
import com.mab.protprofile.ui.components.graphs.HighlightsSection
import com.mab.protprofile.ui.components.graphs.NetProfitCard
import com.mab.protprofile.ui.components.graphs.NetProfitTrendChart
import com.mab.protprofile.ui.components.graphs.SalesPurchasesChart
import com.mab.protprofile.ui.data.FinanceData
import timber.log.Timber


@Serializable
object HomeRoute

@Composable
fun HomeScreen(
    openAddItemScreen: (String) -> Unit,
    openSignInScreen: () -> Unit,
    openTransactionsScreen: (List<Transaction>) -> Unit,
    openTransactionsHistoryScreen: () -> Unit,
    showErrorSnackbar: (ErrorMessage) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    Timber.d("HomeScreen initiated")
    val shouldRestartApp by viewModel.shouldRestartApp.collectAsStateWithLifecycle()

    if (shouldRestartApp) {
        openSignInScreen()
    } else {
        HomeScreenLoad(
            viewModel = viewModel,
            openAddItemScreen = openAddItemScreen,
            loadTransactions = viewModel::fetchAllTransactions,
            showErrorSnackbar = showErrorSnackbar,
            openTransactionsScreen = openTransactionsScreen,
            openTransactionsHistoryScreen = openTransactionsHistoryScreen
        )
    }
}

@Composable
fun HomeScreenLoad(
    viewModel: HomeViewModel,
    showErrorSnackbar: (ErrorMessage) -> Unit,
    loadTransactions: ((ErrorMessage) -> Unit) -> Unit,
    openAddItemScreen: (String) -> Unit,
    openTransactionsScreen: (List<Transaction>) -> Unit,
    openTransactionsHistoryScreen: () -> Unit,
) {
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val financeData by viewModel.financeData.collectAsStateWithLifecycle()
    val userInfo by viewModel.userInfo.collectAsStateWithLifecycle()
    Timber.d("HomeScreenLoad: transactions=$transactions, financeData=$financeData, userInfo=$userInfo")
    if (transactions == null || userInfo == null) {
        LoadingIndicator()
    } else {
        HomeScreenContent(
            transactions = transactions!!,
            financeData = financeData,
            userInfo = userInfo!!,
            signOut = viewModel::signOut,
            openAddItemScreen = openAddItemScreen,
            showErrorSnackbar = showErrorSnackbar,
            openTransactionsScreen = openTransactionsScreen,
            openTransactionsHistoryScreen = openTransactionsHistoryScreen
        )
    }
    LaunchedEffect(true) {
        Timber.d("HomeScreenLoad: LaunchedEffect triggered to load transactions")
        loadTransactions(showErrorSnackbar)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    transactions: List<Transaction>,
    financeData: FinanceData?,
    userInfo: UserInfo,
    showErrorSnackbar: (ErrorMessage) -> Unit,
    signOut: ((ErrorMessage) -> Unit) -> Unit = {},
    openAddItemScreen: (String) -> Unit,
    openTransactionsScreen: (List<Transaction>) -> Unit,
    openTransactionsHistoryScreen: () -> Unit,
) {
    Timber.d("HomeScreenContent: Rendering with transactions count=${transactions.count()}")
    Scaffold(
        topBar = {
            CenterTopAppBar(
                title = stringResource(R.string.dashboard),
                actions = {
                    MoreTasksMenu(
                        transactions = transactions,
                        userInfo.role,
                        openAddItemScreen = openAddItemScreen,
                        openTransactionsScreen = openTransactionsScreen,
                        openTransactionsHistoryScreen = openTransactionsHistoryScreen,
                        signOut = { signOut(showErrorSnackbar) }
                    )
                }
            )
        }
    ) { innerPadding ->
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            val (body, noData) = createRefs()

            if (financeData == null) {
                Text(
                    text = "No Data Found",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.constrainAs(noData) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                )
            } else {
                AnalyticsScreen(
                    financeData = financeData,
                    modifier = Modifier
                        .constrainAs(body) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            height = Dimension.fillToConstraints
                        }
                )
            }
        }
    }
}


@Composable
private fun MoreTasksMenu(
    transactions: List<Transaction>,
    role: UserRole,
    openAddItemScreen: (String) -> Unit,
    openTransactionsScreen: (List<Transaction>) -> Unit,
    openTransactionsHistoryScreen: () -> Unit,
    signOut: () -> Unit
) {
    TopAppBarDropdownMenu(
        iconContent = {
            Icon(Icons.Filled.MoreVert, stringResource(id = R.string.menu_more))
        }
    ) { closeMenu ->
        if (role == UserRole.ADMIN) {
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.add_transaction)) },
                onClick = { openAddItemScreen(""); closeMenu() }
            )
        }
        if (transactions.isNotEmpty())
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.transactions)) },
                onClick = { openTransactionsScreen(transactions); closeMenu() }
            )
        if (role == UserRole.ADMIN && transactions.isNotEmpty()) {
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.open_transactions_history)) },
                onClick = { openTransactionsHistoryScreen(); closeMenu() }
            )
        }
        DropdownMenuItem(
            text = { Text(text = stringResource(id = R.string.sign_out)) },
            onClick = { signOut(); closeMenu() }
        )
    }
}

@Composable
fun AnalyticsScreen(
    financeData: FinanceData,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        // Net Profit Summary
        NetProfitCard(financeData.netProfitTillDate, financeData.userProfit)

        if (financeData.highlightMonths.size > 1) {
            HighlightsSection(financeData.highlightMonths)
        }

        Spacer(modifier = Modifier.height(16.dp))
        ChartSectionTitle("Net Profit Trend")
        NetProfitTrendChart(financeData.monthlyNetProfit)

        Spacer(modifier = Modifier.height(16.dp))
        ChartSectionTitle("Sales vs Purchases")
        SalesPurchasesChart(financeData.monthlySalesPurchases)

        Spacer(modifier = Modifier.height(16.dp))
        ChartSectionTitle("Cash vs Credit Purchases")
        CashCreditPurchasesChart(financeData.cashCreditPurchases)

        Spacer(modifier = Modifier.height(16.dp))
        ChartSectionTitle("Expenses Trend")
        ExpensesChart(financeData.monthlyExpenses)

        Spacer(modifier = Modifier.height(16.dp))
        ChartSectionTitle("Gross vs Net Profit")
        GrossNetProfitChart(financeData.grossNetProfit)

        Spacer(modifier = Modifier.height(16.dp))
        ChartSectionTitle("Cumulative Overview")
        CumulativeOverviewChart(financeData.cumulativeOverview)


    }
}
