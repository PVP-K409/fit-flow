package com.github.k409.fitflow.ui.screen.market

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.k409.fitflow.data.MarketRepository
import com.github.k409.fitflow.model.Item
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class MarketViewModel @Inject constructor(
    marketRepository: MarketRepository,
) : ViewModel() {

    val marketUiState: StateFlow<MarketUiState> = combine (
        marketRepository.getAllItems(),
    )
     { items ->
        MarketUiState.Success(
            items = items
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MarketUiState.Loading,
    )
}
fun getImageHttpUrl(imageUrl: String): String {
    // Reference to an image file in Cloud Storage
    return Firebase.storage.getReferenceFromUrl(imageUrl).downloadUrl.toString()
}
sealed interface MarketUiState {
    data object Loading : MarketUiState
    data class Success(
        val items: Array<List<Item>>,
    ) : MarketUiState {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Success

            return items.contentEquals(other.items)
        }

        override fun hashCode(): Int {
            return items.contentHashCode()
        }
    }
}