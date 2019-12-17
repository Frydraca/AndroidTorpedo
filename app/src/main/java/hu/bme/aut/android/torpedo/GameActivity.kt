package hu.bme.aut.android.torpedo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.messaging.FirebaseMessaging
import hu.bme.aut.android.torpedo.model.Game
import hu.bme.aut.android.torpedo.model.Lobby
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : BaseActivity() {

    private lateinit var registration: ListenerRegistration
    private lateinit var userKey: String
    val db = FirebaseFirestore.getInstance()
    var lobbyId: String = ""
    var game: Game = Game()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        lobbyId = intent.getStringExtra("lobbyId")!!

        game_resignButton.setOnClickListener {
            Toast.makeText(this@GameActivity, "You lost!", Toast.LENGTH_SHORT).show()
        }
        switch_gameboard.setOnClickListener{
            db.collection("lobbies").document(lobbyId)
                .get()
                .addOnSuccessListener { document ->
                    var newLobby = document.toObject(
                        Lobby::class.java)
                    gameView.renderLoop!!.renderer.lobby = newLobby!!
                    gameView.renderLoop!!.renderer.gameStarted = true
                }
            if(gameView.renderLoop!!.renderer.changeBoard())
            {
                board.text = "Your Board"
            }
            else
            {
                board.text = "The Board"
            }

        }
        game_send.setOnClickListener{
            var correct = true
            db.collection("lobbies").document(lobbyId)
                .get()
                .addOnSuccessListener { document ->
                    var modifiedLobby = document.toObject(
                        Lobby::class.java)

                    if(modifiedLobby!!.firstPlayerTurn == gameView.renderLoop!!.renderer.isFirstPlayer)
                    {
                        correct = gameView.renderLoop!!.renderer.sendGuess()
                    }
                    else
                    {
                        Toast.makeText(this@GameActivity, "Not your turn!", Toast.LENGTH_SHORT).show()
                        correct = false
                    }
                    if(correct)
                    {
                        var changedGame = gameView.renderLoop!!.renderer.lobby
                        if(modifiedLobby!!.firstPlayerTurn)
                        {
                            for(index in 0..63)
                            {
                                if(changedGame.squares2!![index] == '2')
                                {
                                    if(modifiedLobby.squares2!![index] == '0') modifiedLobby.squares2 = modifiedLobby.squares2!!.replaceRange(index, index+1, "1")
                                    if(modifiedLobby.squares2!![index] == '4') modifiedLobby.squares2 = modifiedLobby.squares2!!.replaceRange(index, index+1, "3")
                                    modifiedLobby.squaresSeen1 = modifiedLobby.squaresSeen1!!.replaceRange(index, index+1, "1")
                                    changedGame.squaresSeen1 = changedGame.squaresSeen1!!.replaceRange(index, index+1, "1")
                                }
                            }
                        }
                        else
                        {
                            for(index in 0..63)
                            {
                                if(changedGame.squares!![index] == '2')
                                {
                                    if(modifiedLobby.squares!![index] == '0') modifiedLobby.squares = modifiedLobby.squares!!.replaceRange(index, index+1, "1")
                                    if(modifiedLobby.squares!![index] == '4') modifiedLobby.squares = modifiedLobby.squares!!.replaceRange(index, index+1, "3")
                                    modifiedLobby.squaresSeen2 = modifiedLobby.squaresSeen2!!.replaceRange(index, index+1, "1")
                                    changedGame.squaresSeen2 = changedGame.squaresSeen2!!.replaceRange(index, index+1, "1")
                                }
                            }
                        }
                        if(modifiedLobby!!.firstPlayerTurn)
                        {
                            modifiedLobby!!.firstPlayerTurn = false
                        }
                        else
                        {
                            modifiedLobby!!.firstPlayerTurn = true
                        }
                        db.collection("lobbies").document(lobbyId)
                            .set(modifiedLobby)
                    }
                }
            db.collection("lobbies").document(lobbyId)
                .get()
                .addOnSuccessListener { document ->
                    var newLobby = document.toObject(
                        Lobby::class.java)
                    gameView.renderLoop!!.renderer.lobby = newLobby!!
                }
            gameView.renderLoop!!.renderer.showOpponentBoard()


        }

    }

    override fun onStart()
    {
        super.onStart()
        registration = db.collection("lobbies")
            .whereEqualTo("lobbyID", lobbyId)
            .addSnapshotListener  { result, exception ->
                for (dc in result!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.MODIFIED -> {
                            var modifiedLobby = dc.document.toObject(
                                Lobby::class.java
                            )

                            if (gameView.renderLoop!!.renderer.checkForGameWon(modifiedLobby!!)) {
                                // game ended you won
                                Toast.makeText(this@GameActivity, "You won!", Toast.LENGTH_SHORT).show()
                            }
                            if (gameView.renderLoop!!.renderer.checkForGameLost(modifiedLobby!!))
                            {
                                // game ended you lost
                                Toast.makeText(this@GameActivity, "You lost!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }


        FirebaseMessaging.getInstance().subscribeToTopic(lobbyId)
            .addOnCompleteListener {
                Log.w("GAME", "onstop")

            }
    }

    override fun onStop() {
        Log.w("LOBBY", "onstop")
        super.onStop()
        registration.remove()
    }

    override fun onDestroy() {
        Log.w("LOBBY", "ondestroy")
        super.onDestroy()
    }

    fun goBackToBrowser()
    {

    }
}
