package com.mobigod.statussaver.data.local

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Environment
import android.os.SystemClock
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.Toast
import com.mobigod.statussaver.data.model.MusicFile
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.apache.commons.io.FileUtils
import java.io.*
import javax.inject.Inject


/**
 * Manages all file system operations
 */
class FileSystemManager @Inject constructor(private val context: Context): IFileSystemManager {

    private val compositeDisposable = CompositeDisposable()

//getAllStatusVideos

    override fun  getAllStatusImages(observer: Observer<List<File>>, observable: (Observable<List<File>>) -> Unit){
        //get all files through an observable
        val fileObservable = Observable.create {
                emitter: ObservableEmitter<List<File>> ->
            val fullPathToFileSystem = Environment.getExternalStorageDirectory()
            val pathToWhatsApp = "${fullPathToFileSystem.absolutePath}/WhatsApp/Media/.Statuses/"

            val file = File(pathToWhatsApp)

            if (file.isDirectory) {
                //get all the images
                val imageFileList = file.listFiles().toList()
                    .filter { it.absolutePath.endsWith(".jpg")}
                    .sortedBy { it.lastModified() }
                    .reversed()

                if (imageFileList.isEmpty()){
                    throw Exception("You do not have any image files in status")
                }
                emitter.onNext(imageFileList)
                emitter.onComplete()
            } else {
                emitter.onError(Exception("This file path is not a directory"))
            }

        }   .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        observable(fileObservable)
        fileObservable.subscribe(observer)
    }

    override fun  getAllStatusVideos(observer: Observer<List<File>>, observable: (Observable<List<File>>) -> Unit) {
        //get all files through an observable
        val fileObservable = Observable.create {
                emitter: ObservableEmitter<List<File>> ->
            val fullPathToFileSystem = Environment.getExternalStorageDirectory()
            val pathToWhatsApp = "${fullPathToFileSystem.absolutePath}/WhatsApp/Media/.Statuses/"

            val file = File(pathToWhatsApp)

            if (file.isDirectory) {
                //get all the images
                val imageFileList = file.listFiles().toList()
                    .filter { it.absolutePath.endsWith(".mp4")}
                    .sortedBy { it.lastModified() }
                    .reversed()

                if (imageFileList.isEmpty()){
                    throw Exception("You do not have any image files in status")
                }
                emitter.onNext(imageFileList)
                emitter.onComplete()
            } else {
                emitter.onError(Exception("This file path is not a directory"))
            }

        }   .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        observable(fileObservable)
        fileObservable.subscribe(observer)
    }


    @SuppressLint("CheckResult")
    override fun getAllSongsFiles(contentResolver: ContentResolver?, query: String): Observable<MusicFile> {
        var selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        var selectionArgs: Array<String>? = null


        if (!TextUtils.isEmpty(query)) {
            selection = MediaStore.Audio.Media.DISPLAY_NAME + " =?"
            selectionArgs = arrayOf(query)
        }


        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID
        )


        var cursor: Cursor? = null

        if (contentResolver != null) {
            cursor = contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                MediaStore.Audio.Media.DISPLAY_NAME
            )
        }


        val songs = ArrayList<MusicFile>()

        if (cursor != null && cursor.count > 0) {
            if (cursor.moveToFirst()) {
                do {
                    songs.add(
                        MusicFile().apply {
                            displayName =
                                cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                            imagePath = ""
                            duration =
                                cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                            artist =
                                cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                            path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                            albumId =
                                cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                        }
                    )
                } while (cursor.moveToNext())

                cursor.close()
            }
        }

        return Observable.fromIterable(songs)

    }


    fun getAlbumArtUri(contentResolver: ContentResolver?, albumid: Long): Observable<String> {

        if (contentResolver != null) {
            val cursor = contentResolver.query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                arrayOf (
                    MediaStore.Audio.Albums._ID,
                    MediaStore.Audio.Albums.ALBUM_ART),
                MediaStore.Audio.Albums._ID + "=?",
                arrayOf(albumid.toString()),
                null
            )

            if (cursor!!.moveToFirst()) {
                val path =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART))
                        ?: return Observable.empty()
                return Observable.just(path)
                // do whatever you need to do
            }
        }

        return  Observable.just("")
    }


    fun saveVideoFile(context: Context, absolutePath: String) {
        val videoNewPath = File(getAppFolder(context), SystemClock.currentThreadTimeMillis().toString() + ".mp4")
        val srcFile = File(absolutePath)
        FileUtils.copyFile(srcFile, videoNewPath)
        try {
            ContentValues().apply {
                put(MediaStore.MediaColumns.DATA, videoNewPath.absolutePath)
                put(MediaStore.MediaColumns.MIME_TYPE, "video/.mp4")
                put(MediaStore.MediaColumns.DATE_MODIFIED, System.currentTimeMillis())
            }.also {
                context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, it)
                Toast.makeText(context, "Video saved to Gallery", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception){

        }
    }

    private fun getWorkingDirectory(): File {
        val directory = File(Environment.getExternalStorageDirectory(), "Status Saver")
        if (!directory.exists()) {
            directory.mkdir()
        }

        return directory
    }


    fun getAppFolder(context: Context): File {
        val photoDirectory =
            File(getWorkingDirectory().absolutePath, getAppName(context.applicationContext))
        if (!photoDirectory.exists()) {
            photoDirectory.mkdir()
        }

        return photoDirectory
    }


    private fun getAppName(context: Context): String {
        val pm = context.applicationContext.packageManager

        var ai: ApplicationInfo?
        try {
            ai = pm.getApplicationInfo(context.packageName, 0)
        } catch (var4: PackageManager.NameNotFoundException) {
            ai = null
        }

        return (if (ai != null) pm.getApplicationLabel(ai) else "(unknown)") as String
    }


}