package com.saba.balloongame

import MusicLifecycleObserver
import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import android.media.MediaPlayer
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.LifecycleOwner
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import kotlinx.coroutines.delay

@Composable
fun BalloonGameScreen(
    onGameOver: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    mediaPlayer: MediaPlayer,
    onMusicStatusChanged: (Boolean) -> Unit,
    isMuted: Boolean,
    onMuteStatusChanged: (Boolean) -> Unit,
    balloonViewRef: (BalloonView?) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    var gamePaused by rememberSaveable { mutableStateOf(false) }
    var balloonView by remember { mutableStateOf<BalloonView?>(null) }
    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    var currentScore by remember { mutableStateOf(0) }
    var currentHighScore by remember { mutableStateOf(0) }
    var showNewHighScoreMessage by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = MusicLifecycleObserver(mediaPlayer)
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            balloonView?.saveHighScore()
        }
    }

    LaunchedEffect(showNewHighScoreMessage) {
        if (showNewHighScoreMessage) {
            delay(1000) // Show message for 1 second
            showNewHighScoreMessage = false
        }
    }

    BackHandler(onBack = {
        showExitDialog = true
        gamePaused = true
        balloonView?.pauseGame()
        onPause()
    })

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                BalloonView(context).apply {
                    this.onGameOver = {
                        saveHighScore()
                        onGameOver() // صدا زدن onGameOver برای تغییر آهنگ
                    }
                    this.onPause = {
                        gamePaused = true
                        onPause()
                    }
                    this.onResume = {
                        gamePaused = false
                        onResume()
                    }

                    this.gameStateListener = object : GameStateListener {
                        override fun onScoreChanged(newScore: Int) {
                            currentScore = newScore
                        }
                        override fun onHighScoreChanged(newHighScore: Int) {
                            currentHighScore = newHighScore
                        }
                        override fun onNewHighScore(newHighScore: Int) {
                            currentHighScore = newHighScore
                            showNewHighScoreMessage = true
                        }
                        override fun onGameOver() {
                            saveHighScore()
                            onGameOver()
                        }
                    }
                    balloonView = this
                    balloonViewRef(this)
                    resetGame()
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (gamePaused && showExitDialog) {
            AlertDialog(
                onDismissRequest = {
                    showExitDialog = false
                    gamePaused = false
                    balloonView?.resumeGame()
                    onResume()
                },
                title = { Text("Exit game") },
                text = { Text("Are you sure you want to exit?") },
                confirmButton = {
                    OutlinedButton(onClick = {
                        showExitDialog = false
                        gamePaused = false
                        (context as? Activity)?.finish()
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = {
                        showExitDialog = false
                        gamePaused = false
                        balloonView?.resumeGame()
                        onResume()
                    }) {
                        Text("No")
                    }
                }
            )
        }

        if (gamePaused && !showExitDialog) {
            SettingsCard(
                onBackToGame = {
                    gamePaused = false
                    balloonView?.resumeGame()
                    onResume()
                },
                onRestartGame = {
                    gamePaused = false
                    balloonView?.resetGame()
                    onResume()
                },
                mediaPlayer = mediaPlayer,
                onMusicStatusChanged = onMusicStatusChanged,
                isMuted = isMuted,
                onMuteStatusChanged = onMuteStatusChanged
            )
        }

        Image(
            painter = painterResource(id = R.drawable.settings_icon),
            contentDescription = "Settings",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(58.dp) // Adjust the size as needed
                .clickable {
                    gamePaused = true
                    balloonView?.pauseGame()
                    onPause()
                }
        )

        // Display score and high score
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {

        }

        // Display new high score message
        if (showNewHighScoreMessage) {
            Text(
                "NEW HIGH SCORE!",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Red
            )
        }
    }
}
