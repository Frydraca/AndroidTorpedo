package hu.bme.aut.android.torpedo.rendering

import android.content.Context
import android.graphics.Canvas
import android.util.Log

class RenderLoop(
    context: Context,
    private val view: GameSetupView,
    width: Int,
    height: Int
) : Thread() {
    private val renderer = Renderer(context, width, height)

    var running = false

    override fun run() {
        while (running) {
            draw()
        }
    }

    private fun draw() {
        renderer.step()

        var canvas: Canvas? = null

        try {
            canvas = view.holder.lockCanvas()
            synchronized(view.holder) {
                Log.d("DRAW","calling draw")
                renderer.draw(canvas)
            }
        } finally {
            if (canvas != null) {
                view.holder.unlockCanvasAndPost(canvas)
            }
        }
    }

    fun squareClicked(xPos: Float, yPos: Float)
    {
        renderer.squareClicked(xPos, yPos)
    }

}