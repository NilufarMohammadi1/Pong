package ut.ac.cps.pong

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class Ball(private val RADIUS: Float) {
    private var viewWidth: Int? = null
    private var viewHeight: Int? = null
    private var x: Float? = null
    private var y: Float? = null
    private var speedX: Float = 0f
    private var speedY: Float = 0f
    private var G = 0f

    init {
        init()
    }

    private fun init() {
        x = 540f
        y = 0f
        speedX = 0f
        speedY = 0f
    }

    fun setViewWidth(width: Int) {
        viewWidth = width
    }

    fun setViewHeight(height: Int) {
        viewHeight = height
        G = 2f / 3f * viewHeight!! / 1225
    }

    fun update() {
        speedY += G

        // Update ball position
        x = x!! + speedX
        y = y!! + speedY

        // Check for collision with walls
        if (x!! - RADIUS < 0 || x!! + RADIUS > viewWidth!!) {
            speedX = -speedX
        }

        // Check for collision with walls
        if (y!! - RADIUS < 0 || y!! + RADIUS > viewHeight!!) {
            speedY = -speedY
        }
    }

    private fun isCollided(leftPoint: PointF, rightPoint: PointF): Boolean {
        if (x!! > rightPoint.x || x!! < leftPoint.x) return false
        val tempY =
            (leftPoint.y - rightPoint.y) / (leftPoint.x - rightPoint.x) * (x!! - leftPoint.x) + leftPoint.y
        return y!! + abs(speedY) >= tempY && y!! - abs(
            speedY
        ) <= tempY
    }

    fun applyCollision(racket: Racket) {
        val racketRect: RectF = racket.racketRect
        val leftPoint = PointF()
        val rightPoint = PointF()
        leftPoint.x =
            racketRect.left + (1f - cos(Math.toRadians(racket.rotation!!.toDouble()))).toFloat() * racketRect.width() / 2
        rightPoint.x =
            racketRect.right - (1f - cos(Math.toRadians(racket.rotation!!.toDouble()))).toFloat() * racketRect.width() / 2
        leftPoint.y = racketRect.top + sin(Math.toRadians(racket.rotation!!.toDouble()))
            .toFloat() * racketRect.width() / 2
        rightPoint.y = racketRect.top - sin(Math.toRadians(racket.rotation!!.toDouble()))
            .toFloat() * racketRect.width() / 2
        if (isCollided(leftPoint, rightPoint)) {
            val tempSin = sin(2 * Math.toRadians(racket.rotation!!.toDouble())).toFloat()
            val tempCos = cos(2 * Math.toRadians(racket.rotation!!.toDouble())).toFloat()
            val newSpeedY = -1 * speedX * tempSin - speedY * tempCos
            val newSpeedX = speedX * tempCos + speedY * tempSin
            speedX = newSpeedX
            speedY = newSpeedY
        }
    }

    fun draw(canvas: Canvas, paint: Paint) {
        // Draw ball
        paint.color = Color.RED
        canvas.drawCircle(x!!, y!!, RADIUS, paint)
    }

    fun reset() {
        init()
    }
}