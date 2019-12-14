package hu.bme.aut.android.torpedo

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import hu.bme.aut.android.torpedo.adapters.LobbyRecyclerViewAdapter
import hu.bme.aut.android.torpedo.model.Lobby
import kotlinx.android.synthetic.main.activity_browser.*

class BrowserActivity : AppCompatActivity(), LobbyRecyclerViewAdapter.LobbyItemClickListener {

    private lateinit var registration: ListenerRegistration
    private lateinit var lobbyRecyclerViewAdapter: LobbyRecyclerViewAdapter
    private lateinit var button_createLobby: Button
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browser)


        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        lobbyRecyclerViewAdapter = LobbyRecyclerViewAdapter()
        rvApplications.layoutManager =  LinearLayoutManager(this).apply {
                reverseLayout = true
                stackFromEnd = true
            }
        rvApplications.adapter = lobbyRecyclerViewAdapter
        lobbyRecyclerViewAdapter.itemClickListener = this
    }

    override fun onItemClick(lobby: Lobby) {
        val intent = Intent(this, LobbyActivity::class.java)
        intent.putExtra("lobbyName", lobby.lobbyName)
        intent.putExtra("oppName", lobby.firstPlayerName)
        intent.putExtra("player", "second")
        startActivity(intent)

    }

    override  fun onStart()
    {
        registration = db.collection("lobbies")
            .addSnapshotListener { result, e ->

                if (e != null) {
                    //Log.w("LOBBY", "Listen failed.", e)
                    return@addSnapshotListener
                }
                for (change in result!!.documentChanges) {
                    when (change.type) {
                        DocumentChange.Type.ADDED -> {
                            var newLobby = change.document.toObject(
                                Lobby::class.java
                            )
                            newLobby.lobbyID = change.document.id
                            lobbyRecyclerViewAdapter.add(newLobby)
                        }
                        DocumentChange.Type.MODIFIED -> {
                            var modifiedLobby = change.document.toObject(
                                Lobby::class.java
                            )
                            modifiedLobby.lobbyID = change.document.id
                            lobbyRecyclerViewAdapter.update(modifiedLobby)
                        }
                        DocumentChange.Type.REMOVED -> {
                            var deletedLobby = change.document.toObject(
                                Lobby::class.java
                            )
                            deletedLobby.lobbyID = change.document.id
                            lobbyRecyclerViewAdapter.delete(deletedLobby)
                        }
                    }
                }
            }

        button_createLobby = findViewById(R.id.createLobby) as Button

        button_createLobby.setOnClickListener {

            val lobby = hashMapOf(
                "lobbyName" to "Lobby1",
                "firstPlayerName" to "Axi",
                "secondPlayerName" to "Andris",
                "hasPassword" to false,
                "password" to "",
                "firstPlayerReady" to false,
                "secondPlayerReady" to false,
                "gameID" to "Game1"

            )
            db.collection("lobbies").document("Lobby1").set(lobby)

            val gameHash = hashMapOf(
                "player1" to "Axi",
                "player2" to "Andris",
                "firstPlayerTurn" to true,
                "squares" to "",
                "squares2" to ""
            )

            db.collection("games").document("Game1").set(gameHash)

            val intent = Intent(this, LobbyActivity::class.java)
            intent.putExtra("lobbyName", "Lobby1")
            intent.putExtra("player", "first")
            startActivity(intent)

        }

        super.onStart()
    }
    override fun onStop() {
        registration.remove()
        super.onStop()

    }





}
