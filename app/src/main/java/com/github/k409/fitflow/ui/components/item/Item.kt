package com.github.k409.fitflow.ui.components.item

import com.github.k409.fitflow.R
import com.github.k409.fitflow.model.Fish

internal data class Item(
    val name: String,
    val description: String,
    val imageResId: Int,
)

internal val mockDecorations = listOf(
    Item(
        "Plant",
        "I don't know what type of plant this is",
        R.drawable.plant,
    ),
)

internal val mockFishes = listOf(
    Item(
        Fish.Primary.title,
        Fish.Primary.description,
        Fish.Primary.getPhaseImage(0.55.toFloat()),
    ),
    Item(
        Fish.Secondary.title,
        Fish.Secondary.description,
        Fish.Secondary.getPhaseImage(0.55.toFloat()),
    ),
    Item(
        Fish.Third.title,
        Fish.Third.description,
        Fish.Third.getPhaseImage(0.80.toFloat()),
    ),
    Item(
        Fish.Third.title,
        Fish.Third.description,
        Fish.Third.getPhaseImage(0.55.toFloat()),
    ),
    Item(
        Fish.Third.title,
        Fish.Third.description,
        Fish.Third.getPhaseImage(0.20.toFloat()),
    ),
    Item(
        Fish.Third.title,
        Fish.Third.description,
        Fish.Third.getPhaseImage(0.toFloat()),
    ),
)
