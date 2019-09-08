package com.mobigod.statussaver.ui.saver.fragment

import android.content.Intent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.AbsListView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.himangi.imagepreview.ImagePreviewActivity
import com.himangi.imagepreview.PreviewFile
import com.jakewharton.rxbinding3.view.clicks
import com.mobigod.statussaver.R
import com.mobigod.statussaver.base.BaseFragment
import com.mobigod.statussaver.data.local.FileSystemManager
import com.mobigod.statussaver.data.model.MediaItemModel
import com.mobigod.statussaver.databinding.FragmentVideosBinding
import com.mobigod.statussaver.global.show
import com.mobigod.statussaver.rx.SingleObserver
import com.mobigod.statussaver.ui.saver.activity.VideoPlayerActivity
import com.mobigod.statussaver.ui.saver.adapter.MediaFilesAdapter
import com.mobigod.statussaver.ui.saver.adapter.MediaItemType
import com.mobigod.statussaver.ui.saver.adapter.decos.SpacesItemDecoration
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import java.io.File
import javax.inject.Inject

class StatusVideosFragment: BaseFragment<FragmentVideosBinding>() {

    lateinit var binding: FragmentVideosBinding
    private lateinit var mAdapter: MediaFilesAdapter
    lateinit var retryClicks: Flowable<Unit>

    @Inject
    lateinit var fileSystemManager: FileSystemManager


    override fun getLayoutRes() = R.layout.fragment_videos

    override fun initComponents() {
        binding = getBinding()

        retryClicks = binding.retryBtn.clicks().toFlowable(BackpressureStrategy.DROP)

        val options = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .skipMemoryCache(true)


        val glide = Glide.with(this)


        mAdapter = MediaFilesAdapter(options, glide.asBitmap(), glide) {
                item, items, position ->

            VideoPlayerActivity.start(context!!, item.file.absolutePath)
        }

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.space)

        //Preventing view from being recycled
        binding.videosRv.recycledViewPool.setMaxRecycledViews(0, 0)

        val slideInFromBottomAnimator = ScaleInAnimationAdapter(mAdapter)
            .apply {
                setDuration(300)
                setInterpolator(AccelerateDecelerateInterpolator())
                setFirstOnly(false)
            }

        binding.videosRv.apply {
            addItemDecoration(SpacesItemDecoration(spacingInPixels))
            layoutManager = GridLayoutManager(context, 2)
            setHasFixedSize(true)
            addOnScrollListener(object : RecyclerView.OnScrollListener(){
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    when (newState) {
                        RecyclerView.SCROLL_STATE_IDLE -> glide.resumeRequests()
                        AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL,
                        AbsListView.OnScrollListener.SCROLL_STATE_FLING -> glide.pauseRequests()
                    }
                }
            })
            adapter = slideInFromBottomAnimator
        }

        fileSystemManager.getAllStatusVideos(object: SingleObserver<List<File>>() {

            override fun onSubscribe(d: Disposable) {
                super.onSubscribe(d)
                binding.errorView.visibility = View.GONE
            }


            override fun onNext(t: List<File>) {
                //populate the rv
                mAdapter.addAll(t.map { file -> MediaItemModel(
                    MediaItemType.VIDEO_MEDIA,
                    file
                ) }.toMutableList())

            }

            override fun onError(e: Throwable) {
                binding.errorView.show()
                binding.errorTxt.text = e.message
            }

        }) {
            it.retryWhen {
                it.flatMap {
                        err ->
                    err.printStackTrace()
                    val message = err.message
                    binding.errorTxt.text = message

                    retryClicks.toObservable()
                }
            }

        }

    }

}