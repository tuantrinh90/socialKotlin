package com.rozdoum.socialcomponents.views

import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.util.AttributeSet

import com.rozdoum.socialcomponents.R

/**
 * SwipeRefreshLayout compatible with pre Lolly pop android versions
 * (fixed hiding progress view on top of the screen)
 */

class SwipeRefreshPreLollyPop : SwipeRefreshLayout {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        setProgressViewOffset(false,
                resources.getDimension(R.dimen.refresh_layout_start_offset).toInt(),
                resources.getDimension(R.dimen.refresh_layout_end_offset).toInt())
    }
}
