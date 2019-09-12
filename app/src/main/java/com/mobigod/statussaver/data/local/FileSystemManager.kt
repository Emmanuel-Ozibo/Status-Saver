package com.mobigod.statussaver.data.local

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.preference.PreferenceManager
import android.util.Log
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.internal.operators.flowable.FlowableRetryWhen
import io.reactivex.schedulers.Schedulers
import java.io.*
import java.net.URI
import java.util.*
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

    fun saveStatus(path: String) {
        val rootDirectory = Environment.getExternalStorageDirectory()
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