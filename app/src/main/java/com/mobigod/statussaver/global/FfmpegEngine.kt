package com.mobigod.statussaver.global

import android.content.Context
import android.os.Environment
import android.util.Log
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler
import com.mobigod.statussaver.ui.split.SplitVideoFile
import java.io.File

class FfmpegEngine(val context: Context, val listner: FFmpegExecuteResponseHandler) {

    private val fFmpeg: FFmpeg = FFmpeg.getInstance(context)



    fun execute(splitVideoFile: SplitVideoFile) {
        val cmd = getFFmpegCmd(splitVideoFile)
        fFmpeg.execute(cmd, listner)
    }


    private fun getFFmpegCmd(splitVideoFile: SplitVideoFile): Array<String> {

        val input = splitVideoFile.filePath!!
        val outputParent = "${Environment.getExternalStorageDirectory()}/Status Saver/Status Saver/${splitVideoFile.folderName}"
        val file = File(input)


        val outputDir = File(outputParent)
        if (!outputDir.exists())
            outputDir.mkdir()

        //i use # symbol as a delimiter to split the string more accurately
        val ffmpegCmd = "-i#${file.absolutePath}#-c:a aac#-c:v libx264#-c#copy#-map#0#-segment_time#00:00:${splitVideoFile.progress}#-f#segment#-reset_timestamps#1#$outputParent/output%03d.${file.extension}"
            .split("#")


        //ffmpeg -i input.mp4 -c:a aac -c:v libx264 -c copy -map 0 -segment_time 00:20:00 -f segment -reset_timestamps 1 output%03d.mp4

        return ffmpegCmd.toTypedArray()
    }

}