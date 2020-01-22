package com.mobigod.statussaver.ui.split.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import com.mobigod.statussaver.R
import com.mobigod.statussaver.base.BaseFragment
import com.mobigod.statussaver.data.local.FileSystemManager
import com.mobigod.statussaver.databinding.FragmentSplitFoldersBinding
import com.mobigod.statussaver.global.hide
import com.mobigod.statussaver.global.show
import com.mobigod.statussaver.ui.split.adapters.FoldersAdapter
import com.mobigod.statussaver.ui.split.dialogs.SplitVideoDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.io.File
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SplitFoldersFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 */
class SplitFoldersFragment : BaseFragment<FragmentSplitFoldersBinding>() {


    @Inject
    lateinit var fileSystemManager: FileSystemManager

    private lateinit var adp: FoldersAdapter
    lateinit var binding: FragmentSplitFoldersBinding
    private lateinit var bundle: Bundle


    override fun getLayoutRes(): Int = R.layout.fragment_split_folders

    override fun initComponents() {
        binding =  getBinding()

        adp = FoldersAdapter()
        adp.folderListener = folderListener

        getAllFolders()
        setUpListeners()
    }


    private val folderListener = object : FoldersAdapter.FolderListener {
        override fun onClicked(file: String) {
            listener?.onFolderItemClicked(file)
        }

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }


    private fun getAllFolders() {

        val path = "${Environment.getExternalStorageDirectory()}/Status Saver/Status Saver"

        compositeDisposable += fileSystemManager.getAllSubFolders(path)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy (
                onNext = {

                    if (it.isEmpty())
                        binding.emptyView.show()
                    else{
                        binding.emptyView.hide()

                        populateRV(it)

                        if (binding.swipeToRef.isRefreshing)
                            binding.swipeToRef.isRefreshing = false
                    }

                },
                onError = {
                    showToast("${it.message}")
                }
            )

    }

    fun refreshLayout() {
        getAllFolders()
    }

    private fun setUpListeners() {
        binding.swipeToRef.setOnRefreshListener {
            getAllFolders()
        }


        binding.extFab.setOnClickListener {
            showDialog()
        }


    }


    private fun showDialog() {
        val handler = object : Handler() {
            override fun handleMessage(msg: Message?) {
                val bundle = msg!!.data
                onFinished(bundle)
            }
        }
        val message = Message.obtain(handler)
        val d = SplitVideoDialog.getInstance(message)


        d.show(activity!!.supportFragmentManager, "tag1")
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


    private var listener: OnFragmentInteractionListener? = null


    fun onFinished(bundle: Bundle?) {
        listener?.onFragmentInteraction(bundle)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(bundle: Bundle?)
        fun onFolderItemClicked(path: String)
    }

}
