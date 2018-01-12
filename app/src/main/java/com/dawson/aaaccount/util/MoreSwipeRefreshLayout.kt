package com.dawson.aaaccount.util

import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.AbsListView
import android.widget.ListView

import com.dawson.aaaccount.R

/**
 * 作者：PingerOne
 * 链接：http://www.jianshu.com/p/d23b42b6360b
 * 來源：简书
 * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
 * Created by Dawson on 2017/7/14.
 */

class MoreSwipeRefreshLayout : SwipeRefreshLayout {

//    private var loadMoreListener: LoadMoreListener? = null

    private var mListView: ListView? = null
    internal var mFooterView: View? = null

    var isNeedLoadMore: Boolean = false

    private val mScaledTouchSlop = 200f

    private var isLoading: Boolean = false


    /**
     * 在分发事件的时候处理子控件的触摸事件
     *
     * @param ev
     * @return
     */
    private var mDownY: Float = 0.toFloat()
    private var mUpY: Float = 0.toFloat()
    lateinit var more: () -> Unit

    fun setLoadMoreListener(loadMore: () -> Unit) {
//        this.loadMoreListener = loadMoreListener
        more = loadMore
    }

    interface LoadMoreListener {
        fun onLoadMore()
    }

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        // 获取ListView,设置ListView的布局位置
        if (mListView != null) return
        if (childCount > 0 && getChildAt(0) is ListView) {
            // 创建ListView对象
            mListView = getChildAt(0) as ListView

            // 设置ListView的滑动监听
            setListViewOnScroll()

            val mInflater = context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            mFooterView = mInflater.inflate(R.layout.listview_foot, null)
        }
    }

    /**
     * 设置ListView的滑动监听
     */
    private fun setListViewOnScroll() {

        mListView!!.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                // 移动过程中判断是否能下拉加载更多
                if (canLoadMore()) {
                    // 加载数据
                    loadData()
                }
            }

            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {

            }
        })
    }

    /**
     * 判断是否满足加载更多条件
     *
     * @return
     */
    private fun canLoadMore(): Boolean {
        // 1. 是上拉状态
        val condition1 = mDownY - mUpY >= mScaledTouchSlop
        if (condition1) {
            println("是上拉状态")
        }

        // 2. 当前页面可见的item是最后一个条目
        var condition2 = false
        if (mListView != null && mListView!!.adapter != null) {
            condition2 = mListView!!.lastVisiblePosition == mListView!!.adapter.count - 1
        }

        if (condition2) {
            println("是最后一个条目")
        }
        // 3. 正在加载状态
        val condition3 = !isLoading
        if (condition3) {
            println("不是正在加载状态")
        }
        return condition1 && condition2 && condition3
    }

    /**
     * 处理加载数据的逻辑
     */
    private fun loadData() {
        println("加载数据...")
//        if (loadMoreListener != null) {
        // 设置加载状态，让布局显示出来
        setLoading(true)
        more()
//            loadMoreListener!!.onLoadMore()
//        }
    }

    /**
     * 设置加载状态，是否加载传入boolean值进行判断
     *
     * @param loading
     */
    fun setLoading(loading: Boolean) {
        // 修改当前的状态
        isLoading = loading
        if (isLoading) {
            // 显示布局
            //            mListView.addFooterView(mFooterView);
            mFooterView?.visibility = View.VISIBLE
        } else {
            // 隐藏布局
            //            mListView.removeFooterView(mFooterView);
            mFooterView?.visibility = View.GONE

            // 重置滑动的坐标
            mDownY = 0f
            mUpY = 0f
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {

        when (ev.action) {
            MotionEvent.ACTION_DOWN ->
                // 移动的起点
                mDownY = ev.y
            MotionEvent.ACTION_MOVE ->
                // 移动过程中判断时候能下拉加载更多
                if (canLoadMore()) {
                    // 加载数据
                    loadData()
                }
            MotionEvent.ACTION_UP ->
                // 移动的终点
                mUpY = y
        }
        return super.dispatchTouchEvent(ev)
    }
}
