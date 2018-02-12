package cn.xjw.kotlin_finish.utils.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * Created by xjw on 2018/2/9 0009.
 */
class SmartQuadToView : View {

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    constructor(context: Context?, attributeSet: AttributeSet?, defSet: Int) : super(context, attributeSet, defSet)

    private val mViewW by lazy { measuredWidth }
    private val mViewH by lazy { measuredHeight }
    private val startPoint by lazy { PointF(200f, mViewH / 2f) }
    private val endPoint by lazy { PointF(mViewW - 200f, mViewH / 2f) }
    private val conPoint by lazy { PointF(mViewW / 2f, mViewH / 4f) }
    private val mPaintPxy by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL_AND_STROKE
            color = Color.RED
            strokeWidth = 13f
        }
    }
    private val quadPath by lazy { Path() }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //起点,终点,控制点
        drawPoint(canvas)
        //二阶贝塞尔
        drawPointC(canvas)
        //辅助线
        drawLine(canvas)
    }

    private fun drawLine(canvas: Canvas?) {
        mPaintPxy.color = Color.BLUE
        mPaintPxy.strokeWidth = 3f
        canvas!!.drawLine(startPoint.x, startPoint.y, conPoint.x, conPoint.y, mPaintPxy)
        canvas.drawLine(endPoint.x, endPoint.y, conPoint.x, conPoint.y, mPaintPxy)
    }

    private fun drawPointC(canvas: Canvas?) {
        mPaintPxy.color = Color.RED
        mPaintPxy.strokeWidth = 3f
        mPaintPxy.style = Paint.Style.STROKE
        quadPath.reset()
        quadPath.moveTo(startPoint.x,startPoint.y)
        quadPath.quadTo(conPoint.x, conPoint.y, endPoint.x, endPoint.y)
        canvas!!.drawPath(quadPath, mPaintPxy)
    }

    private fun drawPoint(canvas: Canvas?) {
        mPaintPxy.color = Color.RED
        mPaintPxy.strokeWidth = 13f
        canvas!!.drawPoint(startPoint.x, startPoint.y, mPaintPxy)
        canvas.drawPoint(endPoint.x, endPoint.y, mPaintPxy)
        mPaintPxy.color = Color.BLUE
        canvas.drawPoint(conPoint.x, conPoint.y, mPaintPxy)
    }

    //?.  : 表示当前对象可否为空
    //!!. : 表示在当前对象不为空的情况下
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                conPoint.x = event.x
                conPoint.y = event.y
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP -> {
                return false
            }
            else -> return false
        }
    }

}