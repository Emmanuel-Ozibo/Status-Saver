package com.elyeproj.drawtext

import android.content.res.Resources
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.TypedValue

inline fun <reified T> Resources.dpToPx(value: Int): T {
    val result = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        value.toFloat(), displayMetrics
    )

    return when (T::class) {
        Float::class -> result as T
        Int::class -> result.toInt() as T
        else -> throw IllegalStateException("Type not supported")
    }
}

lateinit var projectResources: ProjectResources

class ProjectResources(private val resources: Resources) {
    val paintBig by lazy {
        getBasePaint().apply {
            color = Color.BLACK
            strokeWidth = resources.dpToPx(8)
        }
    }

    val paint by lazy {
        getBasePaint().apply {
            color = Color.BLACK
            strokeWidth = resources.dpToPx(1)
            textSize = resources.dpToPx(30)
        }
    }

    val paintText by lazy {
        getBasePaint().apply {
            color = Color.BLACK
            strokeWidth = resources.dpToPx(1)
            textSize = resources.dpToPx(30)
            style = Paint.Style.FILL
        }
    }

    val paintLight by lazy {
        getBasePaint().apply {
            color = Color.GRAY
            alpha = 128
            strokeWidth = resources.dpToPx(1)
            textSize = resources.dpToPx(30)
        }
    }

    val paintBox by lazy {
        getBasePaint().apply {
            color = Color.BLUE
            alpha = 128
            strokeWidth = resources.dpToPx(1)
            textSize = resources.dpToPx(30)
        }
    }

    val paintRed by lazy {
        getBasePaint().apply {
            color = Color.RED
            alpha = 128
            strokeWidth = resources.dpToPx(4)
            textSize = resources.dpToPx(30)
        }
    }

    val paintRedLine by lazy {
        getBasePaint().apply {
            color = Color.RED
            alpha = 128
            strokeWidth = resources.dpToPx(5)
            textSize = resources.dpToPx(30)
        }
    }

    private fun getBasePaint(): Paint {
        return Paint().apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            textSize = resources.dpToPx(30)
        }
    }
}
