package com.example.androidbasedml.utils

import android.view.View

class DoubleClick {
    abstract class OnDoubleClickListener : View.OnClickListener {

        var lastClickTime: Long = 0
        override fun onClick(v: View?) {
            var clickTime = System.currentTimeMillis()
            if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                onDoubleClick(v)
                lastClickTime = 0
            }
            lastClickTime = clickTime

        }

        abstract fun onDoubleClick(v: View?)

        companion object {
            const val DOUBLE_CLICK_TIME_DELTA: Long = 400 //millis
        }
    }
}