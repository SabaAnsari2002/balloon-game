package com.saba.balloongame

import android.media.MediaPlayer
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
    private lateinit var mediaPlayer: MediaPlayer

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

                // Initialize media player
                mediaPlayer = MediaPlayer.create(this, R.raw.gamesound)
                mediaPlayer.isLooping = true

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when {
                        showSplashScreen -> SplashScreen()
                        gameOver -> GameOverScreen(onRestartClick = {
                            gameOver = false
                            startGame = true
                            mediaPlayer.start() // Start music
                        })
                        startGame -> BalloonGameScreen(
                            onGameOver = {
                                gameOver = true
                                startGame = false
                                mediaPlayer.pause() // Pause music
                            },
                            onPause = {
                                mediaPlayer.pause() // Pause music
                            },
                            onResume = {
                                mediaPlayer.start() // Resume music
                            },
                            mediaPlayer = mediaPlayer
                        )
                        else -> HomeScreen(onStartClick = {
                            startGame = true
                            mediaPlayer.start() // Start music
                        })
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}
