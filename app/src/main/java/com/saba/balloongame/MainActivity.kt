package com.saba.balloongame

import HomeScreen
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
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
    private var currentScore by mutableStateOf(0)
    private var gameOver by mutableStateOf(false)
    private var startGame by mutableStateOf(false)
    private var balloonView: BalloonView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BalloonGameTheme {
                var showSplashScreen by remember { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    delay(3000)
                    showSplashScreen = false
                }

                // Initialize media players
                mediaPlayer = MediaPlayer.create(this, R.raw.gamesound).apply {
                    isLooping = true
                }
                gameOverMediaPlayer = MediaPlayer.create(this, R.raw.gameover)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when {
                        showSplashScreen -> SplashScreen()
                        gameOver -> GameOverScreen(score = currentScore, onRestartClick = {
                            gameOver = false
                            startGame = true
                            if (!isMuted) {
                                mediaPlayer.start()
                                isMusicPlaying = true
                            }
                        })
                        startGame -> BalloonGameScreen(
                            onGameOver = {
                                currentScore = balloonView?.getScore() ?: 0
                                gameOver = true
                                startGame = false
                                mediaPlayer.pause()
                                gameOverMediaPlayer.start()
                                isMusicPlaying = false
                            },
                            onPause = {
                                mediaPlayer.pause()
                                isMusicPlaying = false
                            },
                            onResume = {
                                if (!isMuted) {
                                    mediaPlayer.start()
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
                            },
                            balloonViewRef = { balloonView = it }
                        )
                        else -> HomeScreen(onStartClick = {
                            startGame = true
                            if (!isMuted) {
                                mediaPlayer.start()
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