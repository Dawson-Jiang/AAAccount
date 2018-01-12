package com.dawson.aaaccount.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView

/**
 * 缩放ImageView
 *
 * @author xiejinxiong
 */
class ZoomImageView : ImageView {

    /**
     * ImageView高度
     */
    private var imgHeight: Int = 0
    /**
     * ImageView宽度
     */
    private var imgWidth: Int = 0
    /**
     * 图片高度
     */
    private var intrinsicHeight: Int = 0
    /**
     * 图片宽度
     */
    private var intrinsicWidth: Int = 0
    /**
     * 最大缩放级别
     */
    private var mMaxScale = 2.0f
    /**
     * 最小缩放级别
     */
    private var mMinScale = 0.5f
    /**
     * 用于记录拖拉图片移动的坐标位置
     */
    private val matrixs = Matrix()
    /**
     * 用于记录图片要进行拖拉时候的坐标位置
     */
    private val currentMatrix = Matrix()
    /**
     * 记录第一次点击的时间
     */
    private var firstTouchTime: Long = 0
    /**
     * 时间点击的间隔
     */
    private val intervalTime = 250
    /**
     * 第一次点完坐标
     */
    private var firstPointF: PointF? = null

    private var scaleChangedCallback: (scale: Float) -> Unit = {}

    constructor(context: Context) : super(context) {
        initUI()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initUI()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initUI()
    }

    /**
     * 初始化UI
     */
    private fun initUI() {

        this.scaleType = ImageView.ScaleType.CENTER_CROP
        this.setOnTouchListener(TouchListener())

        getImageViewWidthHeight()
        //        getIntrinsicWidthHeight();
    }

    /**
     * 获得图片内在宽高
     */
    private fun getIntrinsicWidthHeight() {
        val drawable = this.drawable

        // 初始化bitmap的宽高
        intrinsicHeight = drawable.intrinsicHeight
        intrinsicWidth = drawable.intrinsicWidth
    }

