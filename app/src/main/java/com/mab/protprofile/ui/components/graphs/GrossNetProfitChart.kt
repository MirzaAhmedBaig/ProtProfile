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
import com.mab.protprofile.ui.theme.GrossProfitColor
import com.mab.protprofile.ui.theme.NetProfitColor
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.Scroll
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer

@Composable
fun GrossNetProfitChart(data: List<Triple<String, Int, Int>>) {
    val labels = data.map { it.first }
    val gross = data.map { it.second }
    val net = data.map { it.third }

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(gross, net) {
        modelProducer.runTransaction {
            lineSeries {
                series(gross)
                series(net)
            }
        }
    }

    Column {
        ChartLegend(
            labels = listOf("Gross Profit", "Net Profit"),
            colors = listOf(GrossProfitColor, NetProfitColor),
        )

        CartesianChartHost(
            chart =
                rememberCartesianChart(
                    rememberLineCartesianLayer(
                        lineProvider =
                            LineCartesianLayer.LineProvider.series(
                                LineCartesianLayer.Line(
                                    fill = LineCartesianLayer.LineFill.single(fill(GrossProfitColor)),
                                ),
                                LineCartesianLayer.Line(
                                    fill = LineCartesianLayer.LineFill.single(fill(NetProfitColor)),
                                ),
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
