package com.mobigod.statussaver.ui.split

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler
import com.google.android.material.snackbar.Snackbar
import com.mobigod.statussaver.R
import com.mobigod.statussaver.base.BaseActivity
import com.mobigod.statussaver.base.FragmentsBaseActivity
import com.mobigod.statussaver.data.local.FileSystemManager
import com.mobigod.statussaver.data.model.MusicFile
import com.mobigod.statussaver.databinding.SplitVideoActivityBinding
import com.mobigod.statussaver.databinding.VideoPickerDialogLayoutBinding
import com.mobigod.statussaver.global.FfmpegEngine
import com.mobigod.statussaver.global.Tools
import com.mobigod.statussaver.global.longToastWith
import com.mobigod.statussaver.global.shortToastWith
import com.mobigod.statussaver.ui.create.fragment.SongsFragment
import com.mobigod.statussaver.ui.saver.activity.VideoPlayerActivity
import com.mobigod.statussaver.ui.split.dialogs.SplitVideoDialog
import com.mobigod.statussaver.ui.split.fragments.SplitFoldersFragment
import com.mobigod.statussaver.ui.split.fragments.SplitVideoFoldersFragment
import com.mobigod.statussaver.ui.split.fragments.SplitVideoFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.io.File
import javax.inject.Inject

data class SplitVideoFile(
    var folderName: String? = "",
    var progress: Int? = 0,
    var filePath: String? = "",
    var duration: Int = 0) {

    override fun toString(): String {
        return "Folder Name: $folderName \n Progress: $progress \n File Path: $filePath \n Duration: $duration"
    }
}



class SplitVideoActivity: FragmentsBaseActivity<SplitVideoActivityBinding>(),
    SplitFoldersFragment.OnFragmentInteractionListener,
    SplitVideoFragment.SplitVideoInteractor {

    private val VIDEO_PICKER_REQUEST_CODE = 4

    private val projection = arrayOf(
        MediaStore.Video.Media.DATA,
        MediaStore.Video.Media.DISPLAY_NAME,
        MediaStore.Video.Media.DURATION
    )

    private var splitVideoFile = SplitVideoFile()

    private lateinit var dialog: AlertDialog

    private lateinit var splitFoldersFragment: SplitFoldersFragment

    @Inject
    lateinit var fileSystemManager: FileSystemManager

    override fun onFragmentInteraction(bundle: Bundle?) {
        val folderName = bundle?.getString(SplitVideoDialog.FOLDER_NAME)
        val progress = bundle?.getInt(SplitVideoDialog.PROGRESS)

        splitVideoFile.folderName = folderName
        splitVideoFile.progress = progress

        Log.i("arg01", "$folderName, $progress")

        val binding = VideoPickerDialogLayoutBinding.inflate(layoutInflater)

        val alartDialog = AlertDialog.Builder(this)
            .setView(binding.root)
            .create()

        alartDialog.show()


        binding.continueBtn.setOnClickListener {
            Tools.LunchVideoPicker(this, VIDEO_PICKER_REQUEST_CODE)
            alartDialog.dismiss()
        }

    }


    lateinit var binding: SplitVideoActivityBinding


    override fun hasAndroidInjector(): Boolean = true

    override fun getLayoutRes(): Int = R.layout.split_video_activity

    override fun initComponent() {
        binding = getBinding()

        val path = "${Environment.getExternalStorageDirectory()}/Status Saver/Status Saver"

        splitFoldersFragment = SplitFoldersFragment()

        if (Tools.hasSubFolders(path))
            startFragment(R.id.fragment_container, splitFoldersFragment,
                SplitFoldersFragment::class.simpleName,
                animIn = R.anim.slide_from_right
                /*animOut = R.anim.slide_to_left*/)

    }


    companion object {
        fun start(context: Context) {
            Intent(context, SplitVideoActivity::class.java).also {
                context.startActivity(it)
            }
        }
    }


    override fun onFolderItemClicked(path: String) {
        startFragment(R.id.fragment_container, SplitVideoFragment.newInstance(path),
            SplitVideoFragment::class.simpleName,
            animIn = R.anim.slide_from_right,
            animOut = R.anim.slide_to_left)
    }


    override fun onVideoClicked(path: String) {
        //VideoPlayerActivity.start(this, path)
    }


    override fun onBackPressed() {
        if (hasOneItemOnBackStack())
            finish()
        else
            popFragment()
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == VIDEO_PICKER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val uri = data?.data
                val cursor = contentResolver.query(uri,
                    projection,
                    null,
                    null,
                    null,
                    null)


                if (cursor.moveToFirst()) {
                    val filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
                    val duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))
                    splitVideoFile.filePath = filePath
                    splitVideoFile.duration = duration

                    Log.i("filePath", filePath)
                    Log.i("splitFile", splitVideoFile.toString())

                    startFFmpegMagic()
                }
            }
        }
    }



    private val observer = object : FFmpegExecuteResponseHandler {
        override fun onFinish() {
            showSnackWithMessage("")
        }

        override fun onSuccess(message: String?) {
            dialog.dismiss()
            showSnackWithMessage(message)

            val newPath = "${Environment.getExternalStorageDirectory()}/Status Saver/Status Saver/${splitVideoFile.folderName}"
            fileSystemManager.putNewFilesInContentResolver(newPath, contentResolver, compositeDisposable)

            splitFoldersFragment.refreshLayout()

        }

        override fun onFailure(message: String?) {
            dialog.dismiss()
        }

        override fun onProgress(message: String?) {
            dialog.setMessage(message)
        }

        override fun onStart() {
            dialog = AlertDialog.Builder(this@SplitVideoActivity)
                .setTitle("Processing Input...")
                .setMessage("Creating Splits")
                .create()
            dialog.show()
        }

    }


    private fun showSnackWithMessage(s: String?) {
        Snackbar.make(binding.root, "$s", Snackbar.LENGTH_LONG).show()
    }


    private fun startFFmpegMagic() {
        val fFmpegEngine = FfmpegEngine(this, observer)
        fFmpegEngine.execute(splitVideoFile)

    }

}