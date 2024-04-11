package com.github.k409.fitflow.model

data class Item(
    val id: Int = -1,
    val title: String = "",
    val description: String = "",
    val price: Int = -1,
    val phases: Map<String, String>? = emptyMap(),
)
