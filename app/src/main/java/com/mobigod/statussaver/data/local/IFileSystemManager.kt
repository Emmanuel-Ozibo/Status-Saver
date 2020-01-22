package com.mobigod.statussaver.data.local

import android.content.ContentResolver
import android.net.Uri
import com.mobigod.statussaver.data.model.MusicFile
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import java.io.File


/**
 * File system manager should implement this interface
 */
interface IFileSystemManager {

    fun getAllStatusImages(observer: Observer<List<File>>, observable: (Observable<List<File>>) -> Unit)
    fun getAllStatusVideos(observer: Observer<List<File>>, observable: (Observable<List<File>>) -> Unit)
    fun getAllSongsFiles(contentResolver: ContentResolver?, query: String): Observable<MusicFile>
    fun hasSubFolders(path: String): Boolean
    fun getAllSubFolders(path: String): Observable<List<File>>
    fun getFilesInFolderCount(path: String): Int
    fun getFilesInFolder(path: String): Observable<List<File>>
    fun putNewFilesInContentResolver(path: String, contentResolver: ContentResolver, compositeDisposable: CompositeDisposable)
    fun getUrisInFolder(path: String): ArrayList<Uri>

}