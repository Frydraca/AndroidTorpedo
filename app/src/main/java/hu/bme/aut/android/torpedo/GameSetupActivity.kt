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
import com.google.firebase.firestore.MetadataChanges
import hu.bme.aut.android.torpedo.model.Game
import hu.bme.aut.android.torpedo.model.Lobby
import kotlinx.android.synthetic.main.activity_game_setup.*
import kotlinx.android.synthetic.main.activity_lobby.*

class GameSetupActivity : BaseActivity() {

    private lateinit var registration: ListenerRegistration
    val db = FirebaseFirestore.getInstance()
    var lobbyId: String = ""
    var firstplayer: Boolean = false
    var firstPlayerReady: Boolean = false
    var secondPlayerReady: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_setup)

        lobbyId = intent.getStringExtra("lobbyId")!!
        if(intent.getStringExtra("player") == "first")
        {
            firstplayer = true
            setup_gameView.renderLoop!!.renderer.isFirstPlayer = true
        }

        db.collection("lobbies").document(lobbyId)
            .get()
            .addOnSuccessListener { document ->
                var modifiedLobby = document.toObject(
                    Lobby::class.java)
                modifiedLobby!!.firstPlayerReady = false
                modifiedLobby!!.secondPlayerReady = false
                db.collection("lobbies").document(lobbyId)
                    .set(modifiedLobby)
            }

        setup_readyButton.setOnClickListener {

            val game = setup_gameView.renderLoop!!.renderer.lobby
            if(firstplayer)
            {
                db.collection("lobbies").document(lobbyId)
                    .get()
                    .addOnSuccessListener { document ->
                        var modifiedLobby = document.toObject(
                            Lobby::class.java)
                        modifiedLobby!!.squares = game.squares
                        modifiedLobby!!.squaresSeen1 = game.squaresSeen1
                        modifiedLobby!!.firstPlayerReady = true

                        if(modifiedLobby.firstPlayerReady && modifiedLobby.secondPlayerReady)
                        {
                            startGame()
                        }
                        db.collection("lobbies").document(lobbyId)
                            .set(modifiedLobby)
                    }
            }
            else
            {
                db.collection("lobbies").document(lobbyId)
                    .get()
                    .addOnSuccessListener { document ->
                        var modifiedLobby = document.toObject(
                            Lobby::class.java)
                        modifiedLobby!!.squares2 = game.squares
                        modifiedLobby!!.squaresSeen2 = game.squaresSeen2
                        modifiedLobby!!.secondPlayerReady = true

                        if(modifiedLobby.firstPlayerReady && modifiedLobby.secondPlayerReady)
                        {
                            startGame()
                        }
                        db.collection("lobbies").document(lobbyId)
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
            .document(lobbyId)
            .addSnapshotListener (MetadataChanges.INCLUDE) { snapshot, exception ->
                var pending = snapshot!!.metadata.hasPendingWrites()

                if(pending == false){
                    var modifiedLobby = snapshot.toObject(
                        Lobby::class.java
                    )

                     if(modifiedLobby!!.firstPlayerReady && modifiedLobby!!.secondPlayerReady)
                     {
                         startGame()
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
        intent.putExtra("lobbyId", lobbyId)
        startActivity(intent)
    }

}

