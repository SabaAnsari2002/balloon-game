package com.saba.balloongame

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.saba.balloongame.BalloonView

@Composable
fun BalloonGameScreen(onGameOver: () -> Unit) {
    val context = LocalContext.current
    AndroidView(
        factory = {
            BalloonView(context).apply {
                this.onGameOver = onGameOver
                resetGame()
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
