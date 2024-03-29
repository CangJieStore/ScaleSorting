package com.cangjie.frame.core

import android.annotation.SuppressLint
import android.os.Handler
import android.view.MotionEvent
import android.view.View

/**
 * @author nvwa@cangjie
 * Create by AS at 2020/7/7 11:25
 */

private const val INTERVAL = 300L


data class ClickCallback(
    var singleTap: () -> Unit = {},
    var doubleTap: () -> Unit = {}
)

class DoubleClickListener(
    init: ClickCallback.() -> Unit
) : View.OnTouchListener {
    private val callback = ClickCallback().apply(init)
    private var count = 0
    private val handler = Handler()

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            count++
            handler.postDelayed({
                if (count == 1) callback.singleTap()
                else if (count == 2) callback.doubleTap()
                handler.removeCallbacksAndMessages(0)
                count = 0
            }, INTERVAL)
        }

        return false
    }
}