package com.mobigod.statussaver.ui.saver.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.mobigod.statussaver.ui.saver.fragment.StatusImagesFragment
import com.mobigod.statussaver.ui.saver.fragment.StatusVideosFragment

class StatusPagerAdapter(fgmr: FragmentManager): FragmentPagerAdapter(fgmr) {


    override fun getItem(position: Int): Fragment {
        return when(position){
            0 ->  StatusImagesFragment()
            1 ->  StatusVideosFragment()
            else -> StatusImagesFragment()
        }
    }


    override fun getPageTitle(position: Int): CharSequence? {
        return  when(position){
            0 -> "Images"
            1 -> "Videos"
            else -> ""
        }
    }

    override fun getCount() = 2

}