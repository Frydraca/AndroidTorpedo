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
import hu.bme.aut.android.torpedo.adapters.LobbyRecyclerViewAdapter
import hu.bme.aut.android.torpedo.model.Lobby
import kotlinx.android.synthetic.main.activity_browser.*

class BrowserActivity : AppCompatActivity(), LobbyRecyclerViewAdapter.LobbyItemClickListener {

    private lateinit var lobbyRecyclerViewAdapter: LobbyRecyclerViewAdapter
    private lateinit var button_createLobby: Button
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browser)

        db.collection("lobbies")
            .addSnapshotListener { result, e ->

                if (e != null) {
                    //Log.w("LOBBY", "Listen failed.", e)
                    return@addSnapshotListener
                }
                for (change in result!!.documentChanges) {
                    when (change.type) {
                        DocumentChange.Type.ADDED -> {
                            Log.w("LOBBY", "casting")
                            var newLobby = change.document.toObject(
                                Lobby::class.java
                            )
                            Log.w("LOBBY", "Listen failed: ${newLobby.lobbyName}")
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

//            val lobby = hashMapOf(
//                "lobbyName" to "Lobby1",
//                "firstPlayerName" to "Axi",
//                "secondPlayerName" to "Andris",
//                "hasPassword" to false,
//                "password" to ""
//
//            )
//            db.collection("lobbies")
//                .add(lobby)

        }
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
        startActivity(intent)

    }





}
