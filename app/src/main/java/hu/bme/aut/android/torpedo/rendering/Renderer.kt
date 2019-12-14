package hu.bme.aut.android.torpedo.rendering

import android.content.Context
import android.graphics.Canvas
import android.util.Log
import hu.bme.aut.android.torpedo.model.Background
import hu.bme.aut.android.torpedo.model.EmptySquare
import hu.bme.aut.android.torpedo.model.Game
import hu.bme.aut.android.torpedo.model.Renderable
import java.util.*

class Renderer(
    private val context: Context,
    private val width: Int,
    private val height: Int
) {
    private val entitiesToDraw = mutableListOf<Renderable>()

    private val background = Background(context)
    val size = 130
    var game = Game()
    var squaresPlayer1: IntArray = IntArray(64)
    var squaresPlayer2: IntArray = IntArray(64)
    var showingFirstPlayer: Boolean = true
    init {
        background.setSize(width, height)
        // squares
        for(i in 0..7) // columns
        {
            for(j in 0..7) // rows
            {
                val square = EmptySquare(context)
                square.setPosition(0+i*size,0+j*size, (1+i)*size, (1+j)*size)

                square.state = squaresPlayer1[i*8+j]
                entitiesToDraw.add(square)
            }
        }

        // ships


    }

    fun step() {

        entitiesToDraw.forEach(Renderable::step)
    }

    fun draw(canvas: Canvas) {
        background.render(canvas)
        entitiesToDraw.forEach { drawable -> drawable.render(canvas)
                                              }
    }

    fun squareClicked(xPos: Float, yPos: Float)
    {
        var column = xPos.toInt()/size
        if(column > 7) column = 7
        var row = yPos.toInt()/size
        if(row > 7) row = 7
        val index: Int = row + column*8
        squaresPlayer1[index] = 2
        entitiesToDraw[index].setImage(2)
    }

    fun getbackGame() : Game
    {
        game.squares = ""
        game.squares2 = ""
        for(square in squaresPlayer1)
        {
            game.squares += square.toString()
        }
        for(square in squaresPlayer2)
        {
            game.squares2 += square.toString()
        }
        return game
    }

    fun setupGame(newGame: Game)
    {
        game.squares = ""
        game.squares2 = ""
        game.squares = newGame.squares
        game.squares2 = newGame.squares2

        if(game.squares != null)
        {
            var str: String = game.squares!!
            var str2: String = game.squares2!!
            for(i in 0..63)
            {
                squaresPlayer1[i] = str[i].toInt() - 48
                squaresPlayer2[i] = str2[i].toInt() - 48
            }
        }

        for(i in 0..7) // columns
        {
            for(j in 0..7) // rows
            {
                entitiesToDraw[i*8+j].setImage(squaresPlayer1[i*8+j])
            }
        }
    }

    fun changeBoard()
    {
        if(showingFirstPlayer)
        {
            for(i in 0..7) // columns
            {
                for(j in 0..7) // rows
                {
                    entitiesToDraw[i*8+j].setImage(squaresPlayer2[i*8+j])
                }
            }
            showingFirstPlayer = false
        }
        else
        {
            for(i in 0..7) // columns
            {
                for(j in 0..7) // rows
                {
                    entitiesToDraw[i*8+j].setImage(squaresPlayer1[i*8+j])
                }
            }
            showingFirstPlayer = true
        }
    }

}