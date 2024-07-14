package com.saba.balloongame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                        startGame -> BalloonGameScreen(onGameOver = {
                            gameOver = true
                            startGame = false
                        })
                        else -> HomeScreen(onStartClick = { startGame = true })
                    }
                }
            }
        }
    }
}

@Composable
fun GameOverScreen(onRestartClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "گیم اور", fontSize = 30.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRestartClick) {
                Text(text = "شروع مجدد")
            }
        }
    }
}
