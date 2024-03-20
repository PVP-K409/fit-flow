package com.github.k409.fitflow.ui.common

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import kotlin.math.roundToInt

@Composable
fun ProgressGraph(
    modifier: Modifier = Modifier,
    data: List<Number>,
    selectedIndex: MutableIntState = remember { mutableIntStateOf(-1) },
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
//    val selectedIndex = remember { mutableIntStateOf(-1) }

    val density = LocalDensity.current
    val textPaint = remember(density) {
        Paint().apply {
            this.color = textColor.toArgb()
            this.textAlign = Paint.Align.CENTER
            this.textSize = density.run { textSize.toPx() }
        }
    }

    val maxDataPoint = data.maxOfOrNull { it.toFloat() } ?: 1f

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
                        val difference = abs(offset.x - x)

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
                textPaint = textPaint,
                data = data,
                dataUnit = dataUnit,
                endY = endY
            )


            val dotsStep = (endX - startX) / (data.size - 1)

            // Draw lines between dots
            drawLinesBetweenPoints(
                data,
                startX,
                dotsStep,
                startY,
                maxDataPoint,
                endY,
                primaryColor,
                lineWidth
            )

            drawAreaUnderGraph(
                startX = startX,
                startY = startY,
                data = data,
                maxDataPoint = maxDataPoint,
                endY = endY,
                dotsStep = dotsStep,
                endX = endX,
                primaryColor = primaryColor
            )

            // Draw dots
            drawPoints(
                data = data,
                startX = startX,
                dotsStep = dotsStep,
                startY = startY,
                maxDataPoint = maxDataPoint,
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
    dotsStep: Float,
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
        val x = startX + dotsStep * index
        val y = startY - (progress.toFloat() / maxDataPoint) * (startY - endY)

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

private fun DrawScope.drawAreaUnderGraph(
    startX: Float,
    startY: Float,
    data: List<Number>,
    maxDataPoint: Float,
    endY: Float,
    dotsStep: Float,
    endX: Float,
    primaryColor: Color
) {
    // Draw area under the graph
    val path = Path()

    path.moveTo(startX, startY)
    path.lineTo(startX, startY - (data[0].toFloat() / maxDataPoint) * (startY - endY))

    for (i in 1 until data.size) {
        path.lineTo(
            startX + dotsStep * i,
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
    dotsStep: Float,
    startY: Float,
    maxDataPoint: Float,
    endY: Float,
    primaryColor: Color,
    lineWidth: Float
) {
    for (i in 0 until data.size - 1) {
        val x1 = startX + dotsStep * i
        val y1 = startY - (data[i].toFloat() / maxDataPoint) * (startY - endY)

        val x2 = startX + dotsStep * (i + 1)
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
    textPaint: Paint,
    data: List<Number>,
    dataUnit: String,
    endY: Float
) {
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
    val max = data.maxOfOrNull { it.toFloat() } ?: 1f
    val min = data.minOfOrNull { it.toFloat() } ?: 0f
    val center = (max + min) / 2

    val maxLabel = "${max.roundToInt()}$dataUnit"
    val minLabel = "${min.roundToInt()}$dataUnit"
    val centerLabel = "${center.roundToInt()}$dataUnit"

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
