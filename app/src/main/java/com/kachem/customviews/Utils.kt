package com.kachem.customviews

import android.content.Context

/**
 * Created by admin on 2017/7/13.
 */

fun px2dp(context: Context, px: Int): Int {
    val mDensity = context.resources.displayMetrics.density
    return (px / mDensity + 0.5f).toInt()
}

fun dp2px(context: Context, dp: Int): Int {
    val mDensity = context.resources.displayMetrics.density
    return (dp * mDensity + 0.5f).toInt()
}