package hu.bme.aut.android.torpedo.model

import android.graphics.Canvas

interface Renderable {
    fun step()
    fun setSize(x: Int, y: Int)
    fun render(canvas: Canvas)
    fun setPosition(left: Int, top: Int, right: Int, bottom: Int)
    fun setImage()
}