package com.github.k409.fitflow.model

data class MarketItem(
    val id: Int = -1,
    val title: String = "",
    val description: String = "",
    val price: Int = -1,
    val priceCents: Long = -1,
    val phases: Map<String, String>? = emptyMap(),
    val type: String = "",
    val image: String = "",
)

data class InventoryItem(
    val item: MarketItem,
    val placed: Boolean = false,
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
)
