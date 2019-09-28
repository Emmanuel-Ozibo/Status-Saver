package com.mobigod.statussaver.ui.saver.fragment

import android.app.Activity
import android.view.View
import android.widget.AbsListView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.himangi.imagepreview.PreviewFile
import com.mobigod.statussaver.R
import com.mobigod.statussaver.base.BaseFragment
import com.mobigod.statussaver.data.local.FileSystemManager
import com.mobigod.statussaver.data.model.MediaItemModel
import com.mobigod.statussaver.databinding.FragmentImagesBinding
import com.mobigod.statussaver.global.show
import com.mobigod.statussaver.rx.SingleObserver
import com.mobigod.statussaver.ui.saver.adapter.MediaFilesAdapter
import com.mobigod.statussaver.ui.saver.adapter.MediaItemType
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import java.io.File
import javax.inject.Inject
import com.mobigod.statussaver.ui.saver.adapter.decos.SpacesItemDecoration
import com.himangi.imagepreview.ImagePreviewActivity
import android.content.Intent
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import com.jakewharton.rxbinding2.view.clicks
import com.mobigod.statussaver.data.local.PreferenceManager
import com.mobigod.statussaver.data.model.AdItemModel
import com.mobigod.statussaver.data.model.BaseItemModel
import com.mobigod.statussaver.global.Tools
import com.mobigod.statussaver.global.hide
import com.takusemba.spotlight.shape.RoundedRectangle
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter


/**
 * Guy!!!!!, you can actually merge this class with StatusVideoFragment and maybe use some enum to differentiate the
 * View types
 */
class StatusImagesFragment : BaseFragment<FragmentImagesBinding>() {
    private lateinit var mAdapter: MediaFilesAdapter

    lateinit var binding: FragmentImagesBinding
    lateinit var retryClicks: Flowable<Unit>

    @Inject lateinit var fileSystemManager: FileSystemManager
    @Inject lateinit var prefManager: PreferenceManager

    override fun getLayoutRes() = R.layout.fragment_images

    override fun initComponents() {
        binding = getBinding()

        retryClicks = binding.retryBtn.clicks().toFlowable(BackpressureStrategy.DROP)

        val options = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .skipMemoryCache(true)

        val glide = Glide.with(this)

        mAdapter = MediaFilesAdapter(options, glide.asBitmap(), glide,{
                _, items, position ->

            val itemClicked = items[position]
            if (itemClicked is AdItemModel) {
                //respond to clicked ads
                showToast("MY GUY NAH ADS YOU CLICK SO O...")
                return@MediaFilesAdapter
            }

            val previewFileList = ArrayList<PreviewFile>()
            items.forEach {
                if (it is MediaItemModel) {
                    previewFileList.add(
                        PreviewFile(it.file.absolutePath, "")
                    )
                }
            }


            //get the current position in the list
            //get the number of ads before that position
            //subtract that value from the list
            val adsB4List = mutableListOf<AdItemModel>()
            val subList = items.subList(0, position)
            subList.forEach {
                if (it is AdItemModel){
                    adsB4List.add(it)
                }
            }

            val absoluteClickedPosition = position - adsB4List.size

            val intent = Intent(activity, ImagePreviewActivity::class.java)
            intent.putExtra(ImagePreviewActivity.IMAGE_LIST, previewFileList)
            intent.putExtra(ImagePreviewActivity.CURRENT_ITEM, absoluteClickedPosition)

            startActivity(intent)

        }, {
            setUpTreeObserver(it)
        })

        binding.swipeRefImage.setOnRefreshListener {
            mAdapter.addAll(mutableListOf())
            refreshLayout()
        }

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.space)

        //Preventing view from being recycled
        binding.imagesRv.recycledViewPool.setMaxRecycledViews(0, 0)

        val slideInFromBottomAnimator = ScaleInAnimationAdapter(mAdapter)
            .apply {
                setDuration(300)
                setInterpolator(AccelerateDecelerateInterpolator())
                setFirstOnly(false)
            }

        binding.imagesRv.apply {
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

        refreshLayout()

    }


    private fun refreshLayout() {
        fileSystemManager.getAllStatusImages(object: SingleObserver<List<File>>() {

            override fun onSubscribe(d: Disposable) {
                super.onSubscribe(d)
                binding.errorView.hide()
            }


            override fun onNext(t: List<File>) {
                //populate the rv
                if(binding.swipeRefImage.isRefreshing) {
                    binding.swipeRefImage.isRefreshing = false
                }

                val l = mutableListOf<BaseItemModel>()

                for (i in t) {
                    l.add(MediaItemModel(MediaItemType.IMAGE_MEDIA, i))
                }

                val defaultAdSize = 3
                //val x = getNumberOfAds(defaultAdSize, list)
                for (i in 0 until defaultAdSize) {
                    try {
                        l.add((i * defaultAdSize) + 2, AdItemModel())
                    }catch (e: Exception){
                        print("There was an exception")
                    }

                }

                mAdapter.addAll(l)

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



    private fun setUpTreeObserver(view: View) {
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                //start spot light
                if (prefManager.isFirstRun) {
                    //show spotlight
                   // val item = binding.imagesRv
                    val shape = RoundedRectangle(view.height.toFloat(), view.width.toFloat(), 5f)

                    val simpleTarget = Tools.createSimpleSpotLightShape(activity as Activity, view,
                        shape, "Click an item to save",
                        "You can save and share files")
                    Tools.startSpotLight(activity as Activity, simpleTarget)
                    prefManager.isFirstRun = false
                }
            }

        })
    }

}