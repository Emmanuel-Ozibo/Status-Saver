package com.mobigod.statussaver.global

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import android.os.Environment.getExternalStorageDirectory
import android.os.Environment
import android.util.Log
import java.io.*
import java.util.*


object Tools {
    fun checkPermission(context: Context, permission: String) =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    fun askReadStoragePermission(context: Activity, id: Int) {
        ActivityCompat.requestPermissions(context, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE), id)
    }



    fun decodeBitmapAsync(file: File, contentResolver: ContentResolver): Observable<Bitmap> {
        return  Observable.create {
            emitter ->
            val bitmap = file.getImageBitmap(contentResolver)
            emitter.onNext(bitmap)
            emitter.onComplete()
        }
    }


    fun share(context: Context, path: String){
        Intent(Intent.ACTION_SEND).apply {
            type = "video/mp4"
            putExtra(Intent.EXTRA_STREAM, File(path).getUri())
        }.also {
            context.startActivity(Intent.createChooser(it, "Share image using"));
        }
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


    fun saveStatus(path: String) {
        val rootDirectory = getExternalStorageDirectory()
        val desFileName = rootDirectory.absolutePath + "/Status Saver/"
        val newDir = File(desFileName)
        if (!newDir.isDirectory && !newDir.exists()) {
            val directoryCreated = newDir.mkdir()
            if (directoryCreated) {
                createNewFile(desFileName, path)
            }
        } else {
            createNewFile(desFileName, path)
        }
    }


    private fun createNewFile(desFileName: String, path: String) {
        //create a new file
        val savedStoryPath = desFileName + getName(path)
        val SaveStoryFile = File(savedStoryPath)
        if (!SaveStoryFile.isFile && !SaveStoryFile.exists()) {
            Log.i("File Status", "Doesnt exist")
            try {
                val fileCreated = SaveStoryFile.createNewFile()
                if (fileCreated) {
                    Log.i("File Status", "File Created")
                    copyStatusIntoFile(savedStoryPath, path)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        } else {
            Log.i("File Status", "File already exists")
        }
    }



    /**
     * @param savedStoryPath This represents the new path we want to make
     * @param path This is where it is coming from
     */
    private fun copyStatusIntoFile(savedStoryPath: String, path: String) {
        try {
            val inComingChannel = FileInputStream(File(path)).getChannel()
            val destinationChannel = FileOutputStream(File(savedStoryPath)).getChannel()

            val id = destinationChannel.transferFrom(inComingChannel, 0, inComingChannel.size())
            if (id > 0) {
                Log.i("Copy Status: ", "Copied")
                //give a notification that it has been done
            } else {
                Log.i("Copy Status: ", "Cant copy")
            }

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun getName(path: String): String {
        return UUID.randomUUID().toString()
    }
}