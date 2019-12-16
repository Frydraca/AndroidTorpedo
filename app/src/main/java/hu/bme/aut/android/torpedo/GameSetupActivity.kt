package hu.bme.aut.android.torpedo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import hu.bme.aut.android.torpedo.model.Game
import hu.bme.aut.android.torpedo.model.Lobby
import kotlinx.android.synthetic.main.activity_game_setup.*

class GameSetupActivity : AppCompatActivity() {

    private lateinit var registration: ListenerRegistration
    val db = FirebaseFirestore.getInstance()
    var lobbyName: String? = null
    var gameName: String? = null
    var firstplayer: Boolean = false
    var firstPlayerReady: Boolean = false
    var secondPlayerReady: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_setup)

        lobbyName = intent.getStringExtra("lobbyName")
        gameName = intent.getStringExtra("gameName")
        if(intent.getStringExtra("player") == "first")
        {
            firstplayer = true
            setup_gameView.renderLoop!!.renderer.isFirstPlayer = true
        }

        db.collection("lobbies").document(lobbyName!!)
            .get()
            .addOnSuccessListener { document ->
                var modifiedLobby = document.toObject(
                    Lobby::class.java)
                modifiedLobby!!.firstPlayerReady = false
                modifiedLobby!!.secondPlayerReady = false
                db.collection("lobbies").document(lobbyName!!)
                    .set(modifiedLobby)
            }

        setup_readyButton.setOnClickListener {

            val game = setup_gameView.renderLoop!!.renderer.getbackGame()

            if(firstplayer)
            {
                db.collection("games").document(gameName!!)
                    .get()
                    .addOnSuccessListener { document ->
                        var modifiedGame = document.toObject(
                            Game::class.java)
                        modifiedGame!!.squares = game.squares
                        modifiedGame!!.squaresSeen1 = game.squaresSeen1
                        db.collection("games").document(gameName!!)
                            .set(modifiedGame)
                    }

                db.collection("lobbies").document(lobbyName!!)
                    .get()
                    .addOnSuccessListener { document ->
                        var modifiedLobby = document.toObject(
                            Lobby::class.java)
                        modifiedLobby!!.firstPlayerReady = true
                        db.collection("lobbies").document(lobbyName!!)
                            .set(modifiedLobby)
                    }
            }
            else
            {

                db.collection("games").document("Game1")
                    .get()
                    .addOnSuccessListener { document ->
                        var modifiedGame = document.toObject(
                            Game::class.java)
                        modifiedGame!!.squares2 = game.squares
                        modifiedGame!!.squaresSeen2 = game.squaresSeen2
                        db.collection("games").document(gameName!!)
                            .set(modifiedGame)
                    }

                db.collection("lobbies").document(lobbyName!!)
                    .get()
                    .addOnSuccessListener { document ->
                        var modifiedLobby = document.toObject(
                            Lobby::class.java)
                        modifiedLobby!!.secondPlayerReady = true
                        db.collection("lobbies").document(lobbyName!!)
                            .set(modifiedLobby)
                    }
            }

        }

        accept_button.setOnClickListener{
            setup_gameView.renderLoop!!.renderer.fixShips()
        }

        reset_all_button.setOnClickListener{
            setup_gameView.renderLoop!!.renderer.resetAll()
        }

        reset_ship_button.setOnClickListener{
            setup_gameView.renderLoop!!.renderer.resetShip()
        }

        ship_5.setOnClickListener {
            setup_gameView.renderLoop!!.renderer.shipTypePlaced = 5
        }
        ship_4.setOnClickListener {
            setup_gameView.renderLoop!!.renderer.shipTypePlaced = 4
        }
        ship_3.setOnClickListener {
            setup_gameView.renderLoop!!.renderer.shipTypePlaced = 3
        }
        ship_2.setOnClickListener {
            setup_gameView.renderLoop!!.renderer.shipTypePlaced = 2
        }
    }

    override fun onStart() {
        super.onStart()
        registration = db.collection("lobbies")
            .whereEqualTo("lobbyName", lobbyName)
            .addSnapshotListener { result, e ->
                for (change in result!!.documentChanges) {
                    when (change.type) {
                        DocumentChange.Type.MODIFIED -> {
                            var modifiedLobby = change.document.toObject(
                                Lobby::class.java
                            )
                            firstPlayerReady = modifiedLobby.firstPlayerReady
                            secondPlayerReady = modifiedLobby.secondPlayerReady

                            if(firstPlayerReady && secondPlayerReady)
                            {
                                startGame()
                            }
                        }
                    }
                }
            }
    }

    override fun onStop() {
        registration.remove()
        super.onStop()
    }

    fun startGame()
    {
        setup_gameView.renderLoop!!.running = false
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }

}

