package com.github.k409.fitflow.data

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.github.k409.fitflow.model.Item
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val MARKET_COLLECTION = "market"

class MarketRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
) {
    fun getAllItems(): Flow<List<Item>> {
        return db.collection(MARKET_COLLECTION)
            .snapshots()
            .map {
                it.documents.map { document ->
                    //val id = document.data?.get("id") as? Int ?: -1
                    //val title = document.data?.get("title") as? String ?: ""
                    //val description: String = ""
                    //val price: Int = -1
                    //val phases: Map<String, String> = emptyMap()

                    document.toObject<Item>() ?: Item()
                }
            }
    }
}