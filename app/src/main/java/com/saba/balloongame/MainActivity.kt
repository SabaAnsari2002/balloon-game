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
    private lateinit var gameOverMediaPlayer: MediaPlayer
    private var isMusicPlaying by mutableStateOf(false)
    private var isMuted by mutableStateOf(false)

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

                // Initialize media players
                mediaPlayer = MediaPlayer.create(this, R.raw.gamesound)
                gameOverMediaPlayer = MediaPlayer.create(this, R.raw.gameover)
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
                            if (!isMuted) {
                                mediaPlayer.start()
                                isMusicPlaying = true
                            }
                        })
                        startGame -> BalloonGameScreen(
                            onGameOver = {
                                gameOver = true
                                startGame = false
                                mediaPlayer.pause() // Pause current music
                                gameOverMediaPlayer.start() // Start game over music
                                isMusicPlaying = false
                            },
                            onPause = {
                                mediaPlayer.pause() // Pause current music
                                isMusicPlaying = false
                            },
                            onResume = {
                                if (!isMuted) {
                                    mediaPlayer.start() // Resume current music only if not muted
                                    isMusicPlaying = true
                                }
                            },
                            mediaPlayer = mediaPlayer,
                            onMusicStatusChanged = { isPlaying ->
                                isMusicPlaying = isPlaying
                            },
                            isMuted = isMuted,
                            onMuteStatusChanged = { mute ->
                                isMuted = mute
                                if (mute) {
                                    mediaPlayer.pause()
                                } else if (isMusicPlaying) {
                                    mediaPlayer.start()
                                }
                            }
                        )
                        else -> HomeScreen(onStartClick = {
                            startGame = true
                            if (!isMuted) {
                                mediaPlayer.start() // Start music
                                isMusicPlaying = true
                            }
                        })
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        gameOverMediaPlayer.release()
    }
}
