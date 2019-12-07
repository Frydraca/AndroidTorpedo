package hu.bme.aut.android.torpedo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_game_setup.*

class GameSetupActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_setup)

        setup_readyButton.setOnClickListener {
            gameView.renderLoop!!.running = false
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }
    }


}

