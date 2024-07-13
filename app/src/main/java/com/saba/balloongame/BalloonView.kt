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

    // تعریف رنگ‌ها به صورت مقادیر صحیح
    private val balloonColors = intArrayOf(
        Color.parseColor("#ABFFF9"), Color.parseColor("#A075D5"), Color.parseColor("#F5A9ED"),
        Color.parseColor("#FF8E8E"), Color.parseColor("#FFFD86"), Color.parseColor("#B8FF79"),
        Color.parseColor("#8EADFF"), Color.parseColor("#8E9AFF"), Color.parseColor("#8F7BC8"),
        Color.parseColor("#99EEFF")
    )

    init {
        // تولید بادکنک‌های اولیه
        for (i in 1..10) {
            balloons.add(generateRandomBalloon())
        }

        // تنظیم انیمیشن حرکت بادکنک‌ها
        val handler = Handler()
        handler.post(object : Runnable {
            override fun run() {
                val balloonsToRemove = mutableListOf<Balloon>()
                val balloonsToAdd = mutableListOf<Balloon>()
                for (balloon in balloons) {
                    balloon.y -= speed // سرعت حرکت بادکنک‌ها
                    if (balloon.y < 0) {
                        balloonsToRemove.add(balloon)
                        balloonsToAdd.add(generateRandomBalloon())
                    }
                }
                balloons.removeAll(balloonsToRemove)
                balloons.addAll(balloonsToAdd)
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
        // نمایش امتیاز
        paint.color = Color.BLACK
        paint.textSize = 50f
        canvas.drawText("Score: $score", 50f, 50f, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            // بررسی ترکاندن بادکنک
            val iterator = balloons.iterator()
            val balloonsToRemove = mutableListOf<Balloon>()
            while (iterator.hasNext()) {
                val balloon = iterator.next()
                if (balloon.contains(event.x, event.y)) {
                    balloonsToRemove.add(balloon)
                    score++
                    break
                }
            }
            balloons.removeAll(balloonsToRemove)
            // اضافه کردن بادکنک جدید
            if (balloons.size < 10) {
                balloons.add(generateRandomBalloon())
            }
            invalidate()
        }
        return true
    }

    private fun generateRandomBalloon(): Balloon {
        return Balloon(
            x = Random.nextFloat() * width,
            y = height.toFloat(),
            radius = 50f + Random.nextFloat() * 50f,
            color = balloonColors[Random.nextInt(balloonColors.size)]
        )
    }

    data class Balloon(var x: Float, var y: Float, val radius: Float, val color: Int) {
        fun contains(touchX: Float, touchY: Float): Boolean {
            val dx = touchX - x
            val dy = touchY - y
            return dx * dx + dy * dy <= radius * radius
        }
    }
}
