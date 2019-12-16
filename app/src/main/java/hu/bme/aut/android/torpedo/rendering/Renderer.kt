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
    var shipTypePlaced = 0

    private val background = Background(context)
    val size = 130
    var game = Game()
    var showingSelfBoard: Boolean = true
    var isFirstPlayer: Boolean = false
    var gameStarted: Boolean = false
    init {
        background.setSize(width, height)

        var emptyString = ""
        for(i in 0..7) // columns
        {
            for(j in 0..7) // rows
            {
                val index = i*8+j
                val square = EmptySquare(context)
                square.setPosition(0+i*size,0+j*size, (1+i)*size, (1+j)*size)

//                square.state = game.squares!![index]
                entitiesToDraw.add(square)
                emptyString += '0'
            }
        }
        game.squares = emptyString
        game.squares2 = emptyString
        game.squaresSeen1 = emptyString
        game.squaresSeen2 = emptyString
    }

    fun step() {
        entitiesToDraw.forEach(Renderable::step)
    }

    fun draw(canvas: Canvas) {
        background.render(canvas)
        entitiesToDraw.forEach { drawable -> drawable.render(canvas) }
    }

    fun squareClicked(xPos: Float, yPos: Float)
    {
        var column = xPos.toInt()/size
        if(column > 7) column = 7
        var row = yPos.toInt()/size
        if(row > 7) row = 7
        val index: Int = row + column*8

        if(gameStarted && showingSelfBoard)
        {
            // do nothing
        }
        else if(gameStarted)
        {
            activateSquareGame(index)
        }
        else
        {
            activateSquareSetup(index)
        }
    }

    fun getbackGame() : Game
    {
        return game
    }

    fun setupGame(newGame: Game)
    {
        game = newGame
    }

    fun changeBoard(): Boolean
    {
        if(showingSelfBoard)
        {
            showOpponentBoard()
            showingSelfBoard = false
            return false
        }
        else
        {
            showSelfBoard()
            showingSelfBoard = true
            return true
        }
    }

    fun fixShips()
    {
        var newSquares = mutableListOf<Int>()
        var isCorrect = false

        newSquares = getIndexesOfGreenSquares()

        when(shipTypePlaced)
        {
            2 -> isCorrect = squaresAreCorrect(newSquares, 2)
            3 -> isCorrect = squaresAreCorrect(newSquares, 3)
            4 -> isCorrect = squaresAreCorrect(newSquares, 4)
            5 -> isCorrect = squaresAreCorrect(newSquares, 5)
        }
        if(isCorrect)
        {
            for(square in newSquares)
            {
                game.squares = game.squares!!.replaceRange(square, square+1, "4")
                entitiesToDraw[square].setImage('4')
            }
        }
    }

    fun resetAll()
    {
        for(i in 0..7) // columns
        {
            for(j in 0..7) // rows
            {
                val index: Int = j + i*8

                game.squares = game.squares!!.replaceRange(index, index+1, "0")
                entitiesToDraw[index].setImage('0')
            }
        }
    }

    fun resetShip()
    {
        for(i in 0..7) // columns
        {
            for(j in 0..7) // rows
            {
                val index: Int = j + i*8
                if(game.squares!![index] != '4')
                {
                    game.squares = game.squares!!.replaceRange(index, index+1, "0")
                    entitiesToDraw[index].setImage('0')
                }
            }
        }
    }

    fun getIndexesOfGreenSquares(): MutableList<Int>
    {
        var greenSquares = mutableListOf<Int>()

        for(i in 0..7) // columns
        {
            for(j in 0..7) // rows
            {
                val index: Int = j + i*8
                if(game.squares!![index] == '2')
                {
                    greenSquares.add(index)
                }
            }
        }

        return greenSquares
    }

    fun squaresAreCorrect(newSquares: MutableList<Int>, squareNumber: Int): Boolean
    {
        var common_column  = -1
        var common_row = -1
        var column_correct = true
        var row_correct = true
        var correctPlacement = true

        if(squareNumber == newSquares.size)
        {
            newSquares.sort()
            for(square in newSquares)
            {
                var column: Int = square / 8
                var row: Int = square % 8
                if(common_column == -1)
                {
                    common_column = column
                }
                if(common_row == -1)
                {
                    common_row = row
                }
                if(common_column != column)
                {
                    column_correct = false
                }
                if(common_row != row)
                {
                    row_correct = false
                }
                if(!column_correct && !row_correct)
                {
                    correctPlacement = false
                }
            }
            if(correctPlacement)
            {
                var previousIndex = -1
                for(square in newSquares)
                {
                    if(previousIndex != -1)
                    {
                        if((square - previousIndex) != 1 && (square - previousIndex) != 8)
                        {
                            correctPlacement = false
                        }
                    }
                    previousIndex = square
                }
            }
        }
        else
        {
            correctPlacement = false
        }

        return correctPlacement
    }

    fun showSelfBoard()
    {
        if(isFirstPlayer)
        {
            for(i in 0..7) // columns
            {
                for(j in 0..7) // rows
                {
                    entitiesToDraw[i*8+j].setImage(game.squares!![i*8+j])
                }
            }
        }
        else
        {
            for(i in 0..7) // columns
            {
                for(j in 0..7) // rows
                {
                    entitiesToDraw[i*8+j].setImage(game.squares2!![i*8+j])
                }
            }
        }
    }

    fun showOpponentBoard()
    {
        if(isFirstPlayer)
        {
            for(i in 0..7) // columns
            {
                for(j in 0..7) // rows
                {
                    if(game.squaresSeen1!![i*8+j] == '1')
                    {
                        entitiesToDraw[i*8+j].setImage(game.squares2!![i*8+j])
                    }
                    else
                    {
                        entitiesToDraw[i*8+j].setImage('0')
                    }
                }
            }
        }
        else
        {
            for(i in 0..7) // columns
            {
                for(j in 0..7) // rows
                {
                    if(game.squaresSeen2!![i*8+j] == '1')
                    {
                        entitiesToDraw[i*8+j].setImage(game.squares!![i*8+j])
                    }
                    else
                    {
                        entitiesToDraw[i*8+j].setImage('0')
                    }
                }
            }
        }
    }

    fun activateSquareSetup(index: Int)
    {
        if( game.squares!![index] == '2')
        {
            game.squares = game.squares!!.replaceRange(index, index+1, "0")
            entitiesToDraw[index].setImage('0')
        }
        else if (game.squares!![index] == '0')
        {
            game.squares = game.squares!!.replaceRange(index, index+1, "2")
            entitiesToDraw[index].setImage('2')
        }
    }

    fun activateSquareGame(index: Int)
    {
        if(isFirstPlayer)
        {
            if( game.squares2!![index] == '2')
            {
                game.squares2 = game.squares2!!.replaceRange(index, index+1, "0")
                entitiesToDraw[index].setImage('0')
            }
            else if (game.squaresSeen1!![index] != '1')
            {
                game.squares2 = game.squares2!!.replaceRange(index, index+1, "2")
                entitiesToDraw[index].setImage('2')
            }
        }
        else
        {
            if( game.squares!![index] == '2')
            {
                game.squares = game.squares!!.replaceRange(index, index+1, "0")
                entitiesToDraw[index].setImage('0')
            }
            else if (game.squaresSeen2!![index] != '1')
            {
                game.squares = game.squares!!.replaceRange(index, index+1, "2")
                entitiesToDraw[index].setImage('2')
            }
        }
    }

    fun sendGuess() : Boolean
    {
        var guessedSquares = mutableListOf<Int>()
        guessedSquares = getIndexesOfGreenSquares()
        if(guessedSquares.size == 1)
        {
            return true
        }

        return false
    }
}