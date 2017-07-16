package com.kachem.customviews.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

/**
 * 自定義view測試類
 * Created by kachem on 2017/7/15 0015.
 */
class TestView : View {
    private var defaultSize = 300

    private lateinit var mPaint: Paint
    private lateinit var rect: RectF
    private var w = 0
    private var h = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attr: AttributeSet) : super(context, attr) {
        init()
    }

    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int) : super(context, attr)

    fun init() {
        mPaint = Paint()
        mPaint.color = Color.BLUE
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = 10f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(getSize(widthMeasureSpec), getSize(heightMeasureSpec))
    }

    /**
     * 获取控件宽高
     */
    fun getSize(measureSpec: Int): Int {
        val mode = MeasureSpec.getMode(measureSpec)
        val size = MeasureSpec.getSize(measureSpec)
        var result: Int = 0

        when (mode) {
            MeasureSpec.UNSPECIFIED -> result = defaultSize
            MeasureSpec.AT_MOST, MeasureSpec.EXACTLY -> result = size
        }

        return result
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        this.w = w
        this.h = h
        rect = RectF(0f, -200f, 200f, 0f)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.translate(w / 2f, h / 2f)
        canvas?.drawCircle(0f, 0f, 200f, mPaint)
        canvas?.drawCircle(0f, 0f, 180f, mPaint)

        for (i in 1..36) {
            canvas?.drawLine(0f, 180f, 0f, 200f, mPaint)
            canvas?.rotate(10f)
        }
    }
}