package com.hd.seekwidget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import java.util.*
import kotlin.math.abs


/**
 * 字体大小调整
 * 自定义属性
 * @see R.styleable.FontSizeSeekBar
 */
class FontSizeSeekBarBak @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    // 一共有多少格
    private var mMaxCount = 4

    // 一段的宽度，根据总宽度和总格数计算得来
    private var mItemWidth = 0f

    // 控件的宽高
    private var mViewHeight = 0
    private var mViewWidth = 0

    private var mCurrentX = 0f   // 滑动过程中x坐标 单位像素
    private var mPrePointX = 0f  // seek变化通知 单位像素
    private var mCenterY = 0f   // Y轴，中心点

    private val mPointList: MutableList<Point> = ArrayList()    // 有效数据点
    private var mDefaultPointsId = 1    // 默认位置 id
    private var mCurrentPointId = 1           // 当前所在位置 id

    // 滑块
    private var mThumbRadius = 0
    private var mThumbColor = Color.WHITE
    private var mThumbBitmap: Bitmap
    private var mThumbPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mThumbPadding = 0f     // thumb透明块存在阴影等效果，会有padding

    // Progress：进度
    private var mProgressUnSelectPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mProgressUnSelectColor = Color.BLACK
    private var mProgressSelectPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mProgressSelectColor = Color.BLACK
    private var mProgressPaintStrokeWidth = 0f     // 线条粗细

    private var mPointPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mPointPaintColor = Color.BLACK
    private var mPointPaintStrokeWidth = 0f     // 线条粗细

    // 图片:未选中
    private var mPointUnSelectBitmap: Bitmap

    // 图片:选中
    private var mPointSelectBitmap: Bitmap

    init {
        LOG.d("init", " start ")
        mThumbBitmap = (Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.ic_fssb_seek_thumb)) as BitmapDrawable).bitmap
        mPointUnSelectBitmap =
            (Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.ic_fssb_point_un_select_default)) as BitmapDrawable).bitmap
        mPointSelectBitmap =
            (Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.ic_fssb_point_select_default)) as BitmapDrawable).bitmap

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FontSizeSeekBar)
        val arraySize = typedArray.indexCount
        for (i in 0 until arraySize) {
            initCustomAttr(typedArray.getIndex(i), typedArray)
        }
        typedArray.recycle()


        mProgressSelectPaint.color = mProgressUnSelectColor
        mProgressSelectPaint.style = Paint.Style.FILL
        mProgressSelectPaint.strokeWidth = mProgressPaintStrokeWidth

        mProgressUnSelectPaint.color = mProgressSelectColor
        mProgressUnSelectPaint.style = Paint.Style.FILL
        mProgressUnSelectPaint.strokeWidth = mProgressPaintStrokeWidth

        mPointPaint.color = mPointPaintColor
        mPointPaint.style = Paint.Style.FILL
        mPointPaint.strokeWidth = mPointPaintStrokeWidth

        mThumbPaint.color = mThumbColor
        mThumbPaint.style = Paint.Style.FILL
        LOG.d("init", "mViewWidth $mViewWidth mViewHeight:$mViewHeight mProgressPaintStrokeWidth:$mProgressPaintStrokeWidth")
    }

    private fun initCustomAttr(attr: Int, typedArray: TypedArray) {
        when (attr) {
            // progress
            R.styleable.FontSizeSeekBar_seekbar_progressUnSelectColor -> {
                mProgressUnSelectColor = typedArray.getColor(attr, Color.rgb(33, 33, 33))
            }
            R.styleable.FontSizeSeekBar_seekbar_progressSelectColor -> {
                mProgressSelectColor = typedArray.getColor(attr, Color.rgb(33, 33, 33))
            }
            R.styleable.FontSizeSeekBar_seekbar_progressStrokeWidth -> {
                mProgressPaintStrokeWidth = typedArray.getDimensionPixelSize(attr, DensityUtil.dp2px(context, 10f)).toFloat()
            }

            // thumb
            R.styleable.FontSizeSeekBar_seekbar_thumb -> {
                mThumbBitmap =
                    (Objects.requireNonNull(ContextCompat.getDrawable(context, typedArray.getResourceId(attr, R.drawable.ic_fssb_thumb_default))) as BitmapDrawable).bitmap
            }
            R.styleable.FontSizeSeekBar_seekbar_thumbPaintColor -> {
                mThumbColor = typedArray.getColor(attr, Color.WHITE)
            }
            R.styleable.FontSizeSeekBar_seekbar_thumbRadius -> {
                mThumbRadius = typedArray.getDimensionPixelSize(attr, DensityUtil.dp2px(context, 17f))
            }
            R.styleable.FontSizeSeekBar_seekbar_thumbPadding -> {
                mThumbPadding = typedArray.getDimensionPixelSize(attr, DensityUtil.dp2px(context, 7f)).toFloat()
            }


            // point
//            R.styleable.FontSizeSeekBar_seekbar_maxCount -> {
//                mMaxCount = typedArray.getInteger(attr, 5)
//            }
            R.styleable.FontSizeSeekBar_seekbar_defaultPointsId -> {
                mDefaultPointsId = typedArray.getInteger(attr, mDefaultPointsId)
            }
            // point
            R.styleable.FontSizeSeekBar_seekbar_pointSelect -> {
                mPointSelectBitmap =
                    (Objects.requireNonNull(ContextCompat.getDrawable(context, typedArray.getResourceId(attr, R.drawable.ic_fssb_point_select_default))) as BitmapDrawable).bitmap
            }
            R.styleable.FontSizeSeekBar_seekbar_pointUnSelect -> {
                mPointUnSelectBitmap =
                    (Objects.requireNonNull(ContextCompat.getDrawable(context, typedArray.getResourceId(attr, R.drawable.ic_fssb_point_un_select_default))) as BitmapDrawable).bitmap
            }
            R.styleable.FontSizeSeekBar_seekbar_pointStrokeWidth -> {
                mPointPaintStrokeWidth = typedArray.getDimensionPixelSize(attr, DensityUtil.dp2px(context, 10f)).toFloat()
            }
            R.styleable.FontSizeSeekBar_seekbar_pointPaintColor -> {
                mPointPaintColor = typedArray.getColor(attr, Color.WHITE)
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mViewHeight = h
        mViewWidth = w
        mCenterY = mViewHeight / 2f
        LOG.d("onSizeChanged", "mViewWidth $mViewWidth mViewHeight:$mViewHeight mProgressPaintStrokeWidth:$mProgressPaintStrokeWidth")
        LOG.d("onSizeChanged", "mPointSelectBitmap.width ${mPointSelectBitmap.width}  mPointUnSelectBitmap.width:${mPointUnSelectBitmap.width}")
        // 横线宽度是总宽度-2个背景圆的半径
        mItemWidth = (mViewWidth - 2f * mProgressPaintStrokeWidth - mThumbPadding) / mMaxCount
        LOG.d("onSizeChanged", "mMaxCount:$mMaxCount  mItemWidth $mItemWidth")
        // 把可点击点保存起来
        for (i in 0..mMaxCount) {
            LOG.d("onSizeChanged", "i:$i point ${i * mItemWidth + mProgressPaintStrokeWidth}")
            mPointList.add(Point((i * mItemWidth + mProgressPaintStrokeWidth + mThumbPadding / 2).toInt(), mCenterY.toInt()))
        }
        // 初始刻度
        mCurrentX = mPointList[mDefaultPointsId].x.toFloat()
    }

    // 确保 当前选中的x值在范围圆柱内
    private fun checkCurrentXRange() {
        if (mCurrentX < mThumbRadius) {
            mCurrentX = mThumbRadius.toFloat()
        }
        if (mCurrentX > mViewWidth - mProgressPaintStrokeWidth) {
            mCurrentX = mViewWidth.toFloat() - mProgressPaintStrokeWidth
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        checkCurrentXRange()

        val radius = mProgressPaintStrokeWidth
        val progressTop = mViewHeight / 2f - radius
        val progressBottom = mViewHeight / 2f + radius

        // 右边
        if (mCurrentPointId < mMaxCount ) {
            canvas.drawRoundRect(
                mCurrentX, progressTop, mViewWidth.toFloat(), progressBottom, radius, radius, mProgressSelectPaint
            )
            canvas.drawRect(
                mCurrentX, progressTop, mCurrentX + radius, progressBottom, mProgressSelectPaint
            )
        }
        // 左边
        if (mCurrentPointId > 0) {
            canvas.drawRoundRect(
                mPointList[0].x.toFloat() - mProgressPaintStrokeWidth, progressTop, mCurrentX, progressBottom, radius, radius, mProgressUnSelectPaint
            )
            canvas.drawRect(
                mCurrentX - radius, progressTop, mCurrentX, progressBottom, mProgressUnSelectPaint
            )
        }

        // 绘制刻度
        for (point in mPointList) {
            if (mCurrentX >= point.x) {
                canvas.drawBitmap(mPointSelectBitmap, point.x.toFloat() - mPointSelectBitmap.width / 2f, mViewHeight / 2f - mPointSelectBitmap.height / 2f, mPointPaint)
            } else {
                canvas.drawBitmap(mPointUnSelectBitmap, point.x.toFloat() - mPointSelectBitmap.width / 2f, mViewHeight / 2f - mPointSelectBitmap.height / 2f, mPointPaint)
            }
        }

        LOG.d("", "mCurrentX - mThumbBitmap.width / 2f ${mCurrentX - mThumbBitmap.width / 2f}   mCurrentX:$mCurrentX  mThumbBitmap.width:${mThumbBitmap.width}")
        // 实体圆
        canvas.drawBitmap(mThumbBitmap, mCurrentX - mThumbBitmap.width / 2f, mCenterY - mThumbBitmap.height / 2f, mThumbPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mCurrentX = event.x
        when (event.action) {
            MotionEvent.ACTION_DOWN -> invalidate()
            MotionEvent.ACTION_UP, MotionEvent.ACTION_MOVE -> {
                invalidate()
                // 回到最近的一个刻度点
                val targetPoint = getNearestPoint(mCurrentX)
                if (targetPoint != null) {
                    // 最终
                    mCurrentX = mPointList[mCurrentPointId].x.toFloat()
                    invalidate()
                }
                if (mPrePointX != mCurrentX) {
                    onChangeCallbackListener?.invoke(mCurrentPointId)
                    mPrePointX = mCurrentX
                }
            }
            else -> {
            }
        }
        return true
    }

    /**
     * 获取最近的刻度
     */
    private fun getNearestPoint(x: Float): Point? {
        for (i in mPointList.indices) {
            val point = mPointList[i]
            if (abs(point.x - x) < mItemWidth / 2f) {
                mCurrentPointId = i
                return point
            }
        }
        return null
    }

    fun setChangeCallbackListener(listener: ((Int) -> Unit)?) {
        onChangeCallbackListener = listener
    }

    private var onChangeCallbackListener: ((Int) -> Unit)? = null

    fun setDefaultPosition(position: Int) {
        mDefaultPointsId = position
        onChangeCallbackListener?.invoke(mDefaultPointsId)
        invalidate()
    }


}