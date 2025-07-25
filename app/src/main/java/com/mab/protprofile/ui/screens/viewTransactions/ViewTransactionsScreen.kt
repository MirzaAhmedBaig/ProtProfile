package com.mab.protprofile.ui.screens.viewTransactions

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import com.mab.protprofile.R
import com.mab.protprofile.data.model.ErrorMessage
import com.mab.protprofile.data.model.Transaction
import com.mab.protprofile.ui.Constants
import com.mab.protprofile.ui.components.CenterTopAppBar
import com.mab.protprofile.ui.components.TransactionItem
import com.mab.protprofile.ui.navigation.RouteInfo
import com.mab.protprofile.ui.theme.ProtProfileTheme
import kotlinx.serialization.Serializable
import timber.log.Timber

@Serializable
object ViewTransactionsRoute {
    const val SCREEN = "view_entries"
    const val TRANSACTION_ARG = "transactionsJson"
    const val DESTINATION = "$SCREEN/{$TRANSACTION_ARG}"

    fun createRoute(transactionsJson: String): String {
        return "$SCREEN/$transactionsJson"
    }
}

@Composable
fun ViewTransactionsScreen(
    transactions: List<Transaction>,
    showErrorSnackbar: (ErrorMessage) -> Unit,
    goto: (RouteInfo) -> Unit,
    navBackStackEntry: NavBackStackEntry,
    viewModel: ViewTransectionsViewModel = hiltViewModel(),
) {
    Timber.d("ViewEntriesScreen Composable launched")
    val allTransactions = viewModel.transactions.collectAsStateWithLifecycle().value ?: transactions

    // Observe refresh flag in SavedStateHandle (navigation result pattern)
    val shouldRefreshFlow =
        navBackStackEntry.savedStateHandle
            .getStateFlow(Constants.SHOULD_REFRESH_KEY, false)
    val shouldRefresh by shouldRefreshFlow.collectAsStateWithLifecycle()

    LaunchedEffect(shouldRefresh) {
        if (shouldRefresh) {
            Timber.i("Refresh triggered for ViewEntriesScreen")
            viewModel.fetchAllTransactions(showErrorSnackbar)
            // Reset the refresh flag
            navBackStackEntry.savedStateHandle[Constants.SHOULD_REFRESH_KEY] = false
        }
    }

    ViewEntriesScreenContent(
        transactions = allTransactions,
        goto = goto,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewEntriesScreenContent(
    transactions: List<Transaction>,
    goto: (RouteInfo) -> Unit,
) {
    Timber.d("ViewEntriesScreenContent Composable launched")
    Scaffold(
        topBar = {
            CenterTopAppBar(
                title = stringResource(R.string.transactions),
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
            val (list) = createRefs()
            LazyColumn(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .constrainAs(list) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            height = Dimension.fillToConstraints
                        },
            ) {
                items(transactions) { transaction ->
                    TransactionItem(
                        transaction,
                        onEdit = { goto(RouteInfo.AddViewTransaction(it)) },
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true)
fun ViewEntriesScreenPreview() {
    ProtProfileTheme(darkTheme = true) {
        Surface {
            ViewEntriesScreenContent(
                transactions = emptyList(),
                goto = {},
            )
        }
    }
}
