package com.kachem.customviews.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

/**
 * 饼状图
 * Created by kachem on 2017/7/14.
 */
class PieChart : View {
    private var mPaint: Paint = Paint()
    private lateinit var rectF: RectF
    private var list: List<ItemInfo>? = null
    private var w: Float = 0f
    private var h: Float = 0f
    private var defaultSize = 0
    private var radius: Float = 0f

    private var totalValue: Int = 0 //统计数据总大小，没设置取所有item的总大小
    private var startAngle: Float = 0.0f //起始角度
    private var curAngle: Float = 0.0f //当前的角度

    constructor(context: Context) : super(context)

    constructor(context: Context, attr: AttributeSet) : super(context, attr) {
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL
        mPaint.color = Color.GRAY
    }

    constructor(context: Context, attr: AttributeSet, defStyleAttr: Int) : super(context, attr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        defaultSize = w.toInt()
        setMeasuredDimension(getSize(widthMeasureSpec), getSize(heightMeasureSpec))
    }

    fun getSize(measureSpec: Int): Int {
        val mode = MeasureSpec.getMode(measureSpec)
        val size = MeasureSpec.getSize(measureSpec)
        var result: Int = defaultSize

        when (mode) {
            MeasureSpec.EXACTLY -> result = size
            MeasureSpec.AT_MOST -> Math.min(defaultSize, size)
            else -> result = defaultSize
        }
        return result
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        this.w = w.toFloat()
        this.h = h.toFloat()
        radius = Math.min(w, h) / 2 * 0.8f
        rectF = RectF(-radius, -radius, radius, radius)
        curAngle = startAngle
    }

    override fun onDraw(canvas: Canvas?) {
        if (list == null)
            return

        canvas?.translate(w / 2, h / 2)
        //canvas?.drawCircle(0f, 0f, radius, mPaint)
        list?.forEach {
            (color, value) ->
            val itemRadius = (value / totalValue.toFloat()) * 360
            mPaint.color = color
            canvas?.drawArc(rectF, curAngle, itemRadius, true, mPaint)
            curAngle += itemRadius
        }
    }

    /**
     * 设置item数据
     */
    fun setData(list: List<ItemInfo>) {
        this.list = list
        if (totalValue == 0) {
            totalValue = list.sumBy { it.value }
        }
        invalidate()
    }

    /**
     * 设置起始角度
     */
    fun setStartAngle(startAngle: Float) {
        this.startAngle = startAngle
    }

    /**
     * 设置总数据大小
     */
    fun setTotalValue(totalValue: Int) {
        this.totalValue = totalValue
        invalidate()
    }

    /**
     * item信息数据类
     */
    data class ItemInfo(var color: Int, var value: Int, var name: String)
}