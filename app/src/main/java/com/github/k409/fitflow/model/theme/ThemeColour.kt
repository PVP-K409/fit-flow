package com.github.k409.fitflow.model.theme

import androidx.annotation.StringRes
import com.github.k409.fitflow.R

enum class ThemeColour(
    @StringRes val title: Int,
) {
    GREEN(R.string.green),
    PINK(R.string.pink),
    DYNAMIC(R.string.dynamic),
    AMOLED(R.string.amoled),
    ;

    companion object {
        val darkColours = listOf(
            GREEN,
            PINK,
            DYNAMIC,
            AMOLED,
        )

        val lightColours = listOf(
            GREEN,
            PINK,
            DYNAMIC,
        )
    }
}
