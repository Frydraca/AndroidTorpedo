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

class BrowserActivity : BaseActivity(), LobbyRecyclerViewAdapter.LobbyItemClickListener {

    private lateinit var registration: ListenerRegistration
    private lateinit var lobbyRecyclerViewAdapter: LobbyRecyclerViewAdapter
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
        val intent = Intent(this, GameSetupActivity::class.java)
        intent.putExtra("lobbyId", lobby.lobbyID)
        intent.putExtra("playerName", userEmail)
        intent.putExtra("player", "second")
        startActivity(intent)

    }

    override  fun onStart()
    {
        super.onStart()

        registration = db.collection("lobbies")
            .addSnapshotListener { result, e ->

                if (e != null) {
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

        createLobby.setOnClickListener {

            var newLobby = db.collection("lobbies").document()
            val lobbyHash = hashMapOf(
                "lobbyName" to "Lobby Name",
                "lobbyID" to newLobby.id,
                "firstPlayerName" to userEmail,
                "secondPlayerName" to "Waiting for opponent",
                "hasPassword" to false,
                "password" to "",
                "firstPlayerReady" to false,
                "secondPlayerReady" to false,
                "firstPlayerTurn" to true,
                "squares" to "",
                "squares2" to "",
                "squaresSeen1" to "",
                "squaresSeen2" to ""
            )

            newLobby.set(lobbyHash)

            val intent = Intent(this, GameSetupActivity::class.java)
            intent.putExtra("lobbyId", newLobby.id)
            intent.putExtra("playerName", "")
            intent.putExtra("player", "first")
            startActivity(intent)
        }

    }
    override fun onStop() {
        registration.remove()
        super.onStop()

    }





}
