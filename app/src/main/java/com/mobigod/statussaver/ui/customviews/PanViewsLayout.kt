package com.mobigod.statussaver.ui.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.RelativeLayout
import kotlin.math.max
import kotlin.math.min


class PanViewsLayout: RelativeLayout, View.OnTouchListener {

    private var _deltaX: Float = 0f
    private var _deltaY: Float = 0f
    lateinit var listener: FingerListener
    private var mScaleFactor = 1f
    private var currentView: View? = null
    private var isScaling = false



    private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
            isScaling = true
            return super.onScaleBegin(detector)
        }

        override fun onScaleEnd(detector: ScaleGestureDetector?) {
            isScaling = false
            super.onScaleEnd(detector)
        }


        override fun onScale(detector: ScaleGestureDetector): Boolean {
            mScaleFactor *= detector.scaleFactor

            // Don't let the object get too small or too large.
            mScaleFactor = max(0.1f, min(mScaleFactor, 5.0f))

            if (currentView != null) {
                currentView.apply {
                    scaleX = mScaleFactor
                    scaleY = mScaleFactor
                }
            }
            ///invalidate()
            return true
        }
    }

    private val mScaleDetector = ScaleGestureDetector(context, scaleListener)



    constructor(context: Context) : super(context) {
       // init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
       // init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle){
        //init(attrs, defStyle)
    }


    override fun addView(view: View) {
//        val layoutParams = view.layoutParams as
//        layoutParams.addRule(CENTER_IN_PARENT)
//        view.layoutParams = layoutParams
        super.addView(view)
        view.setOnTouchListener(this)
    }


    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        val X = event.rawX
        val Y = event.rawY

        currentView = v
        mScaleDetector.onTouchEvent(event)

        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                val lParams = v!!.layoutParams as LayoutParams
                _deltaX = X - lParams.leftMargin
                _deltaY = Y - lParams.topMargin
            }

            MotionEvent.ACTION_UP -> {
                listener.onFingerStopMove()
            }

            MotionEvent.ACTION_POINTER_DOWN -> {}
            MotionEvent.ACTION_POINTER_UP -> {}
            MotionEvent.ACTION_MOVE -> {
                listener.onFingerMove(v)
                if (!isScaling) {
                    val layoutParams = v!!.layoutParams as LayoutParams
                    layoutParams.leftMargin = (X - _deltaX).toInt()
                    layoutParams.topMargin = (Y - _deltaY).toInt()
                    layoutParams.rightMargin = - 50
                    layoutParams.bottomMargin = - 50
                    v.layoutParams = layoutParams
                }
            }
        }

        invalidate()
        return true
    }

    interface FingerListener {
        fun onFingerMove(view: View?)
        fun onFingerStopMove()
    }

}