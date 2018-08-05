package top.limuyang2.photolibrary.widget

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Checkable
import top.limuyang2.photolibrary.R


/**
 *
 */

class LPreviewSmoothCheckBox @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr), Checkable {

    private lateinit var mTickPaint: Paint
    private lateinit var mFloorPaint: Paint
    private val mTickPoints: Array<Point> = arrayOf(Point(), Point(), Point())
    private val mCenterPoint: Point = Point()
    private val mTickPath: Path = Path()

    private var mLeftLineDistance: Float = 0.toFloat()
    private var mRightLineDistance: Float = 0.toFloat()
    private var mDrewDistance: Float = 0.toFloat()
    private var mScaleVal = 1.0f
    private var mFloorScale = 1.0f
    private var mWidth: Int = 0
    private var mAnimDuration: Int = 0
    private var mStrokeWidth: Int = 0
    private var mTickWidth: Int = 3
    private var mCheckedColor: Int = 0
    private val mUnCheckedColor: Int = Color.GRAY
    private var mFloorColor: Int = Color.GRAY
    private var mFloorUnCheckedColor: Int = 0

    private var mChecked: Boolean = false
    private var mTickDrawing: Boolean = false

    init {
        initAttr()
    }

    @SuppressLint("CustomViewStyleable")
    private fun initAttr() {
        val ta = context.obtainStyledAttributes(R.styleable.LPPAttr)

        val tickColor = ta.getColor(R.styleable.LPPAttr_l_pp_checkBox_color_tick, COLOR_TICK)
        mAnimDuration = ta.getInt(R.styleable.LPPAttr_l_pp_checkBox_duration, DEF_ANIM_DURATION)
//        mFloorColor = ta.getColor(R.styleable.LPPSmoothCheckBox_color_unchecked_stroke, Color.TRANSPARENT)
        mCheckedColor = ta.getColor(R.styleable.LPPAttr_l_pp_checkBox_color_checked, COLOR_CHECKED)
//        mUnCheckedColor = ta.getColor(R.styleable.LPPSmoothCheckBox_color_unchecked, Color.TRANSPARENT)
        mStrokeWidth = ta.getDimensionPixelSize(R.styleable.LPPAttr_l_pp_checkBox_stroke_width, dp2px(context, 0f))
        mTickWidth = ta.getDimensionPixelSize(R.styleable.LPPAttr_l_pp_checkBox_tick_width, 0)
        ta.recycle()

        mFloorUnCheckedColor = mFloorColor

        mTickPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTickPaint.style = Paint.Style.STROKE
        mTickPaint.strokeCap = Paint.Cap.ROUND
        mTickPaint.color = tickColor

        mFloorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mFloorPaint.style = Paint.Style.STROKE
        mFloorPaint.color = mFloorColor


    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(KEY_INSTANCE_STATE, super.onSaveInstanceState())
        bundle.putBoolean(KEY_INSTANCE_STATE, isChecked)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            val isChecked = state.getBoolean(KEY_INSTANCE_STATE)
            setChecked(isChecked)
            super.onRestoreInstanceState(state.getParcelable(KEY_INSTANCE_STATE))
            return
        }
        super.onRestoreInstanceState(state)
    }

    override fun isChecked(): Boolean {
        return mChecked
    }

    override fun toggle() {
        this.isChecked = !isChecked
    }

    override fun setChecked(checked: Boolean) {
        mChecked = checked
        reset()
        invalidate()
    }

    /**
     * checked with animation
     * @param checked checked
     * @param animate change with animation
     */
    fun setChecked(checked: Boolean, animate: Boolean) {
        if (animate) {
            mTickDrawing = false
            mChecked = checked
            mDrewDistance = 0f
            if (checked) {
                startCheckedAnimation()
            } else {
                startUnCheckedAnimation()
            }
        } else {
            this.isChecked = checked
        }
    }

    private fun reset() {
        mTickDrawing = true
        mFloorScale = 1.0f
        mScaleVal = if (isChecked) 0f else 1.0f
        mFloorColor = if (isChecked) mCheckedColor else mFloorUnCheckedColor
        mDrewDistance = if (isChecked) mLeftLineDistance + mRightLineDistance else 0f
    }

    private fun measureSize(measureSpec: Int): Int {
        val defSize = dp2px(context, DEF_DRAW_SIZE.toFloat())
        val specSize = View.MeasureSpec.getSize(measureSpec)
        val specMode = View.MeasureSpec.getMode(measureSpec)

        var result = 0
        when (specMode) {
            View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.AT_MOST -> result = Math.min(defSize, specSize)
            View.MeasureSpec.EXACTLY                               -> result = specSize
        }
        return result
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measureSize(widthMeasureSpec), measureSize(heightMeasureSpec))
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        mWidth = measuredWidth
        mStrokeWidth = if (mStrokeWidth == 0) measuredWidth / 10 else mStrokeWidth
        mStrokeWidth = if (mStrokeWidth > measuredWidth / 5) measuredWidth / 5 else mStrokeWidth
        mStrokeWidth = if (mStrokeWidth < 3) 3 else mStrokeWidth
        mCenterPoint.x = mWidth / 2
        mCenterPoint.y = measuredHeight / 2

        mTickPoints[0].x = Math.round(measuredWidth.toFloat() / 30 * 7)
        mTickPoints[0].y = Math.round(measuredHeight.toFloat() / 30 * 14)
        mTickPoints[1].x = Math.round(measuredWidth.toFloat() / 30 * 13)
        mTickPoints[1].y = Math.round(measuredHeight.toFloat() / 30 * 20)
        mTickPoints[2].x = Math.round(measuredWidth.toFloat() / 30 * 22)
        mTickPoints[2].y = Math.round(measuredHeight.toFloat() / 30 * 10)

        mLeftLineDistance = Math.sqrt(Math.pow((mTickPoints[1].x - mTickPoints[0].x).toDouble(), 2.0) + Math.pow((mTickPoints[1].y - mTickPoints[0].y).toDouble(), 2.0)).toFloat()
        mRightLineDistance = Math.sqrt(Math.pow((mTickPoints[2].x - mTickPoints[1].x).toDouble(), 2.0) + Math.pow((mTickPoints[2].y - mTickPoints[1].y).toDouble(), 2.0)).toFloat()

        mTickPaint.strokeWidth = if (mTickWidth <= 0) mStrokeWidth.toFloat() else mTickWidth.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        drawBorder(canvas)
