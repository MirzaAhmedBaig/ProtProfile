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
import com.mab.protprofile.ui.theme.PurchaseCashColor
import com.mab.protprofile.ui.theme.PurchaseCreditColor
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.stacked
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
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer.MergeMode

@Composable
fun CashCreditPurchasesChart(data: List<Triple<String, Int, Int>>) {
    val labels = data.map { it.first }
    val cash = data.map { it.second.toFloat() }
    val credit = data.map { it.third.toFloat() }

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(cash, credit) {
        modelProducer.runTransaction {
            columnSeries {
                series(cash)
                series(credit)
            }
        }
    }

    Column {
        ChartLegend(
            labels = listOf("Cash", "Credit"),
            colors = listOf(PurchaseCashColor, PurchaseCreditColor),
        )
        CartesianChartHost(
            chart =
                rememberCartesianChart(
                    rememberColumnCartesianLayer(
                        mergeMode = { MergeMode.stacked() },
                        columnCollectionSpacing = 4.dp,
                        columnProvider =
                            ColumnCartesianLayer.ColumnProvider.series(
                                rememberLineComponent(fill = fill(PurchaseCashColor), thickness = 15.dp),
                                rememberLineComponent(fill = fill(PurchaseCreditColor), thickness = 15.dp),
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
