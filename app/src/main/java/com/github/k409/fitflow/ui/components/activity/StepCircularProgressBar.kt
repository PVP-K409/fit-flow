package com.github.k409.fitflow.ui.components.activity

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.k409.fitflow.R
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CircularProgressBar(
    taken: Long,
    goal: Long,
    fontSize: TextUnit = 26.sp,
    radius: Dp = 100.dp,
    color: Color = MaterialTheme.colorScheme.primary,
    strokeWidth: Dp = 20.dp,
    animDuration: Int = 1000,
    animDelay: Int = 0,
) {
    var animationPlayed by remember {
        mutableStateOf(false)
    }
    val percentage: Float = if (taken.toFloat() >= goal) {
        1.0f
    } else {
        (taken.toFloat() / goal)
    }

    val curPercentage = animateFloatAsState(
        targetValue = if (animationPlayed) percentage else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = animDelay,
        ),
        label = "",
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(radius * 2f),
        ) {
            Canvas(Modifier.size(radius * 2f)) {
                val lineColor = color.copy(alpha = 0.1f)
                val lineWidth = 3.dp.toPx()
                val lineLength = 6.dp.toPx()
                val linesCount = 15
                val sweepAnglePerLine = 290f / (linesCount - 1)
                val radiusInPx = radius.toPx()

                for (i in 0 until linesCount) {
                    val angle = sweepAnglePerLine * i - 235f
                    val angleInRadians = Math.toRadians(angle.toDouble()).toFloat()

                    val innerRadius = radiusInPx - lineLength - strokeWidth.toPx()
                    val outerRadius = radiusInPx - strokeWidth.toPx()
                    val startX = radiusInPx + innerRadius * cos(angleInRadians)
                    val startY = radiusInPx + innerRadius * sin(angleInRadians)
                    val endX = radiusInPx + outerRadius * cos(angleInRadians)
                    val endY = radiusInPx + outerRadius * sin(angleInRadians)

                    drawLine(
                        color = lineColor,
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = lineWidth,
                    )
                }

                drawArc(
                    color = color.copy(alpha = 0.1f),
                    startAngle = 125f,
                    sweepAngle = 290f,
                    useCenter = false,
                    style = Stroke((strokeWidth).toPx(), cap = StrokeCap.Round),
                )
                drawArc(
                    color = color,
                    startAngle = 125f,
                    sweepAngle = 290 * curPercentage.value,
                    useCenter = false,
                    style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round),
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.footprint_24px),
                    contentDescription = stringResource(R.string.steps_icon),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(36.dp),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$taken",
                    fontSize = fontSize / 2 * 3,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "/$goal",
                    color = color.copy(alpha = 0.6f),
                    fontSize = fontSize / 3 * 2,
                )
            }
        }
    }
}
