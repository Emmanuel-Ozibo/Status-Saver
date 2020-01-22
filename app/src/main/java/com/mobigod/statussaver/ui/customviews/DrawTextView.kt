package com.mobigod.statussaver.ui.customviews

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.elyeproj.drawtext.dpToPx
import com.elyeproj.drawtext.projectResources
import android.graphics.Bitmap
import kotlin.math.min
import kotlin.math.roundToInt
import android.graphics.Paint.DITHER_FLAG
import android.graphics.MaskFilter
import android.R.attr.strokeWidth
import android.R.attr.strokeWidth
import android.view.MotionEvent
import kotlin.math.abs
import android.graphics.BlurMaskFilter
import android.graphics.EmbossMaskFilter
import com.mobigod.statussaver.global.Tools
import java.util.*
import kotlin.collections.ArrayList


class DrawTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : View(context, attrs, defStyleAttr, defStyleRes) {

    companion object {
        private const val TEXT = "Tap to edit"
    }

    private val TAG = DrawTextView::class.simpleName

    private var drawText = TEXT
    private val drawTextCoordinate = Coordinate()
    private var previousCanvasColorInt: Int =  Tools.generateRandomColor()
    var isImageBackground = false
    private var BRUSH_SIZE = 20f
    val DEFAULT_COLOR = Color.RED
    private val TOUCH_TOLERANCE = 4f
    private var mX: Float = 0.toFloat()
    var mY: Float = 0.toFloat()
    private var mPath: Path? = null
    private var mPaint: Paint? = null
    private val paths = ArrayList<FingerPath>()


    private val srcRect = Rect().apply {
        left = getLeft()
        right = getRight()
        top = getTop()
        bottom = getBottom()

    }


    private val valueAnimator = ValueAnimator().apply {
        duration = 500
        setEvaluator(ArgbEvaluator())
        setIntValues(0xff000000.toInt(), previousCanvasColorInt)
    }


    var paintBrushMode = false
        set(value) {
            field = value
            initFingerPathUtils()
            invalidate()
        }

    var paintBrushColor = DEFAULT_COLOR
        set(value) {
            field = value
            invalidate()
        }

    var paintBrushSize = BRUSH_SIZE
    set(value) {
        field = value
        invalidate()
    }

    private fun initFingerPathUtils() {
        mPaint = Paint().apply {
            isAntiAlias = true
            isDither = true
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }


//        //if this is in picture mode, pls create a canvas with the width and height of the image bitmap
//        //else create an image with the device width and height
//        var canvasWidth = 0
//        var canvasHeight = 0
//
//        if (isImageBackground) {
//            if (backgroundImageBmp != null) {
//                val intArry = IntArray(2)
//                getScaledWidthAndHeight(backgroundImageBmp!!, intArry)
//                canvasWidth = intArry[0]
//                canvasHeight = intArry[1]
//            }
//        }else {
//            canvasWidth = resources.displayMetrics.widthPixels
//            canvasHeight = resources.displayMetrics.heightPixels
//        }
//
//        mBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888)
//        //mCanvas = Canvas(mBitmap)
    }


    var textTypedObj = TypedText(context)
    set(value) {
        field = value
        projectResources.paintText.apply {
            typeface = getTypeFace(value.fontRes)
            color = value.fontColor
        }

        customText = value.typedText!!
        fontSize = value.fontSize * 2
        invalidate()
    }

    private fun getTypeFace(fontRes: Int): Typeface? {
        if (fontRes == 0) {
            return Typeface.DEFAULT
        }
        return ResourcesCompat.getFont(context!!, fontRes)
    }


    @ColorInt
    var canvasColor: Int = previousCanvasColorInt
        set(value) {
            field = value
            valueAnimator.setIntValues(previousCanvasColorInt, value)
            invalidate()
        }

    var backgroundImageBmp: Bitmap? = null
        set(value) {
            field = value
            isImageBackground = true
            invalidate()
        }

    var customText: String = ""
        set(value) {
            field = value

            invalidate()
        }

    var drawBox: Boolean = false
        set(value) {
            field = value
            invalidate()
        }

    // This is to set to ensure the coordinate Y position is fix to the
    // Sample Text height, so that it doesn't move despite of the drawText height change
    // This make it prettier when perform drawing, so the text stay on the baseline regardless
    // of the drawText change height.
    var fixHeightCoordinate: Boolean = false
        set(value) {
            field = value
            invalidate()
        }

    var fontAntialias: Boolean = true
        set(value){
            field = value
            projectResources.paintText.isAntiAlias = field
            invalidate()
        }

    var fontHinting: Int = Paint.HINTING_ON
        set(value) {
            field = value
            projectResources.paintText.hinting = field
            invalidate()
        }

    var fontFakeBold: Boolean = false
        set(value) {
            field = value
            projectResources.paintText.isFakeBoldText = field
            invalidate()
        }

    var fontUnderline: Boolean = false
        set(value) {
            field = value
            projectResources.paintText.isUnderlineText = field
            invalidate()
        }

    var fontStrikeThrough: Boolean = false
        set(value) {
            field = value
            projectResources.paintText.isStrikeThruText = field
            invalidate()
        }

    var fontFeatureSetting: String = ""
        set(value) {
            field = value
            projectResources.paintText.fontFeatureSettings = field
            invalidate()
        }

    var fontScale: Float = 1.0f
        set(value) {
            field = value
            projectResources.paintText.textScaleX = field
            invalidate()
        }

    var fontSize: Int = 30
        set(value) {
            field = value
            projectResources.paintText.textSize = resources.dpToPx(field)
            invalidate()
        }

