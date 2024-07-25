package com.saba.balloongame

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults

val LuckiestGuyFontFamily = FontFamily(Font(R.font.luckiestguy))
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
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) Color.Black else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)  // Set the background color of the Box
    ) {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .width(300.dp)
                .height(400.dp),
            elevation = CardDefaults.cardElevation(4.dp) // Optional: Add elevation if needed
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Settings",
                    fontSize = 40.sp,
                    fontFamily = LuckiestGuyFontFamily,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        muteState.value = !muteState.value
                        onMuteStatusChanged(muteState.value)
                        if (muteState.value) {
                            mediaPlayer.pause()
                            onMusicStatusChanged(false)
                        } else {
                            onMusicStatusChanged(true)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)),
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Text(
                        if (muteState.value) "Unmute" else "Mute",
                        color = textColor,
                        fontSize = 20.sp,
                        fontFamily = LuckiestGuyFontFamily
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        onBackToGame()
                        if (!muteState.value) onMusicStatusChanged(true)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)),
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Text(
                        "Back to Game",
                        color = textColor,
                        fontSize = 20.sp,
                        fontFamily = LuckiestGuyFontFamily
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        onRestartGame()
                        if (!muteState.value) onMusicStatusChanged(true)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500)),
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Text(
                        "Restart Game",
                        color = textColor,
                        fontSize = 20.sp,
                        fontFamily = LuckiestGuyFontFamily
                    )
                }
            }
        }
    }
}
