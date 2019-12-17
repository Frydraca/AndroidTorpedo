package hu.bme.aut.android.torpedo.rendering

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameSetupView : SurfaceView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var isFirstPlayer = false
    var renderLoop: RenderLoop? = null

    init {
        holder.addCallback(object : SurfaceHolder.Callback {

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                var retry = true
                renderLoop?.running = false
                while (retry) {
                    try {
                        renderLoop?.join()
                        retry = false
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

                val loop = RenderLoop(context, this@GameSetupView, width, height)
                loop.running = true
                loop.start()

                renderLoop = loop
                renderLoop!!.renderer.isFirstPlayer = isFirstPlayer
            }
        })
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)

        var action = event!!.action
        when(action)
        {
            MotionEvent.ACTION_DOWN -> {
                renderLoop!!.squareClicked(event!!.x, event!!.y)
            }
        }

        return true
    }


}