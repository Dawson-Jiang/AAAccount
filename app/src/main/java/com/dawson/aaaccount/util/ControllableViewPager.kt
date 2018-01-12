package com.dawson.aaaccount.util

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * 可控制是否滑动的ViewPager
 * Created by Dawson on 2017/6/3.
 */
class ControllableViewPager : ViewPager {

    private var canScroll = true

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    fun setCanScroll(canScroll: Boolean) {
        this.canScroll = canScroll
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return if (canScroll)
            super.onTouchEvent(ev)
        else
            canScroll
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return if (canScroll)
            super.onInterceptTouchEvent(ev)
        else
            canScroll
    }
}
