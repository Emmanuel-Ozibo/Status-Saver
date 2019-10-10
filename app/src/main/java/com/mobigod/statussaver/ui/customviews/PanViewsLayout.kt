package com.mobigod.statussaver.ui.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout



class PanViewsLayout: RelativeLayout, View.OnTouchListener{

    private var _deltaX: Float = 0f
    private var _deltaY: Float = 0f

    lateinit var listener: FingerListener

    constructor(context: Context) : super(context){
       // init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
       // init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle){
        //init(attrs, defStyle)
    }


    override fun addView(view: View) {
        super.addView(view)
        view.setOnTouchListener(this)
    }


    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        val X = event.rawX
        val Y = event.rawY

        when (event.action and MotionEvent.ACTION_MASK){
            MotionEvent.ACTION_DOWN -> {
                val lParams = v!!.layoutParams as LayoutParams
                _deltaX = X - lParams.leftMargin
                _deltaY = Y - lParams.topMargin
            }

            MotionEvent.ACTION_UP -> {
                listener.onFingerStopMove()
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
            }
            MotionEvent.ACTION_POINTER_UP -> {
            }
            MotionEvent.ACTION_MOVE -> {
                listener.onFingerMove()
                val layoutParams = v!!.layoutParams as LayoutParams
                layoutParams.leftMargin = (X - _deltaX).toInt()
                layoutParams.topMargin = (Y - _deltaY).toInt()
                layoutParams.rightMargin = -250
                layoutParams.bottomMargin = -250
                v.layoutParams = layoutParams
            }
        }

        invalidate()
        return true
    }

    interface FingerListener {
        fun onFingerMove()
        fun onFingerStopMove()
    }

}