    var fontSkew: Float = 0f
        set(value) {
            field = value
            projectResources.paintText.textSkewX = field
            invalidate()
        }

    var letterSpacing: Float = 0f
        set(value) {
            field = value
            projectResources.paintText.letterSpacing = field
            invalidate()
        }

    var customCenter: Boolean = false
        set(value) {
            field = value
            invalidate()
        }

    var customAlign: Paint.Align = Paint.Align.CENTER
        set(value) {
            field = value
            projectResources.paintText.textAlign = field
            invalidate()
        }

    var customStyle: Paint.Style = Paint.Style.FILL
        set(value) {
            field = value
            projectResources.paintText.style = field
            invalidate()
        }


    var typeFace: Typeface = Typeface.DEFAULT
        set(value) {
            field = value
            projectResources.paintText.typeface = field
            invalidate()
        }

    private val sampleTextBound by lazy {
        val textBound = Rect()
        projectResources.paintText.getTextBounds(TEXT, 0, TEXT.length, textBound)
        textBound
    }

    private var originTextBound = calculateOriginTextBound()

    private fun calculateOriginTextBound(): Rect {
        val textBound = Rect()
        projectResources.paintText.getTextBounds(drawText, 0, drawText.length, textBound)
        return textBound
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawColor(canvasColor)
        drawText = if (customText.isBlank()) TEXT else customText


        if (isImageBackground) {
            drawImage(canvas)
        }

        if (paintBrushMode) {
            //draw all paint path on touch
            drawFingerPaths(canvas)
        }


        valueAnimator.addUpdateListener {
            val colorInt = it.animatedValue as Int
            previousCanvasColorInt = colorInt
            //canvas.drawColor(colorInt)
            //invalidate()
        }

        valueAnimator.start()


        if (width == 0 || height == 0) return

        originTextBound = calculateOriginTextBound()

        drawTextCoordinate.x = width / 2f

        if (fixHeightCoordinate) {
            if (customCenter) {
                drawTextCoordinate.y = height / 2f - sampleTextBound.exactCenterY()
            } else {
                drawTextCoordinate.y = height / 2f + sampleTextBound.calculateCenterY()
            }
        } else {
            if (customCenter) {
                drawTextCoordinate.y = height / 2f - originTextBound.exactCenterY()
            } else {
                drawTextCoordinate.y = height / 2f + originTextBound.calculateCenterY()
            }

        }
        canvas.drawText(drawText, drawTextCoordinate.x, drawTextCoordinate.y, projectResources.paintText)
    }

    private fun drawFingerPaths(canvas: Canvas) {
        //canvas.save()
        for (fp in paths) {
            mPaint?.color = fp.color
            mPaint?.strokeWidth = fp.paintBrushSize

            canvas.drawPath(fp.path, mPaint)
        }
        //canvas.restore()
    }


    private fun touchStart(x: Float, y: Float) {
        mPath = Path()
        val fp = FingerPath(paintBrushColor, paintBrushSize, mPath!!)
        paths.add(fp)

        mPath!!.reset()
        mPath!!.moveTo(x, y)
        mX = x
        mY = y

    }


    private fun touchMove(x: Float, y: Float) {
        val dx = Math.abs(x - mX)
        val dy = Math.abs(y - mY)

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath!!.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            mX = x
            mY = y
        }
    }


    private fun touchUp() {
        mPath!!.lineTo(mX, mY)
    }



    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x = event!!.x
        val y = event.y

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStart(x, y)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                touchMove(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                touchUp()
                invalidate()
            }
        }

        return true
    }


    private fun drawImage(canvas: Canvas) {
        canvas.drawColor(0xFF000000.toInt())//Black background
        if (backgroundImageBmp != null) {

            drawText = ""//todo: for now, design a more efficient way to draw all this

            val intArry = IntArray(2)
            getScaledWidthAndHeight(backgroundImageBmp!!, intArry)


            val leftRect = 0
            val rightRect = width
            val topRect = (height - intArry[0]) / 2
            val bottomRect = topRect + intArry[1]


            srcRect.apply {
                top = topRect
                left = leftRect
                right = rightRect
                bottom = bottomRect
            }


            Log.i(TAG, "Bitmap's Width: ${backgroundImageBmp?.width}, Bitmap's Height: ${backgroundImageBmp?.height}")
            Log.i(TAG, "FINAL RECT: top: $topRect, bottom: $bottomRect, left: $leftRect, right: $rightRect")

            canvas.drawBitmap(backgroundImageBmp!!, null, srcRect, null)
        }
    }


    private fun getScaledWidthAndHeight(bitmap: Bitmap, widthHeight: IntArray) {
        val ratio = min(width.toFloat() / bitmap.width,
            height.toFloat() / bitmap.height)

        val mwidth = (ratio * bitmap.width).roundToInt()
        val mheight = (ratio * bitmap.height).roundToInt()

        widthHeight[0] = mwidth
        widthHeight[1] = mheight
    }


    private fun Rect.calculateCenterY(): Float {
        return abs((bottom - top) / 2f)
    }

    private fun Rect.calculateCenterX(): Float {
        return abs((right - left) / 2f)
    }

    class Coordinate(var x: Float = 0f, var y: Float = 0f)

    class TypedText(context: Context) {
        var fontRes: Int = 0
        var fontColor: Int =  ContextCompat.getColor(context, android.R.color.white)
        var typedText: String? = ""
        var fontSize: Int = 0
    }
}


data class FingerPath (
    var color: Int,
    var paintBrushSize: Float,
    var path: Path
)