    private inner class TouchListener : View.OnTouchListener {

        /**
         * 记录是拖拉照片模式还是放大缩小照片模式
         */
        private var mode = 0// 初始状态
        /**
         * 用于记录开始时候的坐标位置
         */
        private val startPoint = PointF()
        /**
         * 两个手指的开始距离
         */
        private var startDis: Float = 0.toFloat()
        /**
         * 两个手指的中间点
         */
        private var midPoint: PointF? = null

        override fun onTouch(v: View, event: MotionEvent): Boolean {

            /** 通过与运算保留最后八位 MotionEvent.ACTION_MASK = 255  */
            when (event.action and MotionEvent.ACTION_MASK) {
            // 单点监听和多点触碰监听
            // 手指压下屏幕
                MotionEvent.ACTION_DOWN -> {
                    mode = MODE_DRAG
                    Log.i("dawson zoom view", "ACTION_DOWN")
                    // 记录ImageView当前的移动位置
                    currentMatrix.set(imageMatrix)
                    startPoint.set(event.x, event.y)
                    matrix.set(currentMatrix)
                    makeImageViewFit()
                }
            // 手指在屏幕上移动，改事件会被不断触发
                MotionEvent.ACTION_MOVE -> {
                    Log.i("dawson zoom view", "ACTION_MOVE")
                    // 拖拉图片
                    if (mode == MODE_DRAG) {
                        Log.i("dawson zoom view", "ACTION_MOVE__MODE_DRAG")
                        var dx = event.x - startPoint.x // 得到x轴的移动距离
                        var dy = event.y - startPoint.y // 得到x轴的移动距离
                        // 在没有移动之前的位置上进行移动z
                        matrix.set(currentMatrix)
                        val values = FloatArray(9)
                        matrix.getValues(values)
                        dx = checkDxBound(values, dx)
                        dy = checkDyBound(values, dy)
                        matrix.postTranslate(dx, dy)
                    } else if (mode == MODE_ZOOM) {
                        Log.i("dawson zoom view", "ACTION_MOVE__MODE_ZOOM")
                        val endDis = distance(event)// 结束距离
                        if (endDis > 10f) { // 两个手指并拢在一起的时候像素大于10
                            var scale = endDis / startDis// 得到缩放倍数
                            matrix.set(currentMatrix)
                            val values = FloatArray(9)
                            matrix.getValues(values)
                            scale = checkFitScale(scale, values)
                            matrix.postScale(scale, scale, midPoint!!.x, midPoint!!.y)
                            callback()
                        }
                    }// 放大缩小图片
                }
            // 手指离开屏幕
                MotionEvent.ACTION_UP -> setDoubleTouchEvent(event)
                MotionEvent.ACTION_POINTER_UP -> {
                    mode = 0
                    // matrix.set(currentMatrix);
                    val values = FloatArray(9)
                    matrix.getValues(values)
                    makeImgCenter(values)
                }
            // 当屏幕上已经有触点(手指)，再有一个触点压下屏幕
                MotionEvent.ACTION_POINTER_DOWN -> {
                    Log.i("dawson zoom view", "ACTION_POINTER_DOWN")
                    mode = MODE_ZOOM
                    /** 计算两个手指间的距离  */
                    startDis = distance(event)
                    /** 计算两个手指间的中间点  */
                    if (startDis > 10f) { // 两个手指并拢在一起的时候像素大于10
                        midPoint = mid(event)
                        // 记录当前ImageView的缩放倍数
                        currentMatrix.set(imageMatrix)
                    }
                }
            }
            imageMatrix = matrix
            return true
        }

        /**
         * 计算两个手指间的距离
         */
        private fun distance(event: MotionEvent): Float {
            val dx = event.getX(1) - event.getX(0)
            val dy = event.getY(1) - event.getY(0)
            /** 使用勾股定理返回两点之间的距离  */
            return Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
        }

        /**
         * 计算两个手指间的中间点
         */
        private fun mid(event: MotionEvent): PointF {
            val midX = (event.getX(1) + event.getX(0)) / 2
            val midY = (event.getY(1) + event.getY(0)) / 2
            return PointF(midX, midY)
        }

        /**
         * 和当前矩阵对比，检验dy，使图像移动后不会超出ImageView边界
         *
         * @param values
         * @param dy
         * @return
         */
        private fun checkDyBound(values: FloatArray, dy: Float): Float {
            var dy2 = dy
            val height = imgHeight.toFloat()
            if (intrinsicHeight * values[Matrix.MSCALE_Y] < height)
                return 0f
            if (values[Matrix.MTRANS_Y] + dy2 > 0)
                dy2 = -values[Matrix.MTRANS_Y]
            else if (values[Matrix.MTRANS_Y] + dy2 < -(intrinsicHeight * values[Matrix.MSCALE_Y] - height))
                dy2 = -(intrinsicHeight * values[Matrix.MSCALE_Y] - height) - values[Matrix.MTRANS_Y]
            return dy2
        }

        /**
         * 和当前矩阵对比，检验dx，使图像移动后不会超出ImageView边界
         *
         * @param values
         * @param dx
         * @return
         */
        private fun checkDxBound(values: FloatArray, dx: Float): Float {
            var dx2 = dx
            val width = imgWidth.toFloat()
            if (intrinsicWidth * values[Matrix.MSCALE_X] < width)
                return 0f
            if (values[Matrix.MTRANS_X] + dx > 0)
                dx2 = -values[Matrix.MTRANS_X]
            else if (values[Matrix.MTRANS_X] + dx2 < -(intrinsicWidth * values[Matrix.MSCALE_X] - width))
                dx2 = -(intrinsicWidth * values[Matrix.MSCALE_X] - width) - values[Matrix.MTRANS_X]
            return dx2
        }

        /**
         * MSCALE用于处理缩放变换
         *
         *
         * MSKEW用于处理错切变换
         *
         *
         * MTRANS用于处理平移变换
         */

        /**
         * 检验scale，使图像缩放后不会超出最大倍数
         *
         * @param scale
         * @param values
         * @return
         */
        private fun checkFitScale(scale: Float, values: FloatArray): Float {
            var scale2 = scale
            if (scale2 * values[Matrix.MSCALE_X] > mMaxScale)
                scale2 = mMaxScale / values[Matrix.MSCALE_X]
            if (scale2 * values[Matrix.MSCALE_X] < mMinScale)
                scale2 = mMinScale / values[Matrix.MSCALE_X]
            return scale2
        }

        /**
         * 促使图片居中
         *
         * @param values (包含着图片变化信息)
         */
        private fun makeImgCenter(values: FloatArray) {

            // 缩放后图片的宽高
            val zoomY = intrinsicHeight * values[Matrix.MSCALE_Y]
            val zoomX = intrinsicWidth * values[Matrix.MSCALE_X]
            // 图片左上角Y坐标
            val leftY = values[Matrix.MTRANS_Y]
            // 图片左上角X坐标
            val leftX = values[Matrix.MTRANS_X]
            // 图片右下角Y坐标
            val rightY = leftY + zoomY
            // 图片右下角X坐标
            val rightX = leftX + zoomX

            // 使图片垂直居中
            if (zoomY < imgHeight) {
                val marY = (imgHeight - zoomY) / 2.0f
                matrix.postTranslate(0f, marY - leftY)
            }

            // 使图片水平居中
            if (zoomX < imgWidth) {

                val marX = (imgWidth - zoomX) / 2.0f
                matrix.postTranslate(marX - leftX, 0f)

            }

            // 使图片缩放后上下不留白（即当缩放后图片的大小大于imageView的大小，但是上面或下面留出一点空白的话，将图片移动占满空白处）
            if (zoomY >= imgHeight) {
                if (leftY > 0) {// 判断图片上面留白
                    matrix.postTranslate(0f, -leftY)
                }
                if (rightY < imgHeight) {// 判断图片下面留白
                    matrix.postTranslate(0f, imgHeight - rightY)
                }
            }

            // 使图片缩放后左右不留白
            if (zoomX >= imgWidth) {
                if (leftX > 0) {// 判断图片左边留白
                    matrix.postTranslate(-leftX, 0f)
                }
                if (rightX < imgWidth) {// 判断图片右边不留白
                    matrix.postTranslate(imgWidth - rightX, 0f)
                }
            }
        }
    }

