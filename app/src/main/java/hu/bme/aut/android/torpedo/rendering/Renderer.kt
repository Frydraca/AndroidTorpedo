package hu.bme.aut.android.torpedo.rendering

import android.content.Context
import android.graphics.Canvas
import android.util.Log
import hu.bme.aut.android.torpedo.model.Background
import hu.bme.aut.android.torpedo.model.EmptySquare
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

    init {
        background.setSize(width, height)
        // squares
        for(i in 0..7) // columns
        {
            for(j in 0..7) // rows
            {
                val square = EmptySquare(context)
                square.setPosition(0+i*size,0+j*size, (1+i)*size, (1+j)*size)

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
        Log.w("DRAW","draw called on render")
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
        entitiesToDraw[index].setImage()
    }

}