package com.saba.balloongame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.saba.balloongame.ui.theme.BalloonGameTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BalloonGameTheme {
                var showSplashScreen by remember { mutableStateOf(true) }
                var startGame by remember { mutableStateOf(false) }
                var gameOver by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    delay(3000)
                    showSplashScreen = false
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when {
                        showSplashScreen -> SplashScreen()
                        gameOver -> GameOverScreen(onRestartClick = {
                            gameOver = false
                            startGame = true
                        })
                        startGame -> BalloonGameScreen(
                            onGameOver = {
                                gameOver = true
                                startGame = false
                            },
                            onPause = { /* Handle pause logic here */ },
                            onResume = { /* Handle resume logic here */ }
                        )
                        else -> HomeScreen(onStartClick = { startGame = true })
                    }
                }
            }
        }
    }
}
