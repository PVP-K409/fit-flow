package com.github.k409.fitflow.ui.screens.inventory

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.BottomAppBar
//import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.k409.fitflow.R

@Composable
fun InventoryScreen(){
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Image(
            painter = painterResource(id = R.drawable.chest),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        val items = listOf(
            Item("Clownfish",
                "The fish from the movie \"Finding Nemo\"",
                R.drawable.primary_fish),
            Item("Plant",
                "I don't know what type of plant this is",
                R.drawable.plant),
        )
        List(items)
    }
}