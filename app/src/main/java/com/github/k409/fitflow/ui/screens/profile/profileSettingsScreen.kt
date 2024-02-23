package com.github.k409.fitflow.ui.screens.profile

import android.widget.ImageButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.github.k409.fitflow.ui.features.FeatureList
import com.github.k409.fitflow.ui.navigation.NavRoutes

const val username = "user1" // use db later

val moreOptionsList = listOf(
    FeatureList("Edit Profile", DCodeIcon.ImageVectorIcon(MyIcons.Edit), NavRoutes.ProfileCreation),
    //FeatureList("Manage Account", ImageVectorIcon(MyIcons.AccountBox), ""),
    //FeatureList("Privacy Policy", DrawableResourceIcon(MyIcons.Policy), ""),
    //FeatureList("About", ImageVectorIcon(MyIcons.Info), ""),
    //FeatureList("Help & Feedback", DrawableResourceIcon(MyIcons.Android_Head), ""),
)

@Composable
fun ProfileSettingsScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Text(text = "")
    }
    moreOptionsList.forEach {
        MoreOptionsComp(it)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreOptionsComp(
    featureList: FeatureList,
) {
    Row(
        modifier = Modifier.padding(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when (featureList.listIcon) {
            is DCodeIcon.ImageVectorIcon -> Image(
                imageVector = featureList.listIcon.imageVector,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .padding(6.dp)
            )

            is DCodeIcon.DrawableResourceIcon -> Image(
                painter = painterResource(id = featureList.listIcon.id),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .padding(6.dp)
            )

            else -> {}
        }
        Column(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .weight(1f)
        ) {
            Text(
                text = featureList.name,
                style = MaterialTheme.typography.labelLarge
            )
        }
        IconButton(onClick = {/**/ }) {
            Image(
                imageVector = MyIcons.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}