//        drawCenter(canvas)
        drawTick(canvas)
    }

    private fun drawBorder(canvas: Canvas) {
        mFloorPaint.color = mFloorColor
        mFloorPaint.alpha = 255
        if (mChecked){
            mFloorPaint.style = Paint.Style.FILL
        } else {
            mFloorPaint.strokeWidth = mStrokeWidth.toFloat()
            mFloorPaint.style = Paint.Style.STROKE
        }
        val radius = mCenterPoint.x
        canvas.drawCircle(mCenterPoint.x.toFloat(), mCenterPoint.y.toFloat(), radius * mFloorScale * 0.9f, mFloorPaint)
    }

    private fun drawTick(canvas: Canvas) {
        if (mTickDrawing && isChecked) {
            drawTickPath(canvas)
        }
    }

    private fun drawTickPath(canvas: Canvas) {
        mTickPath.reset()
        // draw left of the tick
        if (mDrewDistance < mLeftLineDistance) {
            val step: Float = if (mWidth / 20.0f < 3) 3f else mWidth / 20.0f
            mDrewDistance += step
            val stopX = mTickPoints[0].x + (mTickPoints[1].x - mTickPoints[0].x) * mDrewDistance / mLeftLineDistance
            val stopY = mTickPoints[0].y + (mTickPoints[1].y - mTickPoints[0].y) * mDrewDistance / mLeftLineDistance

            mTickPath.moveTo(mTickPoints[0].x.toFloat(), mTickPoints[0].y.toFloat())
            mTickPath.lineTo(stopX, stopY)
            canvas.drawPath(mTickPath, mTickPaint)

            if (mDrewDistance > mLeftLineDistance) {
                mDrewDistance = mLeftLineDistance
            }
        } else {

            mTickPath.moveTo(mTickPoints[0].x.toFloat(), mTickPoints[0].y.toFloat())
            mTickPath.lineTo(mTickPoints[1].x.toFloat(), mTickPoints[1].y.toFloat())
            canvas.drawPath(mTickPath, mTickPaint)

            // draw right of the tick
            if (mDrewDistance < mLeftLineDistance + mRightLineDistance) {
                val stopX = mTickPoints[1].x + (mTickPoints[2].x - mTickPoints[1].x) * (mDrewDistance - mLeftLineDistance) / mRightLineDistance
                val stopY = mTickPoints[1].y - (mTickPoints[1].y - mTickPoints[2].y) * (mDrewDistance - mLeftLineDistance) / mRightLineDistance

                mTickPath.reset()
                mTickPath.moveTo(mTickPoints[1].x.toFloat(), mTickPoints[1].y.toFloat())
                mTickPath.lineTo(stopX, stopY)
                canvas.drawPath(mTickPath, mTickPaint)

                val step = (if (mWidth / 20 < 3) 3 else mWidth / 20).toFloat()
                mDrewDistance += step
            } else {
                mTickPath.reset()
                mTickPath.moveTo(mTickPoints[1].x.toFloat(), mTickPoints[1].y.toFloat())
                mTickPath.lineTo(mTickPoints[2].x.toFloat(), mTickPoints[2].y.toFloat())
                canvas.drawPath(mTickPath, mTickPaint)
            }
        }

        // invalidate
        if (mDrewDistance < mLeftLineDistance + mRightLineDistance) {
            postDelayed({ postInvalidate() }, 10)
        }
    }

    private fun startCheckedAnimation() {
        val animator = ValueAnimator.ofFloat(1.0f, 0f)
        animator.duration = (mAnimDuration / 3 * 2).toLong()
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener { animation ->
            mScaleVal = animation.animatedValue as Float
            mFloorColor = getGradientColor(mUnCheckedColor, mCheckedColor, 1 - mScaleVal)
            postInvalidate()
        }
        animator.start()

        val floorAnimator = ValueAnimator.ofFloat(1.0f, 0.8f, 1.0f)
        floorAnimator.duration = mAnimDuration.toLong()
        floorAnimator.interpolator = LinearInterpolator()
        floorAnimator.addUpdateListener { animation ->
            mFloorScale = animation.animatedValue as Float
            postInvalidate()
        }
        floorAnimator.start()

        drawTickDelayed()
    }

    private fun startUnCheckedAnimation() {
        val animator = ValueAnimator.ofFloat(0f, 1.0f)
        animator.duration = mAnimDuration.toLong()
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener { animation ->
            mScaleVal = animation.animatedValue as Float
            mFloorColor = getGradientColor(mCheckedColor, mFloorUnCheckedColor, mScaleVal)
            postInvalidate()
        }
        animator.start()

        val floorAnimator = ValueAnimator.ofFloat(1.0f, 0.8f, 1.0f)
        floorAnimator.duration = mAnimDuration.toLong()
        floorAnimator.interpolator = LinearInterpolator()
        floorAnimator.addUpdateListener { animation ->
            mFloorScale = animation.animatedValue as Float
            postInvalidate()
        }
        floorAnimator.start()
    }

    private fun drawTickDelayed() {
        postDelayed({
                        mTickDrawing = true
                        postInvalidate()
                    }, mAnimDuration.toLong() / 2)
    }


    companion object {
        private const val KEY_INSTANCE_STATE = "InstanceState"

        private const val COLOR_TICK = Color.WHITE
        //        private const val COLOR_UNCHECKED = Color.WHITE
        private val COLOR_CHECKED = Color.parseColor("#169ce4")
//        private val COLOR_FLOOR_UNCHECKED = Color.parseColor("#DFDFDF")

        private const val DEF_DRAW_SIZE = 25
        private const val DEF_ANIM_DURATION = 100

        private fun getGradientColor(startColor: Int, endColor: Int, percent: Float): Int {
            val startA = Color.alpha(startColor)
            val startR = Color.red(startColor)
            val startG = Color.green(startColor)
            val startB = Color.blue(startColor)

            val endA = Color.alpha(endColor)
            val endR = Color.red(endColor)
            val endG = Color.green(endColor)
            val endB = Color.blue(endColor)

            val currentA = (startA * (1 - percent) + endA * percent).toInt()
            val currentR = (startR * (1 - percent) + endR * percent).toInt()
            val currentG = (startG * (1 - percent) + endG * percent).toInt()
            val currentB = (startB * (1 - percent) + endB * percent).toInt()
            return Color.argb(currentA, currentR, currentG, currentB)
        }

        fun dp2px(context: Context, dipValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dipValue * scale + 0.5f).toInt()
        }
    }
}
