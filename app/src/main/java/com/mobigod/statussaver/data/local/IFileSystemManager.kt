package com.mobigod.statussaver.data.local

import android.content.ContentResolver
import com.mobigod.statussaver.data.model.MusicFile
import io.reactivex.Observable
import io.reactivex.Observer
import java.io.File


/**
 * File system manager should implement this interface
 */
interface IFileSystemManager {

    fun getAllStatusImages(observer: Observer<List<File>>, observable: (Observable<List<File>>) -> Unit)
    fun getAllStatusVideos(observer: Observer<List<File>>, observable: (Observable<List<File>>) -> Unit)
    fun getAllSongsFiles(contentResolver: ContentResolver?, query: String): Observable<MusicFile>

}