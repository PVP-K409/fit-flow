package com.github.k409.fitflow.ui.screen.market

import android.app.Activity.RESULT_OK
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.k409.fitflow.R
import com.github.k409.fitflow.service.SnackbarManager
import com.github.k409.fitflow.ui.common.ConfirmDialog
import com.github.k409.fitflow.ui.common.FitFlowCircularProgressIndicator
import com.github.k409.fitflow.ui.common.item.CategorySelectHeader
import com.github.k409.fitflow.ui.common.item.InventoryItemCard
import com.github.k409.fitflow.ui.common.item.InventoryItemCardGooglePay
import com.github.k409.fitflow.ui.screen.checkout.CheckoutViewModel
import com.github.k409.fitflow.ui.screen.checkout.PaymentUiState
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.PaymentData

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MarketScreen(
    marketViewModel: MarketViewModel = hiltViewModel(),
    checkoutViewModel: CheckoutViewModel = hiltViewModel(),
) {
    val payUiState: PaymentUiState by checkoutViewModel.paymentUiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val language = context.resources.configuration.locales[0].language

    var selectedCategoryIndex by rememberSaveable { mutableIntStateOf(0) }

    var showDialog by remember { mutableStateOf(false) }
    var dialogText by remember { mutableStateOf("") }
    var addClicked by remember { mutableStateOf(false) }
    var selectedMarketItem by remember { mutableStateOf(com.github.k409.fitflow.model.MarketItem()) }

    val marketUiState by marketViewModel.marketUiState.collectAsStateWithLifecycle()

    val resolvePaymentForResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
            when (result.resultCode) {
                RESULT_OK ->
                    result.data?.let { intent ->
                        PaymentData.getFromIntent(intent)?.let {
                            checkoutViewModel.setPaymentData(it)
                            SnackbarManager.showMessage(context.getString(R.string.payment_successful))
                            marketViewModel.addItemToUserInventory(selectedMarketItem)
                        }
                    }
            }
        }

    val onGooglePayButtonClick: (priceCents: Long) -> Task<PaymentData> = { priceCents ->
        val task = checkoutViewModel.getLoadPaymentDataTask(priceCents = priceCents)

        task.addOnCompleteListener { completedTask ->
            if (completedTask.isSuccessful) {
                completedTask.result.let {
                    Log.i("Google Pay result:", it.toJson())
                }
            } else {
                when (val exception = completedTask.exception) {
                    is ResolvableApiException -> {
                        resolvePaymentForResult.launch(
                            IntentSenderRequest.Builder(exception.resolution)
                                .build(),
                        )
                    }
                }
            }
        }
    }

    if (marketUiState is MarketUiState.Loading) {
        FitFlowCircularProgressIndicator()
        return
    }

    val allItems = (marketUiState as MarketUiState.Success).marketItems
    val ownedItems = (marketUiState as MarketUiState.Success).ownedMarketItems
    val user = (marketUiState as MarketUiState.Success).user

    val items = when (selectedCategoryIndex) {
        0 -> allItems.filter { it.type == "fish" }
        1 -> allItems.filter { it.type == "decoration" }
        else -> emptyList()
    }
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        stickyHeader {
            CategorySelectHeader(
                selectedItemIndex = selectedCategoryIndex,
                onItemSelected = { selectedCategoryIndex = it },
            )
        }
        items(items) { item ->
            if (item.priceCents <= 0) {
                InventoryItemCard(
                    modifier = Modifier,
                    imageDownloadUrl = item.phases?.get("Regular") ?: item.image,
                    name = item.title,
                    description = item.localizedDescriptions[language] ?: item.description,
                    removeButtonText = "${stringResource(R.string.sell_for)} ${item.price / 2}",
                    onRemoveClick =
                    {
                        showDialog = true
                        addClicked = false
                        dialogText =
                            "${context.getString(R.string.are_you_sure_you_want_to_sell)} ${item.title}?"
                        selectedMarketItem = item
                    },
                    removeButtonEnabled = ownedItems.find { it.item.id == item.id } != null,
                    addButtonText = "${stringResource(R.string.buy_for)} ${item.price}",
                    onAddClick =
                    {
                        showDialog = true
                        addClicked = true
                        dialogText =
                            "${context.getString(R.string.are_you_sure_you_want_to_buy)} ${item.title}?"
                        selectedMarketItem = item
                    },
                    addButtonEnabled = user.points >= item.price && ownedItems.find { it.item.id == item.id } == null,
                    coinIcon = {
                        Icon(
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .size(20.dp),
                            painter = painterResource(id = R.drawable.coin),
                            tint = Color.Unspecified,
                            contentDescription = "",
                        )
                    },
                )
            } else {
                InventoryItemCardGooglePay(
                    modifier = Modifier,
                    imageDownloadUrl = item.phases?.get("Regular") ?: item.image,
                    name = item.title,
                    description = item.localizedDescriptions[language] ?: item.description,
                    owned = ownedItems.find { it.item.id == item.id } != null,
                    payUiState = payUiState,
                    priceCents = item.priceCents,
                    onGooglePayButtonClick = {
                        onGooglePayButtonClick(item.priceCents)
                        selectedMarketItem = item
                    },
                )
            }
        }
    }

    if (showDialog) {
        ConfirmDialog(
            dialogTitle = stringResource(R.string.are_you_sure),
            dialogText = dialogText,
            onConfirm = {
                if (addClicked) {
                    marketViewModel.updateUserCoinBalance((-selectedMarketItem.price).toLong())
                    marketViewModel.addItemToUserInventory(selectedMarketItem)
                    SnackbarManager.showMessage(
                        context.getString(
                            R.string.item_has_been_added_to_your_inventory,
                            selectedMarketItem.title
                        )
                    )
                } else {
                    marketViewModel.updateUserCoinBalance((selectedMarketItem.price / 2).toLong())
                    marketViewModel.removeItemFromUserInventory(selectedMarketItem)
                    SnackbarManager.showMessage(
                        context.getString(
                            R.string.item_has_been_sold,
                            selectedMarketItem.title
                        )
                    )
                }
                showDialog = false
            },
            onDismiss = { showDialog = false },
        )
    }
}
