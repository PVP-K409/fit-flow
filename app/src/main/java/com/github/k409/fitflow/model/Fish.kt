package com.github.k409.fitflow.model

import com.github.k409.fitflow.R
import com.google.firebase.firestore.Exclude

enum class Fish(
    val id: Int,
    val title: String,
    val description: String,
    val price: Int,
    val phases: Map<FishPhase, Int>,
) {
    Primary(
        0,
        "Primary Fish",
        "The primary fish is the most common fish in the aquarium.",
        100,
        mapOf(
            FishPhase.Regular to R.drawable.clown_fish_regular,
            FishPhase.Strong to R.drawable.clown_fish_strong,
            FishPhase.Dying to R.drawable.clown_fish_dying,
            FishPhase.Dead to R.drawable.clown_fish_dead,
        ),
    ),
    Secondary(
        1,
        "Secondary Fish",
        "The secondary fish is the second most common fish in the aquarium.",
        200,
        mapOf(
            FishPhase.Regular to R.drawable.angel_fish_regular,
            FishPhase.Strong to R.drawable.angel_fish_strong,
            FishPhase.Dying to R.drawable.angel_fish_dying,
            FishPhase.Dead to R.drawable.angel_fish_dead,
        ),
    ),
    Third(
        2,
        "Third Fish",
        "The third fish is the third most common fish in the aquarium.",
        300,
        mapOf(
            FishPhase.Regular to R.drawable.gold_fish_regular,
            FishPhase.Strong to R.drawable.gold_fish_strong,
            FishPhase.Dying to R.drawable.gold_fish_dying,
            FishPhase.Dead to R.drawable.gold_fish_dead,
        ),
    ),
    ;

    @Exclude
    fun getPhaseImage(healthLevel: Float): Int {
        return this.phases[FishPhase.getPhase(healthLevel)]
            ?: throw IllegalStateException(
                "Image not found for phase: ${
                    FishPhase.getPhase(
                        healthLevel,
                    )
                }, type: $this",
            )
    }

    @Exclude
    fun getPhaseImage(phase: FishPhase): Int {
        return this.phases[phase]
            ?: throw IllegalStateException("Image not found for phase: $phase, type: $this")
    }
}

enum class FishPhase {
    Regular,
    Strong,
    Dying,
    Dead,
    ;

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
