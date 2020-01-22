package com.mobigod.statussaver.global

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.reactivex.Observable
import com.mobigod.statussaver.R
import com.takusemba.spotlight.OnSpotlightStateChangedListener
import com.takusemba.spotlight.OnTargetStateChangedListener
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.shape.Shape
import com.takusemba.spotlight.target.SimpleTarget
import android.view.inputmethod.InputMethodManager
import com.mobigod.statussaver.ui.split.SplitVideoFile
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


object Tools {

    fun checkPermission(context: Context, permission: String) =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    fun askReadStoragePermission(context: Activity, id: Int) {
        ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE), id)
    }

    fun askRecordAudioPermission(context: Activity, id: Int) {
        ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.RECORD_AUDIO), id)
    }


    fun decodeBitmapAsync(file: File, contentResolver: ContentResolver): Observable<Bitmap> {
        return  Observable.create {
            emitter ->
            val bitmap = file.getImageBitmap(contentResolver)
            emitter.onNext(bitmap)
            emitter.onComplete()
        }
    }


    fun share(context: Context, path: String) {
        Intent(Intent.ACTION_SEND).apply {
            type = "video/mp4"
            putExtra(Intent.EXTRA_STREAM, File(path).getUri2())
        }.also {
            context.startActivity(Intent.createChooser(it, "Share image using"));
        }
    }


    fun shareVideoFilesToWhatsapp(context: Context, files: ArrayList<Uri>) {
        Intent().apply {
            action = Intent.ACTION_SEND_MULTIPLE
            type = "video/*"
            `package` = "com.whatsapp"
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, files)
        }.also {
            context.startActivity(it)
        }
    }



    fun generateRandomColor(): Int{
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }


    fun showKeyboard(context: Context){
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }




    fun convertMillisecsToReadable(milliSecs: Long): String{
        return String.format("%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(milliSecs),
            TimeUnit.MILLISECONDS.toSeconds(milliSecs) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSecs)
            ))
    }

    fun startSpotLight(activity: Activity, target: SimpleTarget) {
        val spotlight= Spotlight.with(activity)
            .setOverlayColor(R.color.background)
            .setDuration(500L)
            .setAnimation(AccelerateDecelerateInterpolator())
            .setTargets(target)
            .setClosedOnTouchedOutside(true)
            .setOnSpotlightStateListener(object : OnSpotlightStateChangedListener {
                override fun onStarted() {
                    //(activity).longToastWith("spotlight is started")
                }

                override fun onEnded() {
                    //(activity).longToastWith("spotlight is ended")
                }
            })
        spotlight.start()
            //.start()
    }



    fun createSimpleSpotLightShape(context: Activity, view: View, shape: Shape, title: String = "",
                                   description: String = "" /*onstarted: (SimpleTarget?) -> Unit,
                                   onEnded: (SimpleTarget?) -> Unit*/): SimpleTarget {

        val location = IntArray(2)
        view.getLocationInWindow(location)

        val x = view.width / 2f
        val y = location[1] * 0.75f + view.height / 2f

        val screenWidth = context.resources.displayMetrics.heightPixels * 0.7f

        return SimpleTarget.Builder(context)
            .setPoint(x, y)
            .setShape(shape) // or RoundedRectangle()
            .setTitle(title)
            //.setDescription(description)
            .setOverlayPoint(30f, screenWidth)
            .setOnSpotlightStartedListener(object : OnTargetStateChangedListener<SimpleTarget> {
                override fun onStarted(target: SimpleTarget?) {
                    //onstarted(target)
                }

                override fun onEnded(target: SimpleTarget?) {
                    //onEnded(target)
                }

            })
            .build()
    }



    fun LunchVideoPicker(context: Activity, requestCode: Int) {
        Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI).apply {
            type = "video/*"
        }.also {
            context.startActivityForResult(it, requestCode)
        }
    }




    fun hasSubFolders(path: String): Boolean {
        val file = File(path)
        if (file.exists()) {
            val allPaths = file.listFiles()
            for (f in allPaths) {
                if (f.isDirectory)
                    return true
            }
        }

        return false;

    }





}