package com.mab.protprofile.ui.screens.viewExpenses

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import com.mab.protprofile.R
import com.mab.protprofile.data.model.ErrorMessage
import com.mab.protprofile.data.model.Expense
import com.mab.protprofile.ui.Constants
import com.mab.protprofile.ui.components.CenterTopAppBar
import com.mab.protprofile.ui.components.ExpenseItem
import com.mab.protprofile.ui.components.LoadingIndicator
import com.mab.protprofile.ui.components.TransactionItem
import com.mab.protprofile.ui.navigation.RouteInfo
import kotlinx.serialization.Serializable
import timber.log.Timber

@Serializable
object ViewExpensesRoute

@Composable
fun ViewExpensesScreen(
    showErrorSnackbar: (ErrorMessage) -> Unit,
    goto: (RouteInfo) -> Unit,
    navBackStackEntry: NavBackStackEntry,
    viewModel: ViewExpensesViewModel = hiltViewModel(),
) {
    Timber.d("ViewExpensesScreen Composable launched")
    val expenses = viewModel.expenses.collectAsStateWithLifecycle().value

    val shouldRefreshFlow = navBackStackEntry.savedStateHandle
        .getStateFlow(Constants.SHOULD_REFRESH_KEY, false)
    val shouldRefresh by shouldRefreshFlow.collectAsStateWithLifecycle()

    if (expenses == null) {
        LoadingIndicator()
    } else {
        ViewExpensesScreenContent(
            expenses = expenses,
            goto = goto,
        )
    }

    LaunchedEffect(shouldRefresh) {
        if (shouldRefresh || expenses == null) {
            Timber.i("Refresh triggered for ViewExpensesScreen")
            viewModel.fetchExpenses(showErrorSnackbar)
            if (shouldRefresh) {
                navBackStackEntry.savedStateHandle[Constants.SHOULD_REFRESH_KEY] = false
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewExpensesScreenContent(
    expenses: List<Expense>,
    goto: (RouteInfo) -> Unit,
) {
    Timber.d("ViewExpensesScreenContent Composable launched")
    Scaffold(
        topBar = {
            CenterTopAppBar(
                title = stringResource(R.string.expenses),
                onBack = { goto(RouteInfo.OnBack()) },
            )
        },
    ) { innerPadding ->
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = 4.dp,
                    end = 4.dp,
                    bottom = 4.dp,
                ),
        ) {
            val (list) = createRefs()
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(list) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        height = Dimension.fillToConstraints
                    },
            ) {
                items(expenses) { expense ->
                    ExpenseItem(
                        expense,
                        onEdit = { goto(RouteInfo.AddViewExpense(it)) },
                    )
                }
            }


        }
    }
}