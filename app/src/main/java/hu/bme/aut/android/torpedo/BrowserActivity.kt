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



        button_createLobby = findViewById(R.id.createLobby) as Button

        button_createLobby.setOnClickListener {
//            val demoData = mutableListOf(
//                Lobby("title1", "Axi", false, "description1")
//            )
            val lobby = hashMapOf(
                "name" to "Lobby1",
                "hostName" to "Axi",
                "password" to "",
                "hasPassword" to false
            )
            db.collection("lobbies")
                .add(lobby)
                .addOnSuccessListener { documentReference ->
                    Log.d("LOBBY", "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("LOBBY", "Error adding document", e)
                }

            db.collection("lobbies")
                .get()
//            lobbyRecyclerViewAdapter.addAll(demoData)
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
