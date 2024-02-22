package com.github.k409.fitflow.DataModels

data class User(
    var gender: String = "",
    var height: Double = 0.0,
    var name: String = "",
    var weight: Double = 0.0,
    var lastUpTime: Long = 0,
    var steps: MutableList<Step> = mutableListOf()
)
