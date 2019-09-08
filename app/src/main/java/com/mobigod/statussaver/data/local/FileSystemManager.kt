package com.mobigod.statussaver.data.local

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.preference.PreferenceManager
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.internal.operators.flowable.FlowableRetryWhen
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.net.URI
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

    override fun  getAllStatusVideos(observer: Observer<List<File>>, observable: (Observable<List<File>>) -> Unit){
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


}