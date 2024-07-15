package com.saba.balloongame

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.random.Random

class BalloonView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val balloons = mutableListOf<Balloon>()
    private val paint = Paint().apply {
        style = Paint.Style.FILL
    }
    private var score = 0
    private var speed = 10 // Initial speed
    private var lastSpeedChangeTime = System.currentTimeMillis()
    private var missedBalloonsCount = 0 // Count of missed balloons
    var onGameOver: (() -> Unit)? = null // Callback function for game over
    var onPause: (() -> Unit)? = null // Callback function for pausing the game
    var onResume: (() -> Unit)? = null // Callback function for resuming the game

    // Constant size for balloons
    private val balloonRadius = 85f

    // Colors defined as integer values
    private val balloonColors = intArrayOf(
        Color.parseColor("#ABFFF9"), Color.parseColor("#A075D5"), Color.parseColor("#F5A9ED"),
        Color.parseColor("#FF8E8E"), Color.parseColor("#FFFD86"), Color.parseColor("#B8FF79"),
        Color.parseColor("#8EADFF"), Color.parseColor("#8E9AFF"), Color.parseColor("#8F7BC8"),
        Color.parseColor("#99EEFF")
    )

    private val handler = Handler()
    private var isGameRunning = false
    private var isGamePaused = false
    private val maxBalloons = 20 // Increase maximum number of balloons

    init {
        resetGame()
    }

    fun startGame() {
        isGameRunning = true
        isGamePaused = false
        handler.post(updateRunnable)
        addBalloonWithDelay()
    }

    fun pauseGame() {
        isGameRunning = false
        isGamePaused = true
        handler.removeCallbacks(updateRunnable)
    }

    fun resumeGame() {
        isGameRunning = true
        isGamePaused = false
        handler.post(updateRunnable)
        addBalloonWithDelay()
    }

    private val updateRunnable = object : Runnable {
        override fun run() {
            if (!isGameRunning) return

            if (missedBalloonsCount >= 5) {
                onGameOver?.invoke()
                return
            }

            val balloonsToRemove = mutableListOf<Balloon>()
            for (balloon in balloons) {
                balloon.y -= speed // Balloon movement speed
                if (balloon.y + balloonRadius < 0) {
                    balloonsToRemove.add(balloon)
                    missedBalloonsCount++
                    if (missedBalloonsCount >= 5) {
                        onGameOver?.invoke()
                        return
                    }
                }
            }
            balloons.removeAll(balloonsToRemove)

            // Increase speed every 15 seconds
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastSpeedChangeTime >= 10000) {
                speed += 2 // Increase speed
                lastSpeedChangeTime = currentTime
            }

            invalidate()
            handler.postDelayed(this, 50) // Update every 50 milliseconds
        }
    }

    private fun addBalloonWithDelay() {
        handler.postDelayed({
            if (isGameRunning && balloons.size < maxBalloons) {
                balloons.add(generateRandomBalloon())
                addBalloonWithDelay()
            }
        }, Random.nextLong(300, 1000)) // Random delay between 0.3 to 1 second
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Draw balloons
        for (balloon in balloons) {
            paint.color = balloon.color
            canvas.drawCircle(balloon.x, balloon.y, balloonRadius, paint)
        }
        // Display score and missed balloons count
        paint.color = Color.BLACK
        paint.textSize = 50f
        canvas.drawText("Score: $score", 50f, 50f, paint)
        canvas.drawText("Missed: $missedBalloonsCount", 50f, 110f, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN && !isGamePaused) {
            // Check for balloon tap
            val iterator = balloons.iterator()
            while (iterator.hasNext()) {
                val balloon = iterator.next()
                if (balloon.contains(event.x, event.y, balloonRadius)) {
                    iterator.remove()
                    score++
                    break
                }
            }
            invalidate()
        }
        return true
    }

    private fun generateRandomBalloon(): Balloon {
        val x = balloonRadius + Random.nextFloat() * (width - 2 * balloonRadius)
        val y = height.toFloat() + balloonRadius // Enter from bottom of the screen
        return Balloon(x, y, balloonColors[Random.nextInt(balloonColors.size)])
    }

    fun resetGame() {
        isGameRunning = false
        handler.removeCallbacksAndMessages(null)
        score = 0
        speed = 10
        missedBalloonsCount = 0
        balloons.clear()
        // Ensure view is measured
        post {
            startGame()
        }
        lastSpeedChangeTime = System.currentTimeMillis()
    }

    data class Balloon(var x: Float, var y: Float, val color: Int) {
        fun contains(touchX: Float, touchY: Float, radius: Float): Boolean {
            val dx = touchX - x
            val dy = touchY - y
            return dx * dx + dy * dy <= radius * radius
        }
    }
}