    companion object {
        /**
         * 拖拉照片模式
         */
        private val MODE_DRAG = 1
        /**
         * 放大缩小照片模式
         */
        private val MODE_ZOOM = 2
    }

    /**
     * 获取ImageView的宽高
     */
    private fun getImageViewWidthHeight() {
        val vto2 = viewTreeObserver
        vto2.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                imgWidth = width
                imgHeight = height

            }
        })
    }

    /**
     * 使得ImageView一开始便显示最适合的宽高比例，便是刚好容下的样子
     */
    private fun makeImageViewFit() {
        if (scaleType != ImageView.ScaleType.MATRIX) {
            scaleType = ImageView.ScaleType.MATRIX

            matrix.postScale(1.0f, 1.0f, (imgWidth / 2).toFloat(), (imgHeight / 2).toFloat())
            callback()
        }
    }

    /**
     * 双击事件触发
     *
     * @param event
     */
    private fun setDoubleTouchEvent(event: MotionEvent) {

        val values = FloatArray(9)
        matrix.getValues(values)
        // 存储当前时间
        val currentTime = System.currentTimeMillis()
        // 判断两次点击间距时间是否符合
        if (currentTime - firstTouchTime >= intervalTime) {
            firstTouchTime = currentTime
            firstPointF = PointF(event.x, event.y)
        } else {
            // 判断两次点击之间的距离是否小于30f
            if (Math.abs(event.x - firstPointF!!.x) < 30f && Math.abs(event.y - firstPointF!!.y) < 30f) {
                // 判断当前缩放比例与最大最小的比例
                if (values[Matrix.MSCALE_X] < mMaxScale) {
                    matrix.postScale(mMaxScale / values[Matrix.MSCALE_X],
                            mMaxScale / values[Matrix.MSCALE_X], event.x,
                            event.y)
                } else {
                    matrix.postScale(mMinScale / values[Matrix.MSCALE_X],
                            mMinScale / values[Matrix.MSCALE_X], event.x,
                            event.y)
                }
                callback()
            }

        }
    }

    /**
     * 设置图片的最大和最小的缩放比例
     *
     * @param mMaxScale
     * @param mMinScale
     */
    fun setPicZoomHeightWidth(mMaxScale: Float, mMinScale: Float) {
        this.mMaxScale = mMaxScale
        this.mMinScale = mMinScale
    }

    override fun setImageBitmap(bm: Bitmap) {
        super.setImageBitmap(bm)
        getIntrinsicWidthHeight()
    }

    fun setOnScaleChanged(scaleChangedCallback: (scale: Float) -> Unit) {
        this.scaleChangedCallback = scaleChangedCallback
    }

    private fun callback() {
        val values = FloatArray(9)
        matrix.getValues(values)
        scaleChangedCallback(values[Matrix.MSCALE_X])
    }
}


