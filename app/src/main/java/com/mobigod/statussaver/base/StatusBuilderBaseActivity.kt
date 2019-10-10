package com.mobigod.statussaver.base

import android.os.Handler
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.elyeproj.drawtext.ProjectResources
import com.elyeproj.drawtext.projectResources
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mobigod.statussaver.R

abstract class StatusBuilderBaseActivity<T: ViewDataBinding>: BaseActivity<T>(){


    override fun performDataBinding() {
        projectResources = ProjectResources(resources)
        super.performDataBinding()
    }


    /**Always start a fragment with a tag*/
    open fun startFragment(layoutId: Int, fragment: Fragment, tag: String?) {
        val frag = supportFragmentManager.findFragmentByTag(tag)
        if (frag != null) {
            supportFragmentManager.popBackStackImmediate(tag, 0)
        } else {
            Handler().postDelayed({
                supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(layoutId, fragment, tag)
                    .addToBackStack(null)
                    .commit()
            }, 250)
        }
    }

    open fun showBottomSheetFragment(fragment: BottomSheetDialogFragment, tag: String?) {

        fragment.show(supportFragmentManager, tag)
    }

    open fun popFragment() {
        supportFragmentManager.popBackStack()
    }
}