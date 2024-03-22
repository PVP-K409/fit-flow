package com.github.k409.fitflow.ui.common

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
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
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun ProgressGraph(
    modifier: Modifier = Modifier,
    data: List<Number>,
    selected: Int = -1,
    selectedIndex: MutableIntState = remember { mutableIntStateOf(selected) },
    dataUnit: String = "",
    xAxisLabels: List<String> = emptyList(),
    onSelectedIndexChange: (Int) -> Unit = {},
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    gridColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
    textColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    lineWidth: Float = 4f,
    circleRadius: Float = 10f,
    textSize: TextUnit = 10.sp,
) {
    if (data.isEmpty()) return

    val density = LocalDensity.current
    val textPaintXAxis = remember(density) {
        Paint().apply {
            this.color = textColor.toArgb()
            this.textAlign = Paint.Align.CENTER
            this.textSize = density.run { textSize.toPx() }
        }
    }
    val textPaintYAxis = remember(density) {
        Paint().apply {
            this.color = textColor.toArgb()
            this.textAlign = Paint.Align.RIGHT
            this.textSize = density.run { textSize.toPx() }
        }
    }

    var minDataValue = data.minOfOrNull { it.toFloat() } ?: 0f
    var maxDataValue = data.maxOfOrNull { it.toFloat() } ?: 1f

    if (minDataValue == maxDataValue) {
        minDataValue = 0f
        maxDataValue = 1f
    }

    val maxXAxisLabelWidth = xAxisLabels.maxOfOrNull { textPaintXAxis.measureText(it) } ?: 0f
    val maxYAxisLabelWidth =
        textPaintYAxis.measureText("${data.maxOfOrNull { it.toFloat() } ?: 1f}$dataUnit")

    val padding = 10f

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    // Calculate the width of the longest label on the x-axis
                    val startX = max(maxYAxisLabelWidth, maxXAxisLabelWidth) + padding
                    val endX = size.width - padding * 2

                    val pointsStep = (endX - startX) / (data.size - 1)

                    // Find the nearest point based on x-coordinate
                    var nearestIndex = -1
                    var smallestDifference = Float.MAX_VALUE

                    for (i in data.indices) {
                        val x = startX + pointsStep * i
                        val difference = abs(offset.x - x)

                        if (difference < smallestDifference) {
                            smallestDifference = difference
                            nearestIndex = i
                        }
                    }

                    // Select the nearest point
                    if (nearestIndex != -1) {
                        selectedIndex.intValue = nearestIndex
                        onSelectedIndexChange(nearestIndex)
                    }
                }
            },
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val startX = max(maxYAxisLabelWidth, maxXAxisLabelWidth) + padding
            val endX = size.width - padding * 2

            val startY = size.height - textPaintXAxis.textSize - padding * 2 - 50f
            val endY = 50f

            val pointsStep = (endX - startX) / (data.size - 1)

            drawGridLines(
                gridColor = gridColor,
                startX = startX,
                startY = startY,
                endX = endX,
                lineWidth = lineWidth,
                endY = endY,
                data = data,
                selectedIndex = selectedIndex,
                selectedColor = selectedColor
            )

            drawLabels(
                endX = endX,
                startX = startX,
                xAxisLabels = xAxisLabels,
                startY = startY,
                textPaintXAxis = textPaintXAxis,
                textPaintYAxis = textPaintYAxis,
                maxDataValue = maxDataValue,
                minDataValue = minDataValue,
                dataUnit = dataUnit,
                endY = endY
            )

            drawLinesBetweenPoints(
                data,
                startX,
                pointsStep,
                startY,
                maxDataValue,
                endY,
                primaryColor,
                lineWidth
            )

            drawAreaUnderGraph(
                startX = startX,
                startY = startY,
                data = data,
                maxDataPoint = maxDataValue,
                endY = endY,
                pointsStep = pointsStep,
                endX = endX,
                primaryColor = primaryColor
            )

            drawPoints(
                data = data,
                startX = startX,
                pointsStep = pointsStep,
                startY = startY,
                maxDataPoint = maxDataValue,
                endY = endY,
                selectedIndex = selectedIndex,
                selectedColor = selectedColor,
                primaryColor = primaryColor,
                circleRadius = circleRadius,
                lineWidth = lineWidth
            )
        }
    }
}

