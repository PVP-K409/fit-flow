package com.github.k409.fitflow.ui.screens.inventory

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.k409.fitflow.R

data class Item(val name: String, val description: String, val imageResId: Int)

@Composable
fun List(
    fishes: List<Item>,
    modifier: Modifier = Modifier,
) {
    LazyColumn {
        items(fishes) { fish ->
            ColumnItem(
                modifier,
                painter = fish.imageResId,
                name = fish.name,
                description = fish.description,
            )
        }
    }
}

@Composable
fun ColumnItem(
    modifier: Modifier,
    painter: Int,
    name: String,
    description: String,
) {
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme

    Card(
        modifier
            .padding(10.dp)
            .wrapContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = colors.surface,
        ),
        elevation = CardDefaults.cardElevation(10.dp),
    ) {
        Box(modifier.fillMaxSize()) {

            Image(
                painter = painterResource(id = R.drawable.chest3),
                contentDescription = null,
                modifier.fillMaxSize(),
            )

            Column(modifier.fillMaxSize()) {
                Row(
                    modifier.fillMaxSize(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Image(
                        painter = painterResource(id = painter),
                        contentDescription = name,
                        modifier.size(120.dp),
                    )
                    Column(modifier.padding(8.dp)) {
                        Text(
                            text = name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = description,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                    }
                }

                Row(
                    modifier.fillMaxSize(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Column(
                        modifier.padding(6.dp),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Button(
                            onClick = {
                                Toast.makeText(
                                    context,
                                    "Added to aquarium",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            },
                        ) {
                            Text(text = "Add")
                        }
                    }
                    Column(
                        modifier.padding(6.dp),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Button(
                            onClick = {
                                Toast.makeText(
                                    context,
                                    "Removed from aquarium",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            },
                            colors = ButtonDefaults.buttonColors(colors.error),
                        ) {
                            Text(text = "Remove")
                        }
                    }
                }
            }
        }
    }
}
