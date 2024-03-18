package com.github.k409.fitflow.model.theme

enum class ThemeColour(
    val title: String,
) {
    GREEN("Green"),
    PINK("Pink"),
    DYNAMIC("Dynamic"),
    AMOLED("Amoled");

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