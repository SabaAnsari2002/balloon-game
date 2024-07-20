package com.saba.balloongame

import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.random.Random

interface GameStateListener {
    fun onScoreChanged(newScore: Int)
    fun onHighScoreChanged(newHighScore: Int)
    fun onNewHighScore(newHighScore: Int)
    fun onGameOver()
}

class BalloonView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val balloons = mutableListOf<Balloon>()
    private val paint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
        color = if (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
            Color.WHITE
        } else {
            Color.BLACK
        }
    }
    private var score = 0
    private var highScore = 0
    private var speed = 10 // Initial speed
    private var lastSpeedChangeTime = System.currentTimeMillis()
    private var missedBalloonsCount = 0 // Count of missed balloons
    private var pausedSpeed = speed
    var onGameOver: (() -> Unit)? = null // Callback function for game over
    var onPause: (() -> Unit)? = null // Callback function for pausing the game
    var onResume: (() -> Unit)? = null // Callback function for resuming the game
    var gameStateListener: GameStateListener? = null
    private var isFirstGame = true
    private var hasShownHighScoreMessage = false
    private val sharedPreferences = context.getSharedPreferences("GamePrefs", Context.MODE_PRIVATE)
    private var lastGoldenBalloonTime = System.currentTimeMillis()

    // برای پیگیری ترکاندن بادکنک‌های مشابه
    private var lastPoppedBalloonType: Int? = null
    private var consecutivePops = 0

    // آخرین امتیاز افزوده شده
    private var lastAddedScore = 0

    // Load the balloon images
    private val balloonBitmaps = listOf(
        BitmapFactory.decodeResource(resources, R.drawable.balloon1),
        BitmapFactory.decodeResource(resources, R.drawable.balloon2),
        BitmapFactory.decodeResource(resources, R.drawable.balloon3),
        BitmapFactory.decodeResource(resources, R.drawable.balloon4),
        BitmapFactory.decodeResource(resources, R.drawable.balloon5),
        BitmapFactory.decodeResource(resources, R.drawable.balloon6),
        BitmapFactory.decodeResource(resources, R.drawable.balloon7),
        BitmapFactory.decodeResource(resources, R.drawable.balloon8),
        BitmapFactory.decodeResource(resources, R.drawable.balloon) // Golden balloon image
    )

    private val balloonRadius = 128f

    private val handler = Handler()
    private var isGameRunning = false
    private var isGamePaused = false
    private val maxBalloons = 20 // Increase maximum number of balloons

    fun Context.isDarkMode(): Boolean {
        return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
    init {
        highScore = sharedPreferences.getInt("highScore", 0)
        isFirstGame = highScore == 0
        resetGame()
    }

    fun startGame() {
        isGameRunning = true
        isGamePaused = false
        handler.post(updateRunnable)
        addBalloonWithDelay()
    }

    fun pauseGame() {
        if (isGameRunning) {
            isGameRunning = false
            isGamePaused = true
            pausedSpeed = speed
            handler.removeCallbacks(updateRunnable)
            onPause?.invoke()
        }
    }

    fun resumeGame() {
        if (isGamePaused) {
            isGameRunning = true
            isGamePaused = false
            speed = pausedSpeed
            handler.post(updateRunnable)
            addBalloonWithDelay()
            onResume?.invoke()
        }
    }

    private val updateRunnable = object : Runnable {
        override fun run() {
            if (!isGameRunning) return

            if (missedBalloonsCount >= 5) {
                gameStateListener?.onGameOver()
                onGameOver?.invoke()
                return
            }

            val balloonsToRemove = mutableListOf<Balloon>()
            for (balloon in balloons) {
                balloon.y -= speed
                if (balloon.y + balloonRadius < 0) {
                    balloonsToRemove.add(balloon)
                    missedBalloonsCount++
                    if (missedBalloonsCount >= 5) {
                        gameStateListener?.onGameOver()
                        onGameOver?.invoke()
                        return
                    }
                }
            }
            balloons.removeAll(balloonsToRemove)

            val currentTime = System.currentTimeMillis()
            if (currentTime - lastSpeedChangeTime >= 5000) {
                speed += 2
                lastSpeedChangeTime = currentTime
            }

            invalidate()
            handler.postDelayed(this, 50)
        }
    }

    private fun addBalloonWithDelay() {
        handler.postDelayed({
            if (isGameRunning && balloons.size < maxBalloons) {
                balloons.add(generateRandomBalloon())
                addBalloonWithDelay()
            }
        }, Random.nextLong(300, 800)) // You can adjust these values to change balloon generation frequency
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (balloon in balloons) {
            val bitmap = balloon.bitmap
            val left = balloon.x - balloonRadius
            val top = balloon.y - balloonRadius
            val right = balloon.x + balloonRadius
            val bottom = balloon.y + balloonRadius
            canvas.drawBitmap(bitmap, null, RectF(left, top, right, bottom), paint)
        }
        paint.color = if (context.isDarkMode()) Color.WHITE else Color.BLACK
        paint.textSize = 50f
        canvas.drawText("Score: $score", 50f, 50f, paint)
        canvas.drawText("High Score: $highScore", 50f, 110f, paint)
        canvas.drawText("Missed: $missedBalloonsCount", 50f, 170f, paint)
        canvas.drawText("Last Added: $lastAddedScore", 50f, 230f, paint) // رسم امتیاز آخرین بادکنک ترکیده
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN && !isGamePaused) {
            val iterator = balloons.iterator()
            while (iterator.hasNext()) {
                val balloon = iterator.next()
                if (balloon.contains(event.x, event.y, balloonRadius)) {
                    iterator.remove()
                    updateScore(balloon)
                    break
                }
            }
            invalidate()
        }
        return true
    }

    private fun updateScore(balloon: Balloon) {
        // اگر بادکنک طلایی باشد
        if (balloon.isGolden) {
            lastAddedScore = 5
        } else {
            // بررسی ترکاندن بادکنک مشابه
            if (lastPoppedBalloonType == balloon.type) {
                consecutivePops++
            } else {
                lastPoppedBalloonType = balloon.type
                consecutivePops = 1
            }
            lastAddedScore = consecutivePops
        }

        score += lastAddedScore
        gameStateListener?.onScoreChanged(score)
        if (score > highScore && !isFirstGame && !hasShownHighScoreMessage) {
            highScore = score
            sharedPreferences.edit().putInt("highScore", highScore).apply()
            gameStateListener?.onNewHighScore(highScore)
            hasShownHighScoreMessage = true
        }
        invalidate()
    }

    private fun generateRandomBalloon(): Balloon {
        val x = balloonRadius + Random.nextFloat() * (width - 2 * balloonRadius)
        val y = height.toFloat() + balloonRadius
        val currentTime = System.currentTimeMillis()
        val isGolden = currentTime - lastGoldenBalloonTime >= 5000 && Random.nextFloat() < 0.1 // 10% chance to generate a golden balloon
        if (isGolden) {
            lastGoldenBalloonTime = currentTime
        }
        val type = if (isGolden) 8 else Random.nextInt(8)
        val bitmap = balloonBitmaps[type]
        return Balloon(x, y, bitmap, isGolden, type)
    }

    fun resetGame() {
        isGameRunning = false
        isFirstGame = false
        hasShownHighScoreMessage = false
        handler.removeCallbacksAndMessages(null)
        score = 0
        speed = 10
        pausedSpeed = speed
        missedBalloonsCount = 0
        balloons.clear()
        lastPoppedBalloonType = null
        consecutivePops = 0
        isFirstGame = false
        lastAddedScore = 0
        gameStateListener?.onScoreChanged(score)
        gameStateListener?.onHighScoreChanged(highScore)
        post {
            startGame()
        }
        lastSpeedChangeTime = System.currentTimeMillis()
    }

    fun saveHighScore() {
        if (score > highScore) {
            highScore = score
            sharedPreferences.edit().putInt("highScore", highScore).apply()
            gameStateListener?.onNewHighScore(highScore)
        }
    }

    fun getScore(): Int = score
    fun getHighScore(): Int = highScore

    data class Balloon(
        var x: Float,
        var y: Float,
        val bitmap: Bitmap,
        val isGolden: Boolean = false,
        val type: Int
    ) {
        fun contains(touchX: Float, touchY: Float, radius: Float): Boolean {
            val dx = x - touchX
            val dy = y - touchY
            return dx * dx + dy * dy <= radius * radius
        }
    }
}
