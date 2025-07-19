package com.mab.protprofile.ui.utils

import com.patrykandpatrick.vico.core.cartesian.data.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.data.ExtraStore

fun getColumnProvider(positive: LineComponent, negative: LineComponent, avgValue: Double) =
    object : ColumnCartesianLayer.ColumnProvider {
        override fun getColumn(
            entry: ColumnCartesianLayerModel.Entry,
            seriesIndex: Int,
            extraStore: ExtraStore,
        ) = if (entry.y >= avgValue) positive else negative

        override fun getWidestSeriesColumn(seriesIndex: Int, extraStore: ExtraStore) = positive
    }