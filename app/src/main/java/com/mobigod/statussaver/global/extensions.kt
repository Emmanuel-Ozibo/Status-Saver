package com.mobigod.statussaver.global

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.view.View
import android.widget.Toast
import android.provider.MediaStore
import androidx.core.content.FileProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.io.File


fun Activity.checkCardMounted(): Boolean {
    return Environment.getExternalStorageState() in
            setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
}

fun Activity.longToastWith(message: String){
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}


fun Activity.shortToastWith(message: String){
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun <E>MutableList<E>.removeAllItems(){
    this.forEach {
        this.remove(it)
    }
}

fun CompositeDisposable.plus(disposable: Disposable) {
    add(disposable)
}


fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

/**
 * @author Emmanuel Ozibo
 * @param timemilli time to wait before we execute the code
 * */
fun View.hideWithTime(timemilli: Long){
    Handler().postDelayed({
        visibility = View.GONE
    }, timemilli)
}


fun View.isShowing() = visibility == View.VISIBLE


fun File.getUri(context: Context) =
    FileProvider.getUriForFile(context, "${context.packageName}.provider", this)

fun File.getUri2() =
    Uri.fromFile(this)

fun File.getImageBitmap(contentResolver: ContentResolver): Bitmap {
    val uri = this.getUri2()
    return MediaStore.Images.Media.getBitmap(contentResolver, uri)
}

fun Uri.getImageBitmap(context: Context): Bitmap{
    return  MediaStore.Images.Media.getBitmap(context.contentResolver, this)
}

