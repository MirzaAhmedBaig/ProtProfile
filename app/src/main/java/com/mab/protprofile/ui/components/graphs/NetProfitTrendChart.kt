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
import com.mab.protprofile.ui.theme.BadColor
import com.mab.protprofile.ui.theme.GoodColor
import com.mab.protprofile.ui.utils.getColumnProvider
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

@Composable
fun NetProfitTrendChart(data: List<Pair<String, Int>>) {
    val profits = data.map { it.second.toFloat() }
    val labels = data.map { it.first }
    val avgProfit = profits.average()

    val modelProducer = remember { CartesianChartModelProducer() }

    val positiveColumn =
        rememberLineComponent(
            fill = fill(GoodColor),
            thickness = 15.dp,
        )
    val negativeColumn =
        rememberLineComponent(
            fill = fill(BadColor),
            thickness = 15.dp,
        )

    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            columnSeries { series(profits) }
        }
    }

    Column {
        ChartLegend(
            labels = listOf("Good Month", "Bad Month"),
            colors = listOf(GoodColor, BadColor),
        )
        CartesianChartHost(
            chart =
                rememberCartesianChart(
                    rememberColumnCartesianLayer(
                        columnProvider =
                            remember(positiveColumn, negativeColumn) {
                                getColumnProvider(positiveColumn, negativeColumn, avgProfit)
                            },
                        columnCollectionSpacing = 4.dp,
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
                    decorations = listOf(rememberAvgHorizontalLine(avgProfit, "Avg Profit")),
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
