package cn.xjw.kotlin_finish.utils.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView

/**
 * Created by xjw on 2018/2/9 0009.
 *
 * 1.View通过刷新重绘视图,时间间隔16ms .绘制时间超过16ms 会给用户感觉卡顿.
 * 2.View适用于主动更新,主线程中刷新页面.无双缓冲.
 * 3.SurfaceView适用于被动更新,频繁的更新,子线程进行页面刷新.双缓冲.
 * 4.
 * 5.这是一个使用SurfaceView 的模板.
 */
class SmartSurfaceView : SurfaceView, SurfaceHolder.Callback, Runnable {

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