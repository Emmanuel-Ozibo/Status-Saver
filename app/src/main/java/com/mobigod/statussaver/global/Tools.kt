package com.mobigod.statussaver.global

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.himangi.imagepreview.Util
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import java.io.File
import androidx.core.content.ContextCompat.startActivity




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



}