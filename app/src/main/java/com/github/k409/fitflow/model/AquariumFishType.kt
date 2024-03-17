package com.github.k409.fitflow.model

import androidx.annotation.DrawableRes
import com.github.k409.fitflow.R

enum class AquariumFishType(
    val id: Int,
    val title: String,
    val description: String,
    @DrawableRes val imageRes: Int,
    val price: Int
) {
    PrimaryFish(
        0,
        "Primary Fish",
        "The primary fish is the most common fish in the aquarium.",
        R.drawable.primary_fish,
        100
    ),
    SecondaryFish(
        1,
        "Secondary Fish",
        "The secondary fish is the second most common fish in the aquarium.",
        R.drawable.secondary_fish,
        200
    ),
    ThirdFish(
        2,
        "Third Fish",
        "The third fish is the third most common fish in the aquarium.",
        R.drawable.third_fish,
        300
    )
}