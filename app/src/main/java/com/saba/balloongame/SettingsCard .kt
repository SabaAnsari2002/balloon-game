package com.saba.balloongame

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.media.MediaPlayer
@Composable
fun SettingsCard(
    onBackToGame: () -> Unit,
    onRestartGame: () -> Unit,
    mediaPlayer: MediaPlayer,
    onMusicStatusChanged: (Boolean) -> Unit,
    isMuted: Boolean,
    onMuteStatusChanged: (Boolean) -> Unit
) {
    val muteState = remember { mutableStateOf(isMuted) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Card(
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Settings", fontSize = 24.sp)
                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    muteState.value = !muteState.value
                    onMuteStatusChanged(muteState.value)
                    if (muteState.value) {
                        mediaPlayer.pause()
                        onMusicStatusChanged(false)
                    } else {
                        onMusicStatusChanged(true) // Ensure music continues to play when returning to game
                    }
                }) {
                    Text(if (muteState.value) "Unmute" else "Mute")
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    onBackToGame()
                    if (!muteState.value) onMusicStatusChanged(true) // Ensure music continues to play when returning to game
                }) {
                    Text("Back to Game")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    onRestartGame()
                    if (!muteState.value) onMusicStatusChanged(true) // Ensure music continues to play when restarting game
                }) {
                    Text("Restart Game")
                }
            }
        }
    }
}
