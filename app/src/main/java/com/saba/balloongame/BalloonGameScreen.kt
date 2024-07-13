package com.saba.balloongame

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.saba.balloongame.BalloonView

@Composable
fun BalloonGameScreen() {
    AndroidView(
        factory = { context -> BalloonView(context) },
        modifier = Modifier.fillMaxSize()
    )
}