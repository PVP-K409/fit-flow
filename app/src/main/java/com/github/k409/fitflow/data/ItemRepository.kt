package com.github.k409.fitflow.data

import android.util.Log
import com.github.k409.fitflow.model.InventoryItem
import com.github.k409.fitflow.model.MarketItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val MARKET_COLLECTION = "market"
private const val INVENTORY_COLLECTION = "inventory"
private const val ITEMS_COLLECTION = "items"
private const val REWARDS_COLLECTION = "rewards"

private const val PLACED_FIELD = "placed"
private const val PRICE_FIELD = "price"
private const val INITIAL_FISH_ID = 0
class ItemRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
) {
    fun getMarketItems(): Flow<List<MarketItem>> {
        return db.collection(MARKET_COLLECTION)
            .orderBy(PRICE_FIELD)
            .snapshots()
            .map {
                it.documents.map { document ->
                    document.toObject<MarketItem>() ?: MarketItem()
                }.drop(1) // drop initial fish that user has by default
            }
    }

    fun getUserOwnedItems(): Flow<List<InventoryItem>> {
        val currentUser = auth.currentUser
        val uid = currentUser!!.uid

        return db.collection(INVENTORY_COLLECTION)
            .document(uid)
            .collection(ITEMS_COLLECTION)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { document ->
                    val itemRef = document.get("item") as? DocumentReference
                    val marketItem = itemRef?.get()?.await()?.toObject<MarketItem>()

                    marketItem?.let {
                        InventoryItem(
                            item = it,
                            placed = document.getBoolean(PLACED_FIELD) ?: false,
                        )
                    }
                }
            }
    }

    fun getAquariumItems(): Flow<List<InventoryItem>> {
        val currentUser = auth.currentUser
        val uid = currentUser!!.uid

        return db.collection(INVENTORY_COLLECTION)
            .document(uid)
            .collection(ITEMS_COLLECTION)
            .whereEqualTo(PLACED_FIELD, true)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { document ->
                    val itemRef = document.get("item") as? DocumentReference
                    val marketItem = itemRef?.get()?.await()?.toObject<MarketItem>()

                    marketItem?.let {
                        InventoryItem(
                            item = it,
                            placed = document.getBoolean(PLACED_FIELD) ?: false,
                            offsetX = document.getDouble("offsetX")?.toFloat() ?: 0f,
                            offsetY = document.getDouble("offsetY")?.toFloat() ?: 0f,
                        )
                    }
                }
            }
    }

    suspend fun addItemToUserInventory(marketItem: MarketItem) {
        val currentUser = auth.currentUser
        val uid = currentUser!!.uid

        val inventoryDocumentRef = getInventoryDocumentRef(uid, marketItem.id.toString())

        val itemReference = db.collection(MARKET_COLLECTION).document(marketItem.id.toString())

        val updatedData = hashMapOf(
            "item" to itemReference,
            "placed" to false,
        )

        try {
            inventoryDocumentRef
                .set(updatedData)
                .await()
        } catch (e: FirebaseFirestoreException) {
            Log.e("Item Repository", "Error updating inventory", e)
        }
    }

    suspend fun addRewardItemToUserInventory(userLevel: Int) {
        val currentUser = auth.currentUser
        val uid = currentUser!!.uid

        try {
            val itemReference = db.collection(REWARDS_COLLECTION).whereEqualTo("id", userLevel).get().await().documents.first().reference

            // Log.d("ItemRepository", "Reward item reference: $itemReference")

            val inventoryDocumentRef = getInventoryDocumentRef(uid, itemReference.id)

            val updatedData = hashMapOf(
                "item" to itemReference,
                "placed" to false,
            )

            inventoryDocumentRef
                .set(updatedData)
                .await()
        } catch (e: FirebaseFirestoreException) {
            Log.e("Item Repository", "Error adding reward item", e)
        }
    }
    fun getRewardItems(): Flow<List<MarketItem>> {
        return db.collection(REWARDS_COLLECTION)
            .snapshots()
            .map {
                it.documents.map { document ->
                    document.toObject<MarketItem>() ?: MarketItem()
                }
            }
    }
    suspend fun updateInventoryItem(marketItem: InventoryItem) {
        val currentUser = auth.currentUser
        val uid = currentUser!!.uid

        val inventoryDocumentRef = getInventoryDocumentRef(uid, marketItem.item.id.toString())

        val updatedData = if (marketItem.item.id >= 1000) {
            hashMapOf(
                "item" to db.collection(REWARDS_COLLECTION).document(marketItem.item.id.toString()),
                "placed" to marketItem.placed,
                "offsetX" to marketItem.offsetX,
                "offsetY" to marketItem.offsetY,
            )
        } else {
            hashMapOf(
                "item" to db.collection(MARKET_COLLECTION).document(marketItem.item.id.toString()),
                "placed" to marketItem.placed,
                "offsetX" to marketItem.offsetX,
                "offsetY" to marketItem.offsetY,
            )
        }

        try {
            inventoryDocumentRef
                .set(updatedData)
                .await()
        } catch (e: FirebaseFirestoreException) {
            Log.e("Item Repository", "Error updating inventory", e)
        }
    }

    suspend fun removeItemFromUser(itemId: Int) {
        val currentUser = auth.currentUser
        val uid = currentUser!!.uid

        val inventoryDocumentRef = getInventoryDocumentRef(uid, itemId.toString())

        try {
            inventoryDocumentRef
                .delete()
                .await()
        } catch (e: FirebaseFirestoreException) {
            Log.e("Item Repository", "Error updating inventory", e)
        }
    }

    private fun getInventoryDocumentRef(
        uid: String,
        itemId: String,
    ): DocumentReference {
        return db.collection(INVENTORY_COLLECTION)
            .document(uid)
            .collection(ITEMS_COLLECTION)
            .document(itemId)
    }
    suspend fun getInitialFish(): MarketItem {
        return db.collection(MARKET_COLLECTION)
            .document(INITIAL_FISH_ID.toString())
            .get()
            .await()
            .toObject<MarketItem>() ?: MarketItem()
    }
}
