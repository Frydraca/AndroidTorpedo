package hu.bme.aut.android.torpedo.model

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import hu.bme.aut.android.torpedo.R

class EmptySquare(private val context: Context) : Renderable {

    private var leftPosition: Int = 0
    private var topPosition: Int = 0
    private var rightPosition: Int = 0
    private var bottomPosition: Int = 0
    private lateinit var bitmapDrawable: BitmapDrawable

    var image = BitmapFactory.decodeResource(context.resources, R.drawable.empty)
    var state = '0' // 0 - empty, 1 - crossed, 2 - ship, 3 - shipcrossed, 4 - fixedShip


    override fun step() {


    }

    override fun setPosition(left: Int, top: Int, right: Int, bottom: Int) {
        this.leftPosition = left
        this.topPosition = top
        this.rightPosition = right
        this.bottomPosition = bottom

    }

    override fun render(canvas: Canvas) {

         val dest = Rect(leftPosition, topPosition, rightPosition, bottomPosition)
         canvas.drawBitmap(image, null, dest, null)

    }

    override fun setSize(x: Int, y: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setImage(newState: Char)
    {
        if(newState == '0')
        {
            image = BitmapFactory.decodeResource(context.resources, R.drawable.empty)
        }
        else if(newState == '1')
        {
            image = BitmapFactory.decodeResource(context.resources, R.drawable.crossed)
        }
        else if(newState == '2')
        {
            image = BitmapFactory.decodeResource(context.resources, R.drawable.ship)
        }
        else if(newState == '3')
        {
            image = BitmapFactory.decodeResource(context.resources, R.drawable.ship_crossed)
        }
        else if(newState == '4')
        {
            image = BitmapFactory.decodeResource(context.resources, R.drawable.ship_fixed)
        }

    }

}