package com.mobigod.statussaver.ui.create.activity

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobigod.statussaver.R
import com.mobigod.statussaver.base.StatusBuilderBaseActivity
import com.mobigod.statussaver.databinding.StatusCreatorLayoutBinding
import com.mobigod.statussaver.ui.create.adapters.DecorationToolsAdapter
import com.mobigod.statussaver.ui.create.fragment.TypeStatusFragment
import com.mobigod.statussaver.ui.customviews.DrawTextView
import android.graphics.Color
import com.mobigod.statussaver.global.*
import com.mobigod.statussaver.ui.create.adapters.decorators.HorizontalSpacingDecorator
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.security.SecureRandom
import android.app.Activity
import android.graphics.Bitmap
import android.R.attr.path
import android.graphics.Rect
import com.bumptech.glide.Glide
import android.graphics.drawable.Drawable
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition




class StatusCreatorActivity: StatusBuilderBaseActivity<StatusCreatorLayoutBinding>(), TypeStatusFragment.TypeStatusInterface{

    lateinit var binding: StatusCreatorLayoutBinding

    override fun hasAndroidInjector() = true
    override fun getLayoutRes() = R.layout.status_creator_layout

    private var textStatusFragment: Fragment? = null
    private val random = SecureRandom()

    lateinit var decorationToolsAdapter: DecorationToolsAdapter

    override fun initComponent() {
        binding = getBinding()

        textStatusFragment = TypeStatusFragment()

        decorationToolsAdapter = DecorationToolsAdapter {
            decorationToolsItem ->
            when(decorationToolsItem.title){
                "Background" ->  {
                    if (binding.drawTxtView.isImageBackground){
                        //Rect
                        longToastWith("Will you like to remove this image?")
                        return@DecorationToolsAdapter
                    }
                    binding.drawTxtView.canvasColor = getRandomColor(random)
                }

                "Picture" -> {
                    CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this)
                }
                else -> {
                    longToastWith(decorationToolsItem.title)
                }
            }
        }

        binding.decoToolsRv.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(HorizontalSpacingDecorator(15, resources))
            adapter = decorationToolsAdapter
        }

        binding.drawTxtView.setOnClickListener {
            binding.reLayout.hideWithTime(400)
            startFragment(R.id.edit_fragment_container, textStatusFragment!!, TypeStatusFragment::class.simpleName)
        }
    }


    private fun getRandomColor(rnd: SecureRandom): Int {
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }

    override fun onTypeFragPaused() {
        if (!binding.reLayout.isShowing()){
            binding.reLayout.show()
        }
    }


    override fun onFinishedClicked(typedText: DrawTextView.TypedText) {
        binding.drawTxtView.textTypedObj = typedText
        popFragment()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri
                Glide.with(this)
                    .asBitmap()
                    .load(resultUri)
                    .into(object : CustomTarget<Bitmap>(){
                        override fun onLoadCleared(placeholder: Drawable?) {
                        }

                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            binding.drawTxtView.backgroundImageBmp = resource
                        }

                    })
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error

            }
        }

    }

    companion object {
        fun start(context: Context) {
            Intent(context, StatusCreatorActivity::class.java).apply {
                //put that ever you like here
            }.also {
                context.startActivity(it)
            }
        }
    }
}