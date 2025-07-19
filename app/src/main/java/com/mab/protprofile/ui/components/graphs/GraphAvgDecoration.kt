package com.mab.protprofile.ui.components.graphs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.component.shapeComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.insets
import com.patrykandpatrick.vico.compose.common.shape.rounded
import com.patrykandpatrick.vico.core.cartesian.decoration.HorizontalLine
import com.patrykandpatrick.vico.core.common.Position
import com.patrykandpatrick.vico.core.common.shape.CorneredShape

@Composable
fun rememberAvgHorizontalLine(
    avgValue: Double,
    label: String,
): HorizontalLine {
    val fill = fill(Color(0xfffdc8c4))
    val line = rememberLineComponent(fill = fill, thickness = 2.dp)
    val labelComponent =
        rememberTextComponent(
            margins = insets(start = 6.dp),
            padding = insets(start = 8.dp, end = 8.dp, bottom = 2.dp),
            background =
                shapeComponent(fill, CorneredShape.rounded(bottomLeft = 4.dp, bottomRight = 4.dp)),
        )
    return remember {
        HorizontalLine(
            y = { avgValue },
            line = line,
            labelComponent = labelComponent,
            label = { label },
            horizontalLabelPosition = Position.Horizontal.End,
        )
    }
}
