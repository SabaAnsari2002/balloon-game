package com.saba.balloongame

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saba.balloongame.R

@Composable
fun SplashScreen() {
    val luckiestGuyFontFamily = FontFamily(
        Font(R.font.luckiestguy, FontWeight.Normal)
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.splash_image),
            contentDescription = "Splash Image",
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Welcome to Balloon Games!",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontFamily = luckiestGuyFontFamily,
                fontSize = 24.sp
            )
        )
    }
}