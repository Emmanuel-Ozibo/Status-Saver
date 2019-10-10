package com.mobigod.statussaver.ui.create.fragment

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView
import com.mobigod.statussaver.R
import com.mobigod.statussaver.base.BaseFragment
import com.mobigod.statussaver.data.local.FileSystemManager
import com.mobigod.statussaver.data.model.MusicFile
import com.mobigod.statussaver.databinding.SongsFragmentLayoutBinding
import com.mobigod.statussaver.global.errors.EmptyListExecption
import com.mobigod.statussaver.global.hide
import com.mobigod.statussaver.global.show
import com.mobigod.statussaver.ui.create.adapters.ColorsAdapter
import com.mobigod.statussaver.ui.create.adapters.SongsAdapter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import java.lang.ClassCastException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SongsFragment: BaseFragment<SongsFragmentLayoutBinding>(), SongsAdapter.SongsAdapterListener{

    @Inject lateinit var fileManager: FileSystemManager

    lateinit var binding: SongsFragmentLayoutBinding
    private val c = CompositeDisposable()
    private var songsAdapter: SongsAdapter? = null

    lateinit var listner: SongsFragmentListner

    override fun getLayoutRes() = R.layout.songs_fragment_layout

    override fun initComponents() {
        binding = getBinding()

        songsAdapter = SongsAdapter(fileManager).apply {
            listener= this@SongsFragment
        }

        binding.songsRv.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
            adapter = songsAdapter
        }

        //disable rv caching
        binding.songsRv.recycledViewPool.setMaxRecycledViews(0, 0)

        c.add(RxSearchView
            .queryTextChanges(binding.searchView)
            .debounce(400, TimeUnit.MICROSECONDS)
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy (
                onNext = {
                    text ->
                    if (songsAdapter!!.getAllSongs().isNotEmpty()){
                        songsAdapter?.removeAllSongs()
                    }
                    loadSongs(text.toString())
                },

                onComplete = {

                },

                onError = {
                    err ->

                    err.printStackTrace()
                    //todo: handle error the right way here
                }
            ))

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listner = context as SongsFragmentListner
        }catch (e: ClassCastException){
            throw ClassCastException("Can't cast this class: implement interface")
        }
    }

    override fun onPause() {
        super.onPause()
        listner.onSongsFragmentClosed()
        Log.i("SongsFragment", "PAUSE CALLED!!!!!")
    }


    private fun loadSongs(searchQuery: String) {
        c.add(fileManager.getAllSongsFiles(activity?.contentResolver, "")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                binding.songsRv.hide()
                binding.progressIndicator.show()
            }
            .filter {
                searchQuery.isEmpty() or it.displayName.toLowerCase().contains(searchQuery)
            }
            .subscribeBy (
                onNext = {
                    song ->
                    binding.songsRv.show()
                    binding.errorTv.hide()
                    binding.progressIndicator.hide()
                    songsAdapter?.addSong(song)
                },

                onError = {
                    error ->
                    binding.progressIndicator.hide()
                    binding.errorTv.show()
                    binding.errorTv.text = error.message
                    error.printStackTrace()
                },

                onComplete = {
                    if (songsAdapter!!.getAllSongs().isEmpty()){
                        binding.progressIndicator.hide()
                        binding.errorTv.show()
                        binding.errorTv.text = "Song not found, try again"
                    }
                }
            )
        )

    }

    override fun onSongClicked(musicFile: MusicFile) {
        listner.onSongPicked(musicFile)
    }

    interface SongsFragmentListner {
        fun onSongsFragmentClosed()
        fun onSongPicked(musicFile: MusicFile)
    }


    override fun onDestroy() {
        super.onDestroy()
        c.dispose()
    }


}