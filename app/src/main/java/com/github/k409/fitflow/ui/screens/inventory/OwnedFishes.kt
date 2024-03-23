package com.github.k409.fitflow.ui.screens.inventory

import com.github.k409.fitflow.model.Fish

val ownedFishes = listOf(
    Item(
        Fish.Primary.title,
        Fish.Primary.description,
        Fish.Primary.getPhaseImage(0.55.toFloat())
    ),
    Item(
        Fish.Secondary.title,
        Fish.Secondary.description,
        Fish.Secondary.getPhaseImage(0.55.toFloat())
    ),
    Item(
        Fish.Third.title,
        Fish.Third.description,
        Fish.Third.getPhaseImage(0.80.toFloat())
    ),
    Item(
        Fish.Third.title,
        Fish.Third.description,
        Fish.Third.getPhaseImage(0.55.toFloat())
    ),
    Item(
        Fish.Third.title,
        Fish.Third.description,
        Fish.Third.getPhaseImage(0.20.toFloat())
    ),
    Item(
        Fish.Third.title,
        Fish.Third.description,
        Fish.Third.getPhaseImage(0.toFloat())
    ),
)