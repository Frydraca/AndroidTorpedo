package hu.bme.aut.android.torpedo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.w("LOBBY", "oncreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
    }

    override  fun onStart()
    {
        Log.w("LOBBY", "onstart")
        super.onStart()
    }

    override fun onStop() {
        Log.w("LOBBY", "onstop")
        super.onStop()
    }

    override fun onDestroy() {
        Log.w("LOBBY", "ondestroy")
        super.onDestroy()
    }
}
