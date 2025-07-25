package com.mab.protprofile.ui.screens.investments

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mab.protprofile.R
import com.mab.protprofile.data.model.ErrorMessage
import com.mab.protprofile.ui.components.CenterTopAppBar
import com.mab.protprofile.ui.components.FinanceCard
import com.mab.protprofile.ui.components.LoadingIndicator
import com.mab.protprofile.ui.data.Partner
import com.mab.protprofile.ui.ext.capitalizeWords
import com.mab.protprofile.ui.navigation.RouteInfo
import com.mab.protprofile.ui.theme.GoodColor
import com.mab.protprofile.ui.theme.TotalInvestment
import com.mab.protprofile.ui.theme.YourInvestment
import com.mab.protprofile.ui.theme.YourProfitShare
import kotlinx.serialization.Serializable
import timber.log.Timber

@Serializable
data class InvestmentSummaryRoute(val totalProfit: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestmentSummaryScreen(
    goto: (RouteInfo) -> Unit,
    showErrorSnackbar: (ErrorMessage) -> Unit,
    viewModel: InvestmentSummaryViewModel = hiltViewModel(),
) {
    Timber.d("InvestmentSummaryScreen initiated")
    val investmentDetails by viewModel.investmentDetails.collectAsStateWithLifecycle()
    if (investmentDetails == null) {
        LoadingIndicator()
    } else {
        val data = investmentDetails!!
        Scaffold(
            topBar = {
                CenterTopAppBar(
                    title = stringResource(R.string.investment_summary),
                    onBack = { goto(RouteInfo.OnBack()) },
                )
            },
        ) { innerPadding ->
            ConstraintLayout(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
            ) {
                val (body) = createRefs()

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
                ) {
                    TotalInvestmentCard(data.totalInvestment, data.totalProfit)
                    MyInvestmentSection(
                        data.yourInvestment,
                        data.yourProfitShare,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ExpandableAssetsSection(assets = data.assets)
                    Spacer(modifier = Modifier.height(8.dp))
                    PartnersSection(partners = data.partners)
                }
            }
        }
    }
    LaunchedEffect(true) {
        Timber.d("InvestmentSummaryScreen: LaunchedEffect triggered to load data")
        viewModel.loadData(showErrorSnackbar)
    }
}

@Composable
fun TotalInvestmentCard(totalInvestment: Int, totalProfit: Int) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        FinanceCard(
            modifier =
                Modifier
                    .weight(1f),
            title = "Total Investment",
            amount = "₹$totalInvestment",
            valueColor = TotalInvestment,
        )
        FinanceCard(
            modifier =
                Modifier
                    .weight(1f),
            title = "Total Profit",
            amount = "₹$totalProfit",
            valueColor = GoodColor,
        )
    }
}

@Composable
fun MyInvestmentSection(
    myInvestment: Int,
    myShare: Int,
) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        FinanceCard(
            modifier =
                Modifier
                    .weight(1f),
            title = "Your Investment",
            amount = "₹$myInvestment",
            valueColor = YourInvestment,
        )
        FinanceCard(
            modifier =
                Modifier
                    .weight(1f),
            title = "Your Profit Share",
            amount = "₹$myShare%",
            valueColor = YourProfitShare,
        )
    }
}

@Composable
fun ExpandableAssetsSection(assets: Map<String, Int>) {
    var expanded by remember { mutableStateOf(false) }
    val totalCost = assets.values.sum()

    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(8.dp),
        modifier =
            Modifier
                .clickable { expanded = !expanded }
                .fillMaxWidth()
                .animateContentSize()
                .padding(4.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Assets & Costs",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (expanded) "Show less" else "Show more",
                )
            }
            Spacer(Modifier.height(12.dp))
            // Show Total Cost in both collapsed and expanded states
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "Total Cost",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    "₹$totalCost",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            if (expanded) {
                Spacer(Modifier.height(8.dp))
                if (assets.isEmpty()) {
                    Text("No assets available")
                } else {
                    assets.entries.forEachIndexed { index, asset ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                asset.key.replace("_", " ").capitalizeWords(),
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            Text(
                                "₹${asset.value}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                        if (index < assets.size - 1) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PartnersSection(partners: List<Partner>) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(8.dp),
        modifier =
            Modifier
                .clickable { expanded = !expanded }
                .fillMaxWidth()
                .animateContentSize()
                .padding(4.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Partners",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (expanded) "Show less" else "Show more",
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (expanded) {
                // Expanded: show full details for each partner
                partners.forEachIndexed { index, partner ->
                    Column {
                        Text(
                            text = partner.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column {
                                Text("Invested Amount", style = MaterialTheme.typography.labelSmall)
                                Text(
                                    "₹${partner.investment}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = YourInvestment,
                                )
                            }
                            Column(
                                horizontalAlignment = Alignment.End,
                            ) {
                                Text("Profit Share", style = MaterialTheme.typography.labelSmall)
                                Text(
                                    "${partner.profitShare}%",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = YourProfitShare,
                                )
                            }
                        }
                        if (index < partners.size - 1) {
                            Spacer(Modifier.height(12.dp))
                            HorizontalDivider()
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                }
            } else {
                Text(
                    text = partners.joinToString(", ") { it.name },
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
