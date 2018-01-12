package com.dawson.aaaccount.activity

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView

import com.dawson.aaaccount.R
import com.dawson.aaaccount.util.ControllableViewPager
import com.dawson.aaaccount.util.ImageLoadUtil
import com.dawson.aaaccount.util.ZoomImageView
import kotlinx.android.synthetic.main.activity_preview_picture.*

import java.util.ArrayList

class PreviewPictureActivity : Activity() {
    internal var urls: MutableList<String> = ArrayList()

    internal var pagerAdapter: PagerAdapter = object : PagerAdapter() {
        override fun getCount(): Int {
            return urls.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val url = urls[position]
            val imageView = ZoomImageView(this@PreviewPictureActivity)
            imageView.setOnScaleChanged { scale -> vpMain.setCanScroll(scale <= 1) }
            container.addView(imageView)
            ImageLoadUtil.loadImage(url, imageView)
            return imageView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as ImageView)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_preview_picture)
        val currentIndex = intent.getIntExtra("index", 0)
        val tmps = intent.getSerializableExtra("urls") as Array<String>
        if (tmps.isNotEmpty()) {
            for (tmp in tmps) {
                urls.add(tmp)
            }
        }
        vpMain.adapter = pagerAdapter
        vpMain.currentItem = currentIndex
    }
}
