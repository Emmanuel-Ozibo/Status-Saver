package com.mobigod.statussaver.ui.split.fragments

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobigod.statussaver.R
import com.mobigod.statussaver.base.BaseFragment
import com.mobigod.statussaver.data.local.FileSystemManager
import com.mobigod.statussaver.databinding.SplitVideoFolderLayoutBinding
import com.mobigod.statussaver.ui.split.adapters.FoldersAdapter
import com.mobigod.statussaver.ui.split.dialogs.SplitVideoDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.io.File
import javax.inject.Inject

class SplitVideoFoldersFragment: BaseFragment<SplitVideoFolderLayoutBinding>()
    /*SplitVideoDialog.SplitDialogInterface*/ {

    @Inject
    lateinit var fileSystemManager: FileSystemManager

    private lateinit var adp: FoldersAdapter
    lateinit var binding: SplitVideoFolderLayoutBinding
    private var bundle: Bundle? = null

    private var listener: SplitVideoFoldersFragment? = null



    override fun getLayoutRes(): Int  = R.layout.split_video_folder_layout

    override fun initComponents() {
        binding = getBinding()

        adp = FoldersAdapter()

        getAllFolders()
        setUpListeners()
    }

    private fun setUpListeners() {
        binding.extFab.setOnClickListener {
            showDialog()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as SplitVideoFoldersFragment
        }catch (e: ClassCastException){
            e.printStackTrace()
            throw ClassCastException()
        }
    }

    private fun showDialog() {
        val handler = object : Handler(){
            override fun handleMessage(msg: Message?) {
                //start the cutting
                bundle = msg?.data
            }
        }
        val message = Message.obtain(handler)
        val d = SplitVideoDialog.getInstance(message)

        d.show(activity!!.supportFragmentManager, "tag1")
    }


    private fun getAllFolders() {

        val path = "${Environment.getExternalStorageDirectory()}/Status Saver/Status Saver"

        compositeDisposable += fileSystemManager.getAllSubFolders(path)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy (
                onNext = {
                    populateRV(it)
                },
                onError = {
                    showToast("${it.message}")
                }
            )

    }


    private fun populateRV(list: List<File>) {
        adp.removeAllFiles()
        adp.addAllFiles(list.toMutableList())

        binding.foldersRv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = adp
        }

    }

//    override fun onDone() {
//        listener?.videoSetUpComplete(bundle)
//    }

    interface SplitVideoFoldersFragment {
        fun videoSetUpComplete(bundle: Bundle?)
    }

}