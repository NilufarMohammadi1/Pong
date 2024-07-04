package ut.ac.cps.pong

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class Pong(context: Context) : SurfaceView(context), Runnable,
    SensorEventListener {
    // Variables for game objects and properties
    private var gameThread: Thread? = null
    private val surfaceHolder: SurfaceHolder = holder

    @Volatile
    private var playing = false
    private val paused = true
    private val paint: Paint = Paint()
    private val sensorManager: SensorManager
    private val accelerometer: Sensor
    private val rotationSensor: Sensor
    private val racket: Racket
    private val ball: Ball

    init {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)
        racket = Racket()
        ball = Ball(20f)
    }

    // Game loop
    override fun run() {
        while (playing) {
            update()
            draw()
            control()
        }
    }

    // Update game objects and properties
    private fun update() {
        racket.setViewWidth(width)
        racket.update()
        ball.setViewWidth(width)
        ball.setViewHeight(height)
        ball.update()
        ball.applyCollision(racket)
    }

    // Draw game objects on the canvas
    private fun draw() {
        if (surfaceHolder.surface.isValid) {
            val canvas = surfaceHolder.lockCanvas()
            canvas.drawColor(Color.BLACK)
            racket.draw(canvas, paint)
            ball.draw(canvas, paint)
            surfaceHolder.unlockCanvasAndPost(canvas)
        }
    }

    // Control the game loop speed
    private fun control() {
        try {
            Thread.sleep(UPDATE_RATE_MS.toLong())
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    // Handle changes in the accelerometer sensor values
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_GAME_ROTATION_VECTOR) {
            val currentRotation = event.values[2]
            racket.updateRotation(currentRotation)
        }
        if (event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) {
            val currentAcc = event.values[0]
            racket.updateAcc(currentAcc)
        }
    }

    // Method to resume the game
    fun resume() {
        playing = true
        gameThread = Thread(this)
        gameThread!!.start()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_GAME)
    }

    // Method to pause the game
    fun pause() {
        playing = false
        try {
            gameThread!!.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Not used
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (e.action == MotionEvent.ACTION_MOVE) {
            ball.reset()
            racket.reset()
        }
        return true
    }

    companion object {
        const val FRACTION = 1f
        var UPDATE_RATE_MS = 20
        var DELTA_IN_SECONDS = UPDATE_RATE_MS / 1000f
        var DESK_WIDTH = 0.5f
    }
}