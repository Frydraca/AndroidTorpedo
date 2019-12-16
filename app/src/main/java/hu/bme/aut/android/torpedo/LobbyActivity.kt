package hu.bme.aut.android.torpedo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import hu.bme.aut.android.torpedo.model.Lobby
import kotlinx.android.synthetic.main.activity_lobby.*

class LobbyActivity : BaseActivity() {

    private lateinit var registration: ListenerRegistration

    val db = FirebaseFirestore.getInstance()
    var lobbyId: String = ""
    var firstplayer: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)
        lobbyId = intent.getStringExtra("lobbyId")!!
        if(lobby_text.text == null || lobby_text.text == "")
        {
            lobby_text.text = "Waiting for opponent"
        }
        if(intent.getStringExtra("player") == "first")
        {
            firstplayer = true
        }

        lobby_readyButton.setOnClickListener {
            db.collection("lobbies").document(lobbyId)
                .get()
                .addOnSuccessListener { document ->
                    var modifiedLobby = document.toObject(
                        Lobby::class.java)
                        if(firstplayer)
                        {
                            modifiedLobby!!.firstPlayerReady = true
                        }
                        else
                        {
                            modifiedLobby!!.secondPlayerReady = true
                        }

                    if(modifiedLobby.firstPlayerReady && modifiedLobby.secondPlayerReady)
                    {
                        startGame()
                    }

                    db.collection("lobbies").document(lobbyId)
                        .set(modifiedLobby)
                }

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
                            if(firstplayer) lobby_text.text = modifiedLobby!!.secondPlayerName
                            else            lobby_text.text = modifiedLobby!!.firstPlayerName

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
        val intent = Intent(this, GameSetupActivity::class.java)
        intent.putExtra("lobbyId", lobbyId)
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
