package hu.bme.aut.android.torpedo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import hu.bme.aut.android.torpedo.model.Game
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    var game: Game = Game()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)


        Log.w("GAME","readyfirst ${intent.getStringExtra("oppName")}")

        game_send.setOnClickListener {
            db.collection("games").document("Game1")
                .get()
                .addOnSuccessListener { document ->
                    var newGame = document.toObject(
                        Game::class.java)
                    game = newGame!!
                }
            Log.w("GAME","first player board ${game.squares}")
            Log.w("GAME","second player board ${game.squares2}")
            gameView.renderLoop!!.renderer.setupGame(game)
        }
        switch_gameboard.setOnClickListener{
            gameView.renderLoop!!.renderer.changeBoard()
        }

    }

    override fun onStart()
    {
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
