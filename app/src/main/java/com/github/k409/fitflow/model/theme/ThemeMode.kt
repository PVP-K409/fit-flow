package com.github.k409.fitflow.model.theme

import androidx.annotation.StringRes
import com.github.k409.fitflow.R

enum class ThemeMode(@StringRes val title: Int) {
    DARK(R.string.dark),
    LIGHT(R.string.light),
    AUTOMATIC(R.string.automatic),
}
