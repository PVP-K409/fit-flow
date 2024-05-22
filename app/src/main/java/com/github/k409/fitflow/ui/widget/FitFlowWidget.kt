package com.github.k409.fitflow.ui.widget

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.components.TitleBar
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.github.k409.fitflow.R

class FitFlowWidget : GlanceAppWidget() {

    companion object {
        private val thinMode = DpSize(120.dp, 120.dp)
        private val smallMode = DpSize(184.dp, 184.dp)
        private val mediumMode = DpSize(260.dp, 200.dp)
        private val largeMode = DpSize(260.dp, 280.dp)
    }

    override val stateDefinition = WidgetInfoDefinition

    override val sizeMode: SizeMode = SizeMode.Responsive(
        setOf(thinMode, smallMode, mediumMode, largeMode)
    )

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        provideContent {
            Content(
                widgetInfo = currentState<WidgetInfo>()
            )
        }
    }

    @Composable
    private fun Content(
        widgetInfo: WidgetInfo
    ) {
        GlanceTheme {
            Scaffold(
                backgroundColor = GlanceTheme.colors.widgetBackground,
                titleBar = {
                    TitleBar(
                        startIcon = ImageProvider(R.drawable.ic_launcher_foreground),
                        title = LocalContext.current.getString(R.string.app_name),
                        actions = {
                            CircleIconButton(
                                imageProvider = ImageProvider(R.drawable.refresh_24px),
                                contentDescription = null,
                                onClick = actionRunCallback(RefreshAction::class.java),
                                backgroundColor = null,
                            )
                        }
                    )
                }
            ) {
                when (widgetInfo) {
                    WidgetInfo.Loading -> {
                        AppWidgetBox(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = GlanceTheme.colors.primary)
                        }
                    }

                    is WidgetInfo.Available -> {
                        WidgetAvailableContainer(widgetInfo = widgetInfo)
                    }

                    is WidgetInfo.Unavailable -> {
                        AppWidgetColumn(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                stringResource(id = R.string.data_not_available),
                                style = TextStyle(
                                    color = GlanceTheme.colors.onSurface,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WidgetAvailableContainer(
    modifier: GlanceModifier = GlanceModifier,
    widgetInfo: WidgetInfo.Available
) {
    val waterPercent = (widgetInfo.waterLevel * 100).toInt()
    val healthPercent = (widgetInfo.healthLevel * 100).toInt()

    LazyColumn(
        modifier = GlanceModifier
            .then(modifier)
    ) {
        item {
            Column {
                MetricRow(
                    iconId = R.drawable.water_drop_24px,
                    startText = stringResource(R.string.water_level),
                    endText = "$waterPercent%",
                    colorFilter = ColorFilter.tint(ColorProvider(Color(0xFF2196F3)))
                )

                Spacer(modifier = GlanceModifier.height(8.dp))

                MetricRow(
                    iconId = R.drawable.ecg_heart_24px,
                    startText = stringResource(R.string.health_level),
                    endText = "$healthPercent%",
                    colorFilter = ColorFilter.tint(ColorProvider(Color(0xFFF44336)))
                )

                Spacer(modifier = GlanceModifier.height(8.dp))
            }
        }

        item {
            Column {
                MetricRow(
                    iconId = R.drawable.walk,
                    startText = stringResource(R.string.steps),
                    endText = "${widgetInfo.steps}",
                    colorFilter = ColorFilter.tint(GlanceTheme.colors.onBackground)
                )

                Spacer(modifier = GlanceModifier.height(8.dp))

                MetricRow(
                    iconId = R.drawable.walk,
                    startText = stringResource(R.string.calories),
                    endText = "${widgetInfo.calories}",
                    colorFilter = ColorFilter.tint(GlanceTheme.colors.onBackground)
                )

                Spacer(modifier = GlanceModifier.height(8.dp))

                MetricRow(
                    iconId = R.drawable.walk,
                    startText = stringResource(R.string.distance),
                    endText = "${widgetInfo.distance} km",
                    colorFilter = ColorFilter.tint(GlanceTheme.colors.onBackground)
                )

                Spacer(modifier = GlanceModifier.height(8.dp))

                MetricRow(
                    iconId = R.drawable.water_drop_24px,
                    startText = stringResource(R.string.total_hydration),
                    endText = "${widgetInfo.hydration} ml",
                    colorFilter = ColorFilter.tint(GlanceTheme.colors.onBackground)
                )

                Spacer(modifier = GlanceModifier.height(8.dp))
            }
        }

        item {
            Text(
                modifier = GlanceModifier.fillMaxWidth(),
                text = stringResource(R.string.last_updated) + ": ${widgetInfo.lastUpdated}",
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = GlanceTheme.colors.onBackground,
                    fontSize = 12.sp,
                )
            )
        }
    }
}

@Composable
private fun MetricRow(
    @DrawableRes iconId: Int,
    startText: String,
    endText: String,
    colorFilter: ColorFilter? = null,
) {
    Row(modifier = GlanceModifier.fillMaxWidth()) {
        Image(
            provider = ImageProvider(iconId),
            contentDescription = null,
            modifier = GlanceModifier.size(24.dp),
            colorFilter = colorFilter
        )

        Spacer(modifier = GlanceModifier.width(8.dp))

        Text(
            text = startText,
            maxLines = 1,
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        )

        Box(
            modifier = GlanceModifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = endText,
                maxLines = 1,
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}