package com.github.k409.fitflow.model

import com.github.k409.fitflow.R

enum class AquariumFishType(
    val id: Int,
    val title: String,
    val description: String,
    val price: Int,
    val phaseImages: Map<AquariumFishPhase, Int>
) {
    PrimaryFish(
        0,
        "Primary Fish",
        "The primary fish is the most common fish in the aquarium.",
        100,
        mapOf(
            AquariumFishPhase.Regular to R.drawable.primary_fish,
            AquariumFishPhase.Strong to R.drawable.primary_fish,
            AquariumFishPhase.Dying to R.drawable.primary_fish,
            AquariumFishPhase.Dead to R.drawable.primary_fish
        )
    ),
    SecondaryFish(
        1,
        "Secondary Fish",
        "The secondary fish is the second most common fish in the aquarium.",
        200,
        mapOf(
            AquariumFishPhase.Regular to R.drawable.secondary_fish,
            AquariumFishPhase.Strong to R.drawable.secondary_fish,
            AquariumFishPhase.Dying to R.drawable.secondary_fish,
            AquariumFishPhase.Dead to R.drawable.secondary_fish
        )
    ),
    ThirdFish(
        2,
        "Third Fish",
        "The third fish is the third most common fish in the aquarium.",
        300,
        mapOf(
            AquariumFishPhase.Regular to R.drawable.third_fish,
            AquariumFishPhase.Strong to R.drawable.third_fish,
            AquariumFishPhase.Dying to R.drawable.third_fish,
            AquariumFishPhase.Dead to R.drawable.third_fish
        )
    )
}

//secondary_fish_dead
data class AquariumFish(
    val type: AquariumFishType,
    val phase: AquariumFishPhase
) {
    fun getCurrentImageRes(): Int {
        return type.phaseImages[phase] ?: R.drawable.ic_launcher_foreground
    }
}

enum class AquariumFishPhase {
    Regular,
    Strong,
    Dying,
    Dead
}