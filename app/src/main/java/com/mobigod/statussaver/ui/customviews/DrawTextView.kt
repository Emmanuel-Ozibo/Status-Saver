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


class DrawTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) : View(context, attrs, defStyleAttr, defStyleRes) {

    companion object {
        private const val TEXT = "Tap to edit"
    }

    private val TAG = DrawTextView::class.simpleName

    private var drawText = TEXT
    private val drawTextCoordinate = Coordinate()
    private var previousCanvasColorInt: Int = 0xFF9c095f.toInt()
    var isImageBackground = false
    private var imageBackgroundBitmap: Bitmap? = null
    private var bitmapPaint: Paint = Paint().apply {
        isAntiAlias = true
        isFilterBitmap = true
        isDither = true
    }

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
            canvas.drawColor(0xFF000000.toInt())//Black background
            if (backgroundImageBmp != null) {

                drawText = ""//todo: for now, design a more efficient way to draw all this

                val ratio = min(width.toFloat() / backgroundImageBmp!!.width,
                    height.toFloat() / backgroundImageBmp!!.height)

                val width = (ratio * backgroundImageBmp!!.width).roundToInt()
                val mheight = (ratio * backgroundImageBmp!!.height).roundToInt()

                val leftRect = 0
                val rightRect = width
                val topRect = (height - mheight) / 2
                val bottomRect = topRect + mheight


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


    private fun Rect.calculateCenterY(): Float {
        return Math.abs((bottom - top) / 2f)
    }

    private fun Rect.calculateCenterX(): Float {
        return Math.abs((right - left) / 2f)
    }

    class Coordinate(var x: Float = 0f, var y: Float = 0f)

    class TypedText(context: Context){
        var fontRes: Int = 0
        var fontColor: Int =  ContextCompat.getColor(context, android.R.color.white)
        var typedText: String? = ""
        var fontSize: Int = 0
    }
}