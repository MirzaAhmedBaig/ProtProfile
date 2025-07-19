package com.mab.protprofile.ui.components.graphs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mab.protprofile.R
import com.mab.protprofile.ui.theme.BadColor
import com.mab.protprofile.ui.theme.GoodColor


@Composable
fun HighlightsSection(highlights: List<Pair<String, Int>>) {
    val bestMonth = highlights.first()
    val worstMonth = highlights.last()
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        Card(
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .weight(1f)
                .padding(4.dp)
        ) {
            Row(modifier = Modifier.padding(10.dp)) {
                Image(
                    painter = painterResource(R.drawable.good),
                    contentDescription = "Best Month"
                )
                Column(Modifier.padding(start = 8.dp)) {
                    Text("Best Month", style = MaterialTheme.typography.bodyLarge)
                    Text(bestMonth.first, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "₹${bestMonth.second}",
                        style = MaterialTheme.typography.titleLarge,
                        color = GoodColor
                    )
                }
            }
        }

        Card(
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .weight(1f)
                .padding(4.dp)
        ) {
            Row(modifier = Modifier.padding(10.dp)) {
                Image(
                    painter = painterResource(R.drawable.bad),
                    contentDescription = "Worst Month"

                )
                Column(Modifier.padding(start = 8.dp)) {
                    Text("Worst Month", style = MaterialTheme.typography.bodyLarge)
                    Text(worstMonth.first, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "₹${worstMonth.second}",
                        style = MaterialTheme.typography.titleLarge,
                        color = BadColor
                    )
                }
            }
        }
    }
}
