package com.github.k409.fitflow.model


enum class FishPhase {
    Regular,
    Strong,
    Dying,
    Dead;

    companion object {
        fun getPhase(healthLevel: Float): FishPhase {
            return when {
                healthLevel >= 1 -> Strong
                healthLevel >= 0.5 -> Regular
                healthLevel >= 0.25 -> Dying
                else -> Dead
            }
        }
    }
}
