package com.mab.protprofile.ui.components.graphs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChartLegend(
    labels: List<String>,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    itemSpacing: Dp = 16.dp,
    circleSize: Dp = 14.dp,
    textSize: TextUnit = 14.sp,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(0.dp),
        horizontalArrangement = Arrangement.End,
    ) {
        labels.forEachIndexed { index, label ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = itemSpacing),
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(circleSize)
                            .clip(CircleShape)
                            .background(colors.getOrElse(index) { Color.Gray }),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = label,
                    fontSize = textSize,
                )
            }
        }
    }
}
