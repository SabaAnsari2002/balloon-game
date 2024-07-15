package com.saba.balloongame

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.media.MediaPlayer

@Composable
fun SettingsCard(
    onBackToGame: () -> Unit,
    onRestartGame: () -> Unit,
    mediaPlayer: MediaPlayer
) {
    val volume = remember { mutableStateOf(0.5f) }

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

                Text(text = "Volume", fontSize = 18.sp)
                Slider(
                    value = volume.value,
                    onValueChange = {
                        volume.value = it
                        mediaPlayer.setVolume(it, it)
                    },
                    valueRange = 0f..1f,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onBackToGame) {
                    Text("Back to Game")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onRestartGame) {
                    Text("Restart Game")
                }
            }
        }
    }
}
