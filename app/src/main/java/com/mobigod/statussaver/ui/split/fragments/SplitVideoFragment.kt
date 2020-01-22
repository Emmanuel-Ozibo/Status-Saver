package com.mobigod.statussaver.ui.split.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobigod.statussaver.R
import com.mobigod.statussaver.base.BaseFragment
import com.mobigod.statussaver.data.local.FileSystemManager
import com.mobigod.statussaver.databinding.SplitVideoLayoutBinding
import com.mobigod.statussaver.global.Tools
import com.mobigod.statussaver.ui.split.SplitVideoActivity
import com.mobigod.statussaver.ui.split.adapters.VideoAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import java.io.File
import java.lang.ClassCastException
import javax.inject.Inject

class SplitVideoFragment: BaseFragment<SplitVideoLayoutBinding>() {

    lateinit var binding: SplitVideoLayoutBinding
    private var mAdapter: VideoAdapter? = null
    private var mListener: SplitVideoInteractor? = null
    private var specPath: String = ""

    @Inject
    lateinit var fileSystemManager: FileSystemManager

    override fun getLayoutRes(): Int = R.layout.split_video_layout

    override fun initComponents() {
        binding = getBinding()

        mAdapter = VideoAdapter(activity as SplitVideoActivity)
        mAdapter?.listener = observer

        specPath = arguments?.getString(FOLDER_NAME_ARG_KEY)!!
        val file = File(specPath)
        binding.folderNameTv.text = "${file.parentFile.name} >> ${file.name}"

        binding.shareBtn.setOnClickListener {
            val arrayListUris = fileSystemManager.getUrisInFolder(file.absolutePath)

            Tools.shareVideoFilesToWhatsapp(context!!, arrayListUris)
        }

        getAllVideos()
    }


    private fun getAllVideos() {


        compositeDisposable += fileSystemManager.getFilesInFolder(specPath)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy (
                onNext = {
                    setUpVideos(it)

                },
                onError = {
                    showToast("${it.message}")
                }
            )

    }


    private fun setUpVideos(list: List<File>) {
        mAdapter?.addAllVideo(list.toMutableList())

        binding.foldersRv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as SplitVideoInteractor
        }catch (e: ClassCastException) {
            throw ClassCastException("Pls implement interface")
        }
    }


    private val observer = object : VideoAdapter.VideoListener {
        override fun onVideoClicked(file: String) {
            mListener?.onVideoClicked(file)
        }

    }



    companion object {
        const val FOLDER_NAME_ARG_KEY = "folder_arg01"

        fun newInstance(folderName: String) =
            SplitVideoFragment().apply {
                arguments = Bundle().apply {
                    putString(FOLDER_NAME_ARG_KEY, folderName)
                }
            }
    }


    interface SplitVideoInteractor {
        fun onVideoClicked(path: String)
    }

}