private fun DrawScope.drawPoints(
    data: List<Number>,
    startX: Float,
    pointsStep: Float,
    startY: Float,
    maxDataPoint: Float,
    endY: Float,
    selectedIndex: MutableIntState,
    selectedColor: Color,
    primaryColor: Color,
    circleRadius: Float,
    lineWidth: Float
) {
    data.forEachIndexed { index, progress ->
        val x = startX + pointsStep * index
        val y = startY - (progress.toFloat() / maxDataPoint) * (startY - endY)

        val color = if (index == selectedIndex.intValue) selectedColor else primaryColor

        if (index == selectedIndex.intValue) {
            drawCircle(
                color = color,
                center = Offset(x, y),
                radius = circleRadius * 2f,
                style = Fill,
                alpha = 0.5f
            )
        }

        drawCircle(
            color = color,
            center = Offset(x, y),
            radius = circleRadius,
            style = if (index == selectedIndex.intValue) Fill else Stroke(width = lineWidth)
        )
    }
}

private fun DrawScope.drawAreaUnderGraph(
    startX: Float,
    startY: Float,
    data: List<Number>,
    maxDataPoint: Float,
    endY: Float,
    pointsStep: Float,
    endX: Float,
    primaryColor: Color
) {
    // Draw area under the graph
    val path = Path()

    path.moveTo(startX, startY)
    path.lineTo(startX, startY - (data[0].toFloat() / maxDataPoint) * (startY - endY))

    for (i in 1 until data.size) {
        path.lineTo(
            startX + pointsStep * i,
            startY - (data[i].toFloat() / maxDataPoint) * (startY - endY)
        )
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
}

private fun DrawScope.drawLinesBetweenPoints(
    data: List<Number>,
    startX: Float,
    pointsStep: Float,
    startY: Float,
    maxDataPoint: Float,
    endY: Float,
    primaryColor: Color,
    lineWidth: Float
) {
    for (i in 0 until data.size - 1) {
        val x1 = startX + pointsStep * i
        val y1 = startY - (data[i].toFloat() / maxDataPoint) * (startY - endY)

        val x2 = startX + pointsStep * (i + 1)
        val y2 = startY - (data[i + 1].toFloat() / maxDataPoint) * (startY - endY)

        drawLine(
            color = primaryColor,
            start = Offset(x1, y1),
            end = Offset(x2, y2),
            strokeWidth = lineWidth
        )
    }
}

private fun DrawScope.drawLabels(
    endX: Float,
    startX: Float,
    xAxisLabels: List<String>,
    startY: Float,
    textPaintXAxis: Paint,
    textPaintYAxis: Paint,
    maxDataValue: Float,
    minDataValue: Float,
    dataUnit: String,
    endY: Float
) {
    // Draw x-axis labels from yLabels
    val xLabelStep = (endX - startX) / (xAxisLabels.size - 1)

    xAxisLabels.forEachIndexed { index, label ->
        val x = startX + xLabelStep * index - 10f
        val y = startY + 60f

        val textWidth = textPaintXAxis.measureText(label)

        // Labels alignment
        when (index) {
            0 -> {
                this.drawIntoCanvas {
                    it.nativeCanvas.drawText(label, x + textWidth / 2, y, textPaintXAxis)
                }
            }

            xAxisLabels.size - 1 -> {
                this.drawIntoCanvas {
                    it.nativeCanvas.drawText(label, x - textWidth / 2, y, textPaintXAxis)
                }
            }

            else -> {
                this.drawIntoCanvas {
                    it.nativeCanvas.drawText(label, x, y, textPaintXAxis)
                }
            }
        }
    }

    // Draw y-axis labels
    val center = (maxDataValue + minDataValue) / 2

    val maxLabel = "${maxDataValue.roundToInt()}$dataUnit"
    val minLabel = "${minDataValue.roundToInt()}$dataUnit"
    val centerLabel = "${center.roundToInt()}$dataUnit"

    val spaceFromAxis = 30f

    val maxLabelX = startX - spaceFromAxis
    val maxLabelY = endY + textPaintYAxis.textSize

    val minLabelX = startX - spaceFromAxis

    val centerLabelX = startX - spaceFromAxis
    val centerLabelY = startY - (startY - endY) / 2

    this.drawIntoCanvas {
        it.nativeCanvas.drawText(maxLabel, maxLabelX, maxLabelY, textPaintYAxis)
        it.nativeCanvas.drawText(minLabel, minLabelX, startY, textPaintYAxis)
        it.nativeCanvas.drawText(centerLabel, centerLabelX, centerLabelY, textPaintYAxis)
    }
}

private fun DrawScope.drawGridLines(
    gridColor: Color,
    startX: Float,
    startY: Float,
    endX: Float,
    lineWidth: Float,
    endY: Float,
    data: List<Number>,
    selectedIndex: MutableIntState,
    selectedColor: Color
) {
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
