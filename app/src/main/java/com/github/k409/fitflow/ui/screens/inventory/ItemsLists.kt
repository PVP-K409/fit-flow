package com.github.k409.fitflow.ui.screens.inventory

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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

data class Item(val name: String, val description: String, val imageResId: Int)

@Composable
fun List(
    fishes: List<Item>,
    modifier: Modifier = Modifier,
) {
    LazyColumn {
        items(fishes) {fish ->
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
){
    val context = LocalContext.current

    Card(
        onClick = {
            Toast.makeText(
                context,
                "Add to aquarium",
                Toast.LENGTH_SHORT).show()
        },
        modifier
            .padding(10.dp)
            .wrapContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(10.dp),
    ) {
        Row(
            modifier.fillMaxSize(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ){
            Image(
                painter = painterResource(id = painter),
                contentDescription = name,
                modifier.size(100.dp),
            )
            Column(modifier.padding(12.dp)) {
                Text(text = name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = description, fontSize = 14.sp)
            }
        }
    }
}
