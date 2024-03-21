package com.github.k409.fitflow.model

import com.github.k409.fitflow.R
import com.google.firebase.firestore.Exclude

enum class FishType(
    val id: Int,
    val title: String,
    val description: String,
    val price: Int,
    val phaseImages: Map<FishPhase, Int>
) {
    PrimaryFish(
        0,
        "Primary Fish",
        "The primary fish is the most common fish in the aquarium.",
        100,
        mapOf(
            FishPhase.Regular to R.drawable.primary_fish,
            FishPhase.Strong to R.drawable.primary_fish,
            FishPhase.Dying to R.drawable.primary_fish,
            FishPhase.Dead to R.drawable.primary_fish
        )
    ),
    SecondaryFish(
        1,
        "Secondary Fish",
        "The secondary fish is the second most common fish in the aquarium.",
        200,
        mapOf(
            FishPhase.Regular to R.drawable.secondary_fish,
            FishPhase.Strong to R.drawable.secondary_fish,
            FishPhase.Dying to R.drawable.secondary_fish,
            FishPhase.Dead to R.drawable.secondary_fish
        )
    ),
    ThirdFish(
        2,
        "Third Fish",
        "The third fish is the third most common fish in the aquarium.",
        300,
        mapOf(
            FishPhase.Regular to R.drawable.third_fish_regular,
            FishPhase.Strong to R.drawable.third_fish_strong,
            FishPhase.Dying to R.drawable.third_fish_dying,
            FishPhase.Dead to R.drawable.third_fish_dead
        )
    );


    @Exclude
    fun getPhaseImage(healthLevel: Float): Int {
        return this.phaseImages[FishPhase.getPhase(healthLevel)]
            ?: throw IllegalStateException(
                "Image not found for phase: ${
                    FishPhase.getPhase(
                        healthLevel
                    )
                }, type: $this"
            )
    }

}

enum class FishPhase {
    Regular,
    Strong,
    Dying,
    Dead;

    companion object {
        fun getPhase(healthLevel: Float): FishPhase {
            return when {
                healthLevel >= 0.8 -> Strong
                healthLevel >= 0.55 -> Regular
                healthLevel >= 0.20 -> Dying
                else -> Dead
            }
        }
    }
}