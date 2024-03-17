package com.github.k409.fitflow.model.theme

enum class Theme(
    val title: String,
) {
    GREEN("Green"),
    PINK("Pink"),
    DYNAMIC("Dynamic"),
    AMOLED("Amoled");

    companion object {
        val darkColorThemes = listOf(
            GREEN,
            PINK,
            DYNAMIC,
            AMOLED,
        )

        val lightColorThemes = listOf(
            GREEN,
            PINK,
            DYNAMIC,
        )
    }
}