package com.github.k409.fitflow.ui.common.item

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.github.k409.fitflow.R
import com.google.firebase.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun InventoryItemCard(
    modifier: Modifier,
    imageUrl: String,
    name: String,
    description: String,
    addButtonText: String,
    onAddClick: () -> Unit = {},
    removeButtonText: String,
    onRemoveClick: () -> Unit = {},
    coinIcon: @Composable () -> Unit,
    context: Context,
) {
    val colors = MaterialTheme.colorScheme
    var loadSuccess by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var image by remember { mutableStateOf("") }

    coroutineScope.launch {
        image = getImageHttpUrl(imageUrl)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = colors.tertiaryContainer, shape = CardDefaults.shape),
    ) {
        Column(
            modifier = modifier
                .padding(16.dp)
                .fillMaxSize(),
        ) {
            Row(
                modifier = modifier
                    .fillMaxSize()
                    .background(
                        color = colors.tertiary,
                        shape = CardDefaults.shape,
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                /*Image(
                    modifier = modifier
                        .padding(8.dp)
                        .size(80.dp),
                    painter = painterResource(id = painter),
                    contentDescription = name,
                )*/

                if (image.isNotEmpty()) {
                    /*AsyncImage(
                        model = ImageRequest.Builder(context = LocalContext.current)
                            .data(image)
                            .crossfade(true)
                            .build(),
                        //error = painterResource(R.drawable.error_24px),
                        //placeholder = painterResource(R.drawable.avd_fitflow),
                        contentDescription = name,
                        modifier = modifier
                            .padding(8.dp)
                            .size(80.dp),
                            )*/
                    /*val overlayPainter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(image)
                            .size(Size.ORIGINAL)
                            .build()
                    )
                    val overlayImageLoadedState = remember { overlayPainter.state }
                    Log.d("ItemCardState", overlayImageLoadedState.toString())
                    if (overlayImageLoadedState is AsyncImagePainter.State.Success) {*/

                        Log.d("ItemCard", "Composing $image")

                        AsyncImage(
                            //model = ImageRequest.Builder(context = LocalContext.current)
                            //    .data(overlayImageBitmap)
                            //    .build(),
                            // works with non-xml images
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(image)
                                //.allowConversionToBitmap(true)
                                .crossfade(true)
                                .size(Size.ORIGINAL)
                                .build(),
                            error = painterResource(R.drawable.error_24px),
                            //placeholder = painterResource(R.drawable.avd_fitflow),
                            contentDescription = name,
                            modifier = modifier
                                .padding(8.dp)
                                .size(80.dp),
                        )
                    }
                    /*GlideImage(
                        model = image,
                        contentDescription = name,
                        modifier = modifier
                            .padding(8.dp)
                            .size(80.dp),
                    )*/
                    /*Glide.with(LocalContext.current)
                        .asBitmap()
                        .load(image)
                        .into(object : CustomTarget<Bitmap>(){
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                imageView.setImageBitmap(resource)
                            }
                            override fun onLoadCleared(placeholder: Drawable?) {
                                // this is called when imageView is cleared on lifecycle call or for
                                // some other reason.
                                // if you are referencing the bitmap somewhere else too other than this imageView
                                // clear it here as you can no longer have the bitmap
                            }
                        })*/
                }
                Column(modifier = modifier.padding(8.dp)) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.onTertiary,
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Normal,
                        color = colors.onTertiary,
                        textAlign = TextAlign.Justify,
                    )
                }
            //}

            Spacer(modifier = modifier.height(16.dp))

            Row(
                modifier = modifier.fillMaxSize(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // TODO : disable remove button if item is not in aquarium / was not purchased
                // TODO : disable add button if item is in aquarium and no more in inventory (also, can user purchase multiple fishes of single type?)
                Button(
                    onClick = onRemoveClick,
                    colors = ButtonDefaults.buttonColors(colors.error),
                ) {
                    Text(text = removeButtonText)
                    coinIcon()
                }

                Button(
                    onClick = onAddClick,
                    colors = ButtonDefaults.buttonColors(colors.primary),
                ) {
                    Text(text = addButtonText)
                    coinIcon()
                }
            }
        }
    }
}
suspend fun getImageHttpUrl(imageUrl: String): String {
    // Reference to an image file in Cloud Storage
    //Log.d("ItemCard", Firebase.storage.getReferenceFromUrl(imageUrl).downloadUrl.await().toString())
    return Firebase.storage.getReferenceFromUrl(imageUrl).downloadUrl.await().toString()
    // ImageView in your Activity
    /*val imageView = ImageView(context)

    // Download directly from StorageReference using Glide
    // (See MyAppGlideModule for Loader registration)
    imageRef.downloadUrl.addOnSuccessListener { uri ->

        Log.d("ItemCard", uri.toString())
    }

    return ""*/
}
