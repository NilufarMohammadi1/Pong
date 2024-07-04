package ut.ac.cps.pong

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import kotlin.math.abs
import kotlin.math.sign

class Racket {
    private var speed: Float = 0f
    private var acc: Float = 0f
    var rotation: Float? = null
        private set
    private var position: Float = 540f
    private var racketWidth: Float? = null
    val racketRect: RectF = RectF()
    private var viewWidth: Int = 0

    init {
        init()
    }

    private fun init() {
        speed = 0f
        acc = 0f
        rotation = 0f
        position = 540f
        racketWidth = 0f
    }

    fun updateAcc(currentAcc: Float?) {
        acc = if (abs(currentAcc!!) < 0.5) 0f else {
            if (sign(speed) != sign(currentAcc)) sign(
                currentAcc
            ) * DESCENDING_ACC_SENSITIVITY_FACTOR else sign(
                currentAcc
            ) * ASCENDING_ACC_SENSITIVITY_FACTOR
        }
    }

    fun updateRotation(currentRotation: Float) {
        rotation = Math.toDegrees(currentRotation.toDouble()).toFloat() * -2
    }

    fun update() {
        position += speed * Pong.DELTA_IN_SECONDS * viewWidth / Pong.DESK_WIDTH
        speed += acc * Pong.DELTA_IN_SECONDS
        speed -= sign(speed) * Pong.FRACTION
        if (abs(speed) < 0.7f) speed = 0f
        if (position + racketWidth!! / 2 < 0) {
            position = -1 * racketWidth!! / 2
            speed = 0f
        }
        if (position - racketWidth!! / 2 > viewWidth) {
            speed = 0f
            position = viewWidth - racketWidth!! / 2
        }
    }

    fun draw(canvas: Canvas, paint: Paint) {
        paint.color = Color.WHITE
        racketWidth = canvas.width / 3f
        racketRect.left = position - racketWidth!! / 2
        racketRect.top =
            (canvas.height - canvas.height / 4).toFloat()
        racketRect.right =
            position + racketWidth!! / 2
        racketRect.bottom = racketRect.top + 10

        canvas.save()
        canvas.rotate(rotation!!, racketRect.centerX(), racketRect.centerY())
        canvas.drawRect(racketRect, paint)
        canvas.restore()

    }

    fun setViewWidth(width: Int) {
        viewWidth = width
    }

    fun reset() {
        init()
    }

    companion object {
        const val ASCENDING_ACC_SENSITIVITY_FACTOR = 10f
        const val DESCENDING_ACC_SENSITIVITY_FACTOR = 0.1f
    }
}