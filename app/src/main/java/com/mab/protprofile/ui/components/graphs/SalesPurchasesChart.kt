package com.mab.protprofile.ui.components.graphs

import android.graphics.Typeface
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
fun SalesPurchasesChart(data: List<Triple<String, Int, Int>>) {
    val labels = data.map { it.first }
    val sales = data.map { it.second }
    val purchases = data.map { it.third }

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(sales, purchases) {
        modelProducer.runTransaction {
            columnSeries {
                series(purchases)
                series(sales)
            }
        }
    }

    Column {
        ChartLegend(
            labels = listOf("Purchase", "Sale"),
            colors = listOf(PurchaseColor, SaleColor),
        )
        CartesianChartHost(
            chart =
                rememberCartesianChart(
                    rememberColumnCartesianLayer(
                        columnCollectionSpacing = 30.dp,
                        columnProvider =
                            ColumnCartesianLayer.ColumnProvider.series(
                                rememberLineComponent(fill = fill(PurchaseColor), thickness = 15.dp),
                                rememberLineComponent(fill = fill(SaleColor), thickness = 15.dp),
                            ),
                    ),
                    startAxis =
                        VerticalAxis.rememberStart(
                            title = "Rupees",
                            titleComponent =
                                rememberAxisLabelComponent(
                                    typeface = Typeface.DEFAULT_BOLD,
                                ),
                        ),
                    bottomAxis =
                        HorizontalAxis.rememberBottom(
                            title = "Months",
                            titleComponent =
                                rememberAxisLabelComponent(
                                    typeface = Typeface.DEFAULT_BOLD,
                                ),
                            valueFormatter = { a, x, b ->
                                labels.getOrNull(x.toInt()).orEmpty()
                            },
                        ),
                ),
            modelProducer = modelProducer,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(250.dp),
            scrollState = rememberVicoScrollState(initialScroll = Scroll.Absolute.End),
        )
    }
}
