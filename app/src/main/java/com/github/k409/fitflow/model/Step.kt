package com.github.k409.fitflow.model

data class Step(
    var current: Long = 0, // all current steps
    var initial: Long = 0, // stepcounter value just after midnight
    var date: String = "", // day
    var temp: Long = 0, // value before reboot
    var calories: Long? = 0, // today calories count
    var distance: Double? = 0.0 // today total distance
)
