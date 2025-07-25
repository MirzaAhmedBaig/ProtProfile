package com.mab.protprofile.ui.screens.transactionsHistory

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.mab.protprofile.R
import com.mab.protprofile.data.model.ErrorMessage
import com.mab.protprofile.data.model.TransactionHistory
import com.mab.protprofile.ui.components.CenterTopAppBar
import com.mab.protprofile.ui.components.LoadingIndicator
import com.mab.protprofile.ui.components.TransactionHistoryItem
import com.mab.protprofile.ui.navigation.RouteInfo
import com.mab.protprofile.ui.theme.ProtProfileTheme
import kotlinx.serialization.Serializable
import timber.log.Timber

@Serializable
object HistoryRoute

@Composable
fun HistoryScreen(
    goto: (RouteInfo) -> Unit,
    showErrorSnackbar: (ErrorMessage) -> Unit,
    viewModel: HistoryViewModel = hiltViewModel(),
) {
    Timber.d("HistoryScreen composable launched")
    val transactionsHistory by viewModel.transactionsHistory.collectAsStateWithLifecycle()

    if (transactionsHistory == null) {
        LoadingIndicator()
    } else {
        HistoryScreenContent(
            goto = goto,
            transactionsHistory = transactionsHistory!!,
        )
    }
    LaunchedEffect(true) {
        viewModel.fetchAllHistory(showErrorSnackbar)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreenContent(
    goto: (RouteInfo) -> Unit,
    transactionsHistory: Map<String, List<TransactionHistory>>,
) {
    Scaffold(
        topBar = {
            CenterTopAppBar(
                title = stringResource(R.string.history),
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
            val (list, noData) = createRefs()
            if (transactionsHistory.isEmpty()) {
                Text(
                    text = "No Data Found",
                    style = MaterialTheme.typography.titleLarge,
                    modifier =
                        Modifier.constrainAs(noData) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                )
            } else {
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
                    transactionsHistory.forEach { (transId, historyList) ->
                        item {
                            TransactionHistoryItem(transId, historyList)
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true)
fun HistoryScreenPreview() {
    ProtProfileTheme(darkTheme = true) {
        HistoryScreenContent(
            goto = {},
            transactionsHistory = mapOf(),
        )
    }
}
