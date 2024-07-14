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
    private var speed = 10 // سرعت اولیه
    private var lastSpeedChangeTime = System.currentTimeMillis()
    private var missedBalloonsCount = 0 // شمارش بادکنک‌های نترکیده و خارج شده
    var onGameOver: (() -> Unit)? = null // تعریف یک تابع برای هندل کردن پایان بازی

    // تعریف رنگ‌ها به صورت مقادیر صحیح
    private val balloonColors = intArrayOf(
        Color.parseColor("#ABFFF9"), Color.parseColor("#A075D5"), Color.parseColor("#F5A9ED"),
        Color.parseColor("#FF8E8E"), Color.parseColor("#FFFD86"), Color.parseColor("#B8FF79"),
        Color.parseColor("#8EADFF"), Color.parseColor("#8E9AFF"), Color.parseColor("#8F7BC8"),
        Color.parseColor("#99EEFF")
    )

    init {
        resetGame()
        // تنظیم انیمیشن حرکت بادکنک‌ها
        val handler = Handler()
        handler.post(object : Runnable {
            override fun run() {
                if (missedBalloonsCount >= 5) {
                    onGameOver?.invoke()
                    return
                }

                val balloonsToRemove = mutableListOf<Balloon>()
                for (balloon in balloons) {
                    balloon.y -= speed // سرعت حرکت بادکنک‌ها
                    if (balloon.y + balloon.radius < 0) {
                        balloonsToRemove.add(balloon)
                        missedBalloonsCount++
                        if (missedBalloonsCount >= 5) {
                            onGameOver?.invoke()
                            return
                        }
                    }
                }
                balloons.removeAll(balloonsToRemove)
                while (balloons.size < 10) {
                    balloons.add(generateRandomBalloon())
                }
                invalidate()

                // افزایش سرعت هر ۱۵ ثانیه
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastSpeedChangeTime >= 15000) {
                    speed += 5 // افزایش سرعت
                    lastSpeedChangeTime = currentTime
                }

                handler.postDelayed(this, 50) // به‌روزرسانی هر 50 میلی‌ثانیه
            }
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // رسم بادکنک‌ها
        for (balloon in balloons) {
            paint.color = balloon.color
            canvas.drawCircle(balloon.x, balloon.y, balloon.radius, paint)
        }
        // نمایش امتیاز و تعداد بادکنک‌های نترکیده
        paint.color = Color.BLACK
        paint.textSize = 50f
        canvas.drawText("Score: $score", 50f, 50f, paint)
        canvas.drawText("Missed: $missedBalloonsCount", 50f, 110f, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            // بررسی ترکاندن بادکنک
            val iterator = balloons.iterator()
            while (iterator.hasNext()) {
                val balloon = iterator.next()
                if (balloon.contains(event.x, event.y)) {
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
        val radius = 50f + Random.nextFloat() * 50f
        val x = radius + Random.nextFloat() * (width - 2 * radius)
        val y = height.toFloat() + radius // از پایین صفحه وارد شود
        return Balloon(x, y, radius, balloonColors[Random.nextInt(balloonColors.size)])
    }

    fun resetGame() {
        score = 0
        speed = 10
        missedBalloonsCount = 0
        balloons.clear()
        // اطمینان از اینکه View اندازه‌گیری شده است
        post {
            for (i in 1..10) {
                balloons.add(generateRandomBalloon())
            }
            invalidate()
        }
        lastSpeedChangeTime = System.currentTimeMillis()
    }

    data class Balloon(var x: Float, var y: Float, val radius: Float, val color: Int) {
        fun contains(touchX: Float, touchY: Float): Boolean {
            val dx = touchX - x
            val dy = touchY - y
            return dx * dx + dy * dy <= radius * radius
        }
    }
}