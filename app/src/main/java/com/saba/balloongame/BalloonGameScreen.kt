package com.saba.balloongame

import MusicLifecycleObserver
import android.app.Activity
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.LifecycleOwner
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable


//@Composable
//fun BalloonGameScreen(
//    onGameOver: () -> Unit,
//    onPause: () -> Unit,
//    onResume: () -> Unit,
//    mediaPlayer: MediaPlayer,
//    onMusicStatusChanged: (Boolean) -> Unit,
//    isMuted: Boolean,
//    onMuteStatusChanged: (Boolean) -> Unit
//) {
//    val context = LocalContext.current
//    var gamePaused by remember { mutableStateOf(false) }
//    var balloonView by remember { mutableStateOf<BalloonView?>(null) }
//
//    Box(modifier = Modifier.fillMaxSize()) {
//        AndroidView(
//            factory = {
//                BalloonView(context).apply {
//                    this.onGameOver = onGameOver
//                    this.onPause = {
//                        gamePaused = true
//                        onPause()
//                    }
//                    this.onResume = {
//                        gamePaused = false
//                        onResume()
//                    }
//                    balloonView = this
//                    resetGame()
//                }
//            },
//            modifier = Modifier.fillMaxSize()
//        )
//
//        if (gamePaused) {
//            SettingsCard(
//                onBackToGame = {
//                    gamePaused = false
//                    balloonView?.resumeGame()
//                    onResume()
//                },
//                onRestartGame = {
//                    gamePaused = false
//                    balloonView?.resetGame()
//                    onResume()
//                },
//                mediaPlayer = mediaPlayer,
//                onMusicStatusChanged = onMusicStatusChanged,
//                isMuted = isMuted,
//                onMuteStatusChanged = onMuteStatusChanged
//            )
//        }
//
//        Button(
//            onClick = {
//                gamePaused = true
//                balloonView?.pauseGame()
//                onPause()
//            },
//            modifier = Modifier
//                .align(Alignment.TopEnd)
//                .padding(16.dp)
//        ) {
//            Text("Settings")
//        }
//    }
//}



//@Composable
//fun BalloonGameScreen(
//    onGameOver: () -> Unit,
//    onPause: () -> Unit,
//    onResume: () -> Unit,
//    mediaPlayer: MediaPlayer,
//    onMusicStatusChanged: (Boolean) -> Unit,
//    isMuted: Boolean,
//    onMuteStatusChanged: (Boolean) -> Unit
//) {
//    val context = LocalContext.current
//    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
//    var gamePaused by remember { mutableStateOf(false) }
//    var balloonView by remember { mutableStateOf<BalloonView?>(null) }
//
//    DisposableEffect(lifecycleOwner) {
//        val observer = MusicLifecycleObserver(mediaPlayer)
//        lifecycleOwner.lifecycle.addObserver(observer)
//
//        onDispose {
//            lifecycleOwner.lifecycle.removeObserver(observer)
//        }
//    }
//
//    Box(modifier = Modifier.fillMaxSize()) {
//        AndroidView(
//            factory = {
//                BalloonView(context).apply {
//                    this.onGameOver = onGameOver
//                    this.onPause = {
//                        gamePaused = true
//                        onPause()
//                    }
//                    this.onResume = {
//                        gamePaused = false
//                        onResume()
//                    }
//                    balloonView = this
//                    resetGame()
//                }
//            },
//            modifier = Modifier.fillMaxSize()
//        )
//
//        if (gamePaused) {
//            SettingsCard(
//                onBackToGame = {
//                    gamePaused = false
//                    balloonView?.resumeGame()
//                    onResume()
//                },
//                onRestartGame = {
//                    gamePaused = false
//                    balloonView?.resetGame()
//                    onResume()
//                },
//                mediaPlayer = mediaPlayer,
//                onMusicStatusChanged = onMusicStatusChanged,
//                isMuted = isMuted,
//                onMuteStatusChanged = onMuteStatusChanged
//            )
//        }
//
//        Button(
//            onClick = {
//                gamePaused = true
//                balloonView?.pauseGame()
//                onPause()
//            },
//            modifier = Modifier
//                .align(Alignment.TopEnd)
//                .padding(16.dp)
//        ) {
//            Text("Settings")
//        }
//    }
//}



@Composable
fun BalloonGameScreen(
    onGameOver: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    mediaPlayer: MediaPlayer,
    onMusicStatusChanged: (Boolean) -> Unit,
    isMuted: Boolean,
    onMuteStatusChanged: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    var gamePaused by rememberSaveable { mutableStateOf(false) }
    var balloonView by remember { mutableStateOf<BalloonView?>(null) }
    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = MusicLifecycleObserver(mediaPlayer)
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Handle back press to show exit dialog
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
                    this.onGameOver = onGameOver
                    this.onPause = {
                        gamePaused = true
                        onPause()
                    }
                    this.onResume = {
                        gamePaused = false
                        onResume()
                    }
                    balloonView = this
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
                        Text("yes")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = {
                        showExitDialog = false
                        gamePaused = false
                        balloonView?.resumeGame()
                        onResume()
                    }) {
                        Text("no")
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

        Button(
            onClick = {
                gamePaused = true
                balloonView?.pauseGame()
                onPause()
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text("Settings")
        }
    }
}
