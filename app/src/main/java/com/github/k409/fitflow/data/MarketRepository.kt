package com.github.k409.fitflow.data

import android.util.Log
import com.github.k409.fitflow.model.Item
import com.google.firebase.auth.FirebaseAuth
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
class MarketRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
) {
    fun getMarketItems(): Flow<List<Item>> {
        return db.collection(MARKET_COLLECTION)
            .snapshots()
            .map {
                it.documents.map { document ->
                    document.toObject<Item>() ?: Item()
                }
            }
    }
    fun getUserOwnedItems(): Flow<List<Item>> {
        val currentUser = auth.currentUser
        val uid = currentUser!!.uid

        return db.collection(INVENTORY_COLLECTION)
            .document(uid)
            .collection(ITEMS_COLLECTION)
            .snapshots()
            .map {
                it.documents.map { document ->
                    document.toObject<Item>() ?: Item()
                }
            }
    }
    suspend fun addItemToUser(item : Item) {
        val currentUser = auth.currentUser
        val uid = currentUser!!.uid

        val inventoryDocumentRef =
            db.collection(INVENTORY_COLLECTION)
                .document(uid)
                .collection(ITEMS_COLLECTION)
                .document(item.id.toString())

        val updatedData = hashMapOf(
            "description" to item.description,
            "id" to item.id,
            "phases" to (item.phases ?: emptyMap()),
            "price" to item.price,
            "title" to item.title,
            "type" to item.title,
            "image" to item.image,
        )
        try {
            inventoryDocumentRef
                .set(updatedData)
                .await()
        } catch (e: FirebaseFirestoreException) {
            Log.e("Market Repository", "Error updating inventory", e)
        }
    }
    suspend fun removeItemFromUser(item : Item) {
        val currentUser = auth.currentUser
        val uid = currentUser!!.uid

        val inventoryDocumentRef =
            db.collection(INVENTORY_COLLECTION)
                .document(uid)
                .collection(ITEMS_COLLECTION)
                .document(item.id.toString())

        try {
            inventoryDocumentRef
                .delete()
                .await()
        } catch (e: FirebaseFirestoreException) {
            Log.e("Market Repository", "Error updating inventory", e)
        }
    }
}