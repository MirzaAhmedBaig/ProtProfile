package com.mab.protprofile.ui.components.graphs

import android.graphics.Typeface
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mab.protprofile.ui.data.OverviewData
import com.mab.protprofile.ui.theme.ExpenseColor
import com.mab.protprofile.ui.theme.NetProfitColor
import com.mab.protprofile.ui.theme.PurchaseColor
import com.mab.protprofile.ui.theme.SaleColor
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.Scroll
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer

@Composable
fun CumulativeOverviewChart(overviewList: List<OverviewData>) {

    val labels = overviewList.map { it.monthYear }
    val sales = overviewList.map { it.cumulativeSale.toFloat() }
    val purchases = overviewList.map { it.cumulativePurchase.toFloat() }
    val expenses = overviewList.map { it.cumulativeExpense.toFloat() }
    val profits = overviewList.map { it.cumulativeProfit.toFloat() }

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(sales, purchases, expenses, profits) {
        modelProducer.runTransaction {
            columnSeries {
                series(sales)
                series(purchases)
                series(expenses)
                series(profits)
            }
        }
    }

    Column {
        ChartLegend(
            labels = listOf("Purchase", "Sale", "Expense", "Net Profit"),
            colors = listOf(PurchaseColor, SaleColor, ExpenseColor, NetProfitColor)
        )
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberColumnCartesianLayer(
                    columnCollectionSpacing = 35.dp,
                    columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                        rememberLineComponent(fill = fill(PurchaseColor), thickness = 15.dp),
                        rememberLineComponent(fill = fill(SaleColor), thickness = 15.dp),
                        rememberLineComponent(fill = fill(ExpenseColor), thickness = 15.dp),
                        rememberLineComponent(fill = fill(NetProfitColor), thickness = 15.dp),
                    )

                ),
                startAxis = VerticalAxis.rememberStart(
                    title = "Rupees",
                    titleComponent = rememberAxisLabelComponent(
                        typeface = Typeface.DEFAULT_BOLD
                    )
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    title = "Months",
                    titleComponent = rememberAxisLabelComponent(
                        typeface = Typeface.DEFAULT_BOLD
                    ),
                    valueFormatter = { a, x, b ->
                        labels.getOrNull(x.toInt()).orEmpty()
                    }
                )
            ),
            modelProducer = modelProducer,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            scrollState = rememberVicoScrollState(initialScroll = Scroll.Absolute.End),
        )
    }


}
