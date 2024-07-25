package com.saba.balloongame

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameOverScreen(score: Int, onRestartClick: () -> Unit) {
    val luckiestGuyFontFamily = FontFamily(
        Font(R.font.luckiestguy, FontWeight.Normal)
    )

    // Check if the system is in dark theme
    val isDarkTheme = isSystemInDarkTheme()

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.game_over_icon),
                contentDescription = "Game Over Icon",
                modifier = Modifier.size(180.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Game Over",
                fontSize = 58.sp,
                fontFamily = luckiestGuyFontFamily,
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Score: $score",
                fontSize = 34.sp,
                fontFamily = luckiestGuyFontFamily,
                fontWeight = FontWeight.Bold,
                color = if (isDarkTheme) Color.White else Color.Black
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onRestartClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9000)),
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = "Restart",
                    fontSize = 30.sp,
                    fontFamily = luckiestGuyFontFamily,
                    color = Color.Black
                )
            }
        }
    }
}
