package com.mobigod.statussaver.data.model


import com.mobigod.statussaver.ui.saver.adapter.MediaItemType
import java.io.File

data class MediaItemModel(val type: MediaItemType, val file: File): BaseItemModel {

    override fun getModelType(): Int {
        return 0
    }
}