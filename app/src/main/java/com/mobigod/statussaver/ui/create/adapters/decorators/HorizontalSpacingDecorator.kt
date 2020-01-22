package com.mobigod.statussaver.ui.create.adapters.decorators

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.elyeproj.drawtext.dpToPx

class HorizontalSpacingDecorator(val horizontalSpacing: Int, val resources: Resources): RecyclerView.ItemDecoration(){

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = resources.dpToPx(horizontalSpacing)
        outRect.right = resources.dpToPx(horizontalSpacing)
    }
}