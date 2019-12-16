package hu.bme.aut.android.torpedo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import hu.bme.aut.android.torpedo.model.Game
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {

    private lateinit var registration: ListenerRegistration
    val db = FirebaseFirestore.getInstance()
    var game: Game = Game()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)



        game_resignButton.setOnClickListener {

            gameView.renderLoop!!.renderer.gameStarted = true
        }
        switch_gameboard.setOnClickListener{
            db.collection("games").document("Game1")
                .get()
                .addOnSuccessListener { document ->
                    var newGame = document.toObject(
                        Game::class.java)
                    gameView.renderLoop!!.renderer.game = newGame!!
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
            db.collection("games").document("Game1")
                .get()
                .addOnSuccessListener { document ->
                    var modifiedGame = document.toObject(
                        Game::class.java)

                    if(modifiedGame!!.firstPlayerTurn == gameView.renderLoop!!.renderer.isFirstPlayer)
                    {
                        correct = gameView.renderLoop!!.renderer.sendGuess()
                    }
                    else
                    {
                        // Not your turn
                        correct = false
                    }
                    if(correct)
                    {
                        var changedGame = gameView.renderLoop!!.renderer.game
                        if(modifiedGame!!.firstPlayerTurn)
                        {
                            for(index in 0..63)
                            {
                                if(changedGame.squares2!![index] == '2')
                                {
                                    if(modifiedGame.squares2!![index] == '0') modifiedGame.squares2 = modifiedGame.squares2!!.replaceRange(index, index+1, "1")
                                    if(modifiedGame.squares2!![index] == '4') modifiedGame.squares2 = modifiedGame.squares2!!.replaceRange(index, index+1, "3")
                                    modifiedGame.squaresSeen1 = modifiedGame.squaresSeen1!!.replaceRange(index, index+1, "1")
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
                                    if(modifiedGame.squares!![index] == '0') modifiedGame.squares = modifiedGame.squares!!.replaceRange(index, index+1, "1")
                                    if(modifiedGame.squares!![index] == '4') modifiedGame.squares = modifiedGame.squares!!.replaceRange(index, index+1, "3")
                                    modifiedGame.squaresSeen2 = modifiedGame.squaresSeen2!!.replaceRange(index, index+1, "1")
                                    changedGame.squaresSeen2 = changedGame.squaresSeen2!!.replaceRange(index, index+1, "1")
                                }
                            }
                        }
                        if(modifiedGame!!.firstPlayerTurn)
                        {
                            modifiedGame!!.firstPlayerTurn = false
                        }
                        else
                        {
                            modifiedGame!!.firstPlayerTurn = true
                        }
                        db.collection("games").document("Game1")
                            .set(modifiedGame)
                    }
                }
            db.collection("games").document("Game1")
                .get()
                .addOnSuccessListener { document ->
                    var newGame = document.toObject(
                        Game::class.java)
                    gameView.renderLoop!!.renderer.game = newGame!!
                }
            gameView.renderLoop!!.renderer.showOpponentBoard()


        }

    }

    override fun onStart()
    {
        super.onStart()
        registration = db.collection("games")
            .whereEqualTo("gameID", "Game1")
            .addSnapshotListener { result, e ->
                for (change in result!!.documentChanges) {
                    when (change.type) {
                        DocumentChange.Type.MODIFIED -> {
                            var modifiedgame = change.document.toObject(
                                Game::class.java
                            )
                            gameView.renderLoop!!.renderer.game = modifiedgame!!
                        }
                    }
                }
            }
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
