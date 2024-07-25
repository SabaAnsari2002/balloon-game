import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.saba.balloongame.R

@Composable
fun HomeScreen(onStartClick: () -> Unit) {
    val luckiestGuyFontFamily = FontFamily(
        Font(R.font.luckiestguy, FontWeight.Normal)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.icon),
            contentDescription = "Home Icon",
            modifier = Modifier.size(250.dp)
        )
        Spacer(modifier = Modifier.height(60.dp))
        Button(
            onClick = onStartClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFE257)
            ),
            modifier = Modifier
                .border(5.dp, Color.Black, RoundedCornerShape(20.dp))
                .background(Color(0xFFFFE257), RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Start",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = luckiestGuyFontFamily,
                color = Color.Black
            )
        }
    }
}