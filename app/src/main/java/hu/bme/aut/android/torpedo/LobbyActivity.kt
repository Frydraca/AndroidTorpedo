package hu.bme.aut.android.torpedo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import hu.bme.aut.android.torpedo.model.Lobby
import kotlinx.android.synthetic.main.activity_lobby.*

class LobbyActivity : AppCompatActivity() {

    private lateinit var registration: ListenerRegistration

    val db = FirebaseFirestore.getInstance()
    var lobbyName: String? = null
    var firstplayer: Boolean = false
    var firstPlayerReady: Boolean = false
    var secondPlayerReady: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)
        lobbyName = intent.getStringExtra("lobbyName")
        lobby_text.text = intent.getStringExtra("oppName")
        if(lobby_text.text == null || lobby_text.text == "")
        {
            lobby_text.text = "Waiting for opponent"
        }
        if(intent.getStringExtra("player") == "first")
        {
            firstplayer = true
        }

        lobby_readyButton.setOnClickListener {
            Log.w("DRAW","ready pressed")
            val lName = lobbyName
            if(firstplayer)
            {

                db.collection("lobbies").document(lName!!)
                    .get()
                    .addOnSuccessListener { document ->
                        var modifiedLobby = document.toObject(
                            Lobby::class.java)
                        modifiedLobby!!.firstPlayerReady = true
                        db.collection("lobbies").document(lName!!)
                            .set(modifiedLobby)
                    }
            }
            else
            {
                db.collection("lobbies").document(lName!!)
                    .get()
                    .addOnSuccessListener { document ->
                        var modifiedLobby = document.toObject(
                            Lobby::class.java)
                        modifiedLobby!!.secondPlayerReady = true
                        db.collection("lobbies").document(lName!!)
                            .set(modifiedLobby)
                    }
            }
        }

    }

    override fun onStart() {
        super.onStart()
        Log.w("DRAW","lobbyname start ${lobbyName}")
        registration = db.collection("lobbies")
            .whereEqualTo("lobbyName", lobbyName)
            .addSnapshotListener { result, e ->
                for (change in result!!.documentChanges) {
                    when (change.type) {
                        DocumentChange.Type.MODIFIED -> {
                            var modifiedLobby = change.document.toObject(
                                Lobby::class.java
                            )
                            lobby_text.text = modifiedLobby.secondPlayerName
                            firstPlayerReady = modifiedLobby.firstPlayerReady
                            secondPlayerReady = modifiedLobby.secondPlayerReady

                            if(firstPlayerReady && secondPlayerReady)
                            {
                                startGame(modifiedLobby)
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

    fun startGame(lobby: Lobby)
    {
        val intent = Intent(this, GameSetupActivity::class.java)
        intent.putExtra("lobbyName", lobbyName)
        intent.putExtra("gameName", lobby.gameID)
        if(firstplayer)
        {
            intent.putExtra("player", "first")
        }
        else
        {
            intent.putExtra("player", "second")
        }

        startActivity(intent)
    }
}
