package com.mobigod.statussaver.ui.saver.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.mobigod.statussaver.data.model.MediaItemModel
import com.mobigod.statussaver.databinding.MediaItemLayoutBinding
import com.mobigod.statussaver.global.hide
import com.mobigod.statussaver.global.removeAllItems
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.ads.*
import com.google.android.gms.ads.formats.MediaView
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.mobigod.statussaver.R
import com.mobigod.statussaver.data.model.BaseItemModel


class MediaFilesAdapter(val options: RequestOptions, val glide: RequestBuilder<Bitmap>, val glideMain: RequestManager,
                        val onitemClick: (BaseItemModel, List<BaseItemModel>, position: Int) -> Unit,
                        val onItem: (View) -> Unit):
                        RecyclerView.Adapter<MediaFilesAdapter.MediaFilesViewHolder>() {
    val mediaItems = mutableListOf<BaseItemModel>()

    lateinit var binding: ViewDataBinding
    var context: Context? = null
    var itemView: View? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaFilesViewHolder {
        context = parent.context
        if (viewType == 0){
            binding = MediaItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        }else{
            val binding = LayoutInflater.from(context)
                .inflate(R.layout.advert_item_view, parent, false)
            return MediaFilesViewHolder(binding)
        }

        return MediaFilesViewHolder(binding.root)
    }

    override fun getItemCount() = mediaItems.size

    override fun onBindViewHolder(holder: MediaFilesViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            onitemClick(mediaItems[position], mediaItems, position)
        }

        if (position == 0) {
            onItem(holder.itemView)
        }
        holder.bindView(position)
    }

    override fun getItemViewType(position: Int): Int {
        val item = mediaItems[position]
        return item.getModelType()
    }


    fun addAll(list: MutableList<BaseItemModel>) {
        mediaItems.addAll(list)
        notifyDataSetChanged()
    }

    fun getFirstItem() = itemView

    private fun removeAllItems() {
        mediaItems.removeAllItems()
        notifyDataSetChanged()
    }


    inner class MediaFilesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val ADMOB_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110"

        fun bindView(position: Int) {
            val item = mediaItems[position]
            if (item.getModelType() == 0){
                item as MediaItemModel
                if (item.type == MediaItemType.IMAGE_MEDIA) {
                    (binding as MediaItemLayoutBinding).playIcon.hide()
                }

                glide.load(item.file)
                    .apply(options)
                    .thumbnail(0.5f)
                    .transition(BitmapTransitionOptions.withCrossFade())
                    .into((binding as MediaItemLayoutBinding).thumbnail)
            }else {
                showAd(itemView)
            }

        }

        private fun showAd(view: View) {
            //refresh_button.isEnabled = false

            val builder = AdLoader.Builder(context, ADMOB_AD_UNIT_ID)

            builder.forUnifiedNativeAd { unifiedNativeAd ->
                // OnUnifiedNativeAdLoadedListener implementation.
                val adView = view as UnifiedNativeAdView

                populateUnifiedNativeAdView(unifiedNativeAd, adView)
            }

            val videoOptions = VideoOptions.Builder()
                //.setStartMuted(start_muted_checkbox.isChecked)
                .build()

            val adOptions = NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build()

            builder.withNativeAdOptions(adOptions)

            val adLoader = builder.withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(errorCode: Int) {
                    //refresh_button.isEnabled = true
                    //Toast.makeText(this@MainActivity, "Failed to load native ad: " + errorCode, Toast.LENGTH_SHORT).show()
                }
            }).build()

            adLoader.loadAd(AdRequest.Builder().build())

            //videostatus_text.text = ""
        }

        private fun populateUnifiedNativeAdView(nativeAd: UnifiedNativeAd, adView: UnifiedNativeAdView) {
            // You must call destroy on old ads when you are done with them,
            // otherwise you will have a memory leak.
            //currentNativeAd?.destroy()
            //currentNativeAd = nativeAd
            // Set the media view. Media content will be automatically populated in the media view once
            // adView.setNativeAd() is called.
            adView.mediaView = adView.findViewById<MediaView>(R.id.ad_media)

            // Set other ad assets.
            adView.headlineView = adView.findViewById(R.id.ad_headline)
            adView.bodyView = adView.findViewById(R.id.ad_body)
            adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
            adView.iconView = adView.findViewById(R.id.ad_app_icon)
            adView.priceView = adView.findViewById(R.id.ad_price)
            adView.starRatingView = adView.findViewById(R.id.ad_stars)
            adView.storeView = adView.findViewById(R.id.ad_store)
            adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

            // The headline is guaranteed to be in every UnifiedNativeAd.
            (adView.headlineView as TextView).text = nativeAd.headline

            // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
            // check before trying to display them.
            if (nativeAd.body == null) {
                adView.bodyView.visibility = View.INVISIBLE
            } else {
                adView.bodyView.visibility = View.VISIBLE
                (adView.bodyView as TextView).text = nativeAd.body
            }

            if (nativeAd.callToAction == null) {
                adView.callToActionView.visibility = View.INVISIBLE
            } else {
                adView.callToActionView.visibility = View.VISIBLE
                (adView.callToActionView as Button).text = nativeAd.callToAction
            }

            if (nativeAd.icon == null) {
                adView.iconView.visibility = View.GONE
            } else {
                (adView.iconView as ImageView).setImageDrawable(
                    nativeAd.icon.drawable)
                adView.iconView.visibility = View.VISIBLE
            }

            if (nativeAd.price == null) {
                adView.priceView.visibility = View.INVISIBLE
            } else {
                adView.priceView.visibility = View.VISIBLE
                (adView.priceView as TextView).text = nativeAd.price
            }

            if (nativeAd.store == null) {
                adView.storeView.visibility = View.INVISIBLE
            } else {
                adView.storeView.visibility = View.VISIBLE
                (adView.storeView as TextView).text = nativeAd.store
            }

            if (nativeAd.starRating == null) {
                adView.starRatingView.visibility = View.INVISIBLE
            } else {
                (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
                adView.starRatingView.visibility = View.VISIBLE
            }

            if (nativeAd.advertiser == null) {
                adView.advertiserView.visibility = View.INVISIBLE
            } else {
                (adView.advertiserView as TextView).text = nativeAd.advertiser
                adView.advertiserView.visibility = View.VISIBLE
            }

            // This method tells the Google Mobile Ads SDK that you have finished populating your
            // native ad view with this native ad. The SDK will populate the adView's MediaView
            // with the media content from this native ad.
            adView.setNativeAd(nativeAd)

            // Get the video controller for the ad. One will always be provided, even if the ad doesn't
            // have a video asset.
            val vc = nativeAd.videoController

            // Updates the UI to say whether or not this ad has a video asset.
//            if (vc.hasVideoContent()) {
//                //videostatus_text.text = String.format(Locale.getDefault(),
//                    "Video status: Ad contains a %.2f:1 video asset.",
//                    vc.aspectRatio)
//
//                // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
//                // VideoController will call methods on this object when events occur in the video
//                // lifecycle.
//                vc.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
//                    override fun onVideoEnd() {
//                        // Publishers should allow native ads to complete video playback before
//                        // refreshing or replacing them with another ad in the same UI location.
////                        refresh_button.isEnabled = true
////                        videostatus_text.text = "Video status: Video playback has ended."
//                        super.onVideoEnd()
//                    }
//                }
//            } else {
////                videostatus_text.text = "Video status: Ad does not contain a video asset."
////                refresh_button.isEnabled = true
//            }
        }
    }
}