package ut.ac.cps.pong

import android.app.Activity
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : Activity() {
    private var pongGame: Pong? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pongGame = Pong(context = this)
        setContentView(pongGame)
    }

    override fun onResume() {
        super.onResume()
        pongGame?.resume()
    }

    override fun onPause() {
        super.onPause()
        pongGame?.pause()
    }
}
