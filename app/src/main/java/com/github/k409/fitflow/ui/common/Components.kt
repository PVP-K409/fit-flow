package com.github.k409.fitflow.ui.common

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FitFlowCircularProgressIndicator(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(bottom = 22.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.width(45.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceContainer,
            strokeWidth = 5.dp,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FancyIndicatorTabs(
    values: List<String>,
    selectedIndex: Int,
    onValueChange: (Int) -> Unit,
) {
    Column {
        ElevatedCard {
            PrimaryTabRow(
                modifier = Modifier.clip(MaterialTheme.shapes.small),
                selectedTabIndex = selectedIndex,
                divider = {},
            ) {
                values.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedIndex == index,
                        onClick = {
                            onValueChange(index)
                        },
                        text = { Text(title) },
                    )
                }
            }
        }
    }
}

@Composable
fun ProgressGraph(
    modifier: Modifier = Modifier,
    data: List<Float>,
    dataUnit: String = "",
    xAxisLabels: List<String>,
    onSelectedIndexChange: (Int) -> Unit = {},
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    gridColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
    textColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    lineWidth: Float = 4f,
    circleRadius: Float = 10f,
    textSize: TextUnit = 10.sp,
) {
    val selectedIndex = remember { mutableIntStateOf(-1) }

    val density = LocalDensity.current
    val textPaint = remember(density) {
        Paint().apply {
            this.color = textColor.toArgb()
            this.textAlign = Paint.Align.CENTER
            this.textSize = density.run { textSize.toPx() }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    // Select the nearest dot based on x-coordinate
                    val startX = 50f
                    val endX = size.width - 50f
                    val dotsStep = (endX - startX) / (data.size - 1)

                    // Find the nearest dot based on x-coordinate
                    var nearestIndex = -1
                    var smallestDifference = Float.MAX_VALUE

                    for (i in data.indices) {
                        val x = startX + dotsStep * i
                        val difference = kotlin.math.abs(offset.x - x)

                        if (difference < smallestDifference) {
                            smallestDifference = difference
                            nearestIndex = i
                        }
                    }

                    // Select the nearest dot
                    if (nearestIndex != -1) {
                        selectedIndex.intValue = nearestIndex
                        onSelectedIndexChange(nearestIndex)
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val startX = 50f
            val endX = size.width - 50f
            val startY = size.height - 50f
            val endY = 50f

            // Draw x-axis
            drawLine(
                color = gridColor,
                start = Offset(startX, startY),
                end = Offset(endX, startY),
                strokeWidth = lineWidth
            )

            // Draw y-axis
            drawLine(
                color = gridColor,
                start = Offset(startX, startY),
                end = Offset(startX, endY),
                strokeWidth = lineWidth
            )

            // Draw vertical grid lines where each data point is
            val stepVerticalLine = (endX - startX) / (data.size - 1)

            for (i in data.indices) {
                val x = startX + stepVerticalLine * i
                val color =
                    if (i == selectedIndex.intValue) selectedColor else gridColor

                drawLine(
                    color = color,
                    start = Offset(x, startY),
                    end = Offset(x, endY),
                    strokeWidth = lineWidth
                )
            }

            // Draw horizontal grid lines
            drawLine(
                color = gridColor,
                start = Offset(startX, endY),
                end = Offset(endX, endY),
                strokeWidth = lineWidth
            )

            // Draw x-axis labels from yLabels
            val xLabelStep = (endX - startX) / (xAxisLabels.size - 1)

            xAxisLabels.forEachIndexed { index, label ->
                val x = startX + xLabelStep * index - 10f
                val y = startY + 50f

                this.drawIntoCanvas {
                    it.nativeCanvas.drawText(label, x, y, textPaint)
                }
            }

            // Draw y-axis labels
            val max = data.maxOrNull() ?: 1f
            val min = data.minOrNull() ?: 0f
            val center = (max + min) / 2

            val maxLabel = "$max$dataUnit"
            val minLabel = "$min$dataUnit"
            val centerLabel = "$center$dataUnit"

            val maxLabelX = startX - 50f
            val maxLabelY = endY + 10f

            val minLabelX = startX - 50f
            val minLabelY = startY + 10f

            val centerLabelX = startX - 50f
            val centerLabelY = startY - (startY - endY) / 2

            this.drawIntoCanvas {
                it.nativeCanvas.drawText(maxLabel, maxLabelX, maxLabelY, textPaint)
                it.nativeCanvas.drawText(minLabel, minLabelX, minLabelY, textPaint)
                it.nativeCanvas.drawText(centerLabel, centerLabelX, centerLabelY, textPaint)
            }


            val dotsStep = (endX - startX) / (data.size - 1)

            // Draw lines between dots
            for (i in 0 until data.size - 1) {
                val x1 = startX + dotsStep * i
                val y1 = startY - data[i] * (startY - endY)
                val x2 = startX + dotsStep * (i + 1)
                val y2 = startY - data[i + 1] * (startY - endY)

                drawLine(
                    color = primaryColor,
                    start = Offset(x1, y1),
                    end = Offset(x2, y2),
                    strokeWidth = lineWidth
                )
            }

            // Draw area under the graph
            val path = Path()

            path.moveTo(startX, startY)
            path.lineTo(startX, startY - data[0] * (startY - endY))

            for (i in 1 until data.size) {
                path.lineTo(startX + dotsStep * i, startY - data[i] * (startY - endY))
            }

            path.lineTo(endX, startY)
            path.close()

            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        primaryColor.copy(alpha = 0.4f),
                    ),
                    startY = startY,
                    endY = endY
                )
            )

            // Draw dots
            data.forEachIndexed { index, progress ->
                val x = startX + dotsStep * index
                val y = startY - progress * (startY - endY)
                val color = if (index == selectedIndex.intValue) selectedColor else primaryColor

                if (index == selectedIndex.intValue) {
                    drawCircle(
                        color = color,
                        center = Offset(x, y),
                        radius = circleRadius * 0.5f,
                        style = Fill,
                    )
                }

                drawCircle(
                    color = color,
                    center = Offset(x, y),
                    radius = circleRadius,
                    style = Stroke(width = lineWidth),
                )
            }
        }
    }
}

@Preview
@Composable
fun LinearGraphPreview() {
    ProgressGraph(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
            .background(Color.White),
        data = listOf(0.5f, 0.3f, 0.8f, 0.6f, 0.9f, 0.0f, 0.7f, 0.5f, 0.4f, 0.7f),
        dataUnit = "%",
        xAxisLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
        primaryColor = Color(0xFF60DBB8),
        gridColor = Color(0xFF000000).copy(alpha = 0.2f),
        textColor = Color.Black.copy(alpha = 0.8f),
        selectedColor = Color(0xFF006B55),
    )
}

