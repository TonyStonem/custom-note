package cn.xjw.kotlin_finish.utils.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView

/**
 * Created by xjw on 2018/2/9 0009.
 *
 * 1.借助android.graphics.Path 即可实现贝塞尔曲线
 * 2.Path.moveTo() : Path的初始点
 * 3.Path.lineTo() : 直线
 * 4.Path.quadTo() : 二阶贝塞尔
 * 5.Path.cubicTo() : 三阶贝塞尔
 * 6.
 * 7.
 * 8.
 */
class SmartBcSView : SurfaceView, SurfaceHolder.Callback, Runnable {

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var isDrawing = false
    private val mHolder : SurfaceHolder by lazy { holder }
    private val mCanvas : Canvas by lazy { mHolder.lockCanvas() }

    init {
        mHolder.addCallback(this)
        isFocusable = true
        isFocusableInTouchMode = true
        this.keepScreenOn = true
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        isDrawing = false
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        isDrawing = true
        Thread(this).start()
    }

    override fun run() {
        while (isDrawing) {
            drawBegin()
        }
    }

    private fun drawBegin() {

    }

}