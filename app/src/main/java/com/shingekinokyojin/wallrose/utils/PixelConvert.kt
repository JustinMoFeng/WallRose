package com.shingekinokyojin.wallrose.utils

import android.content.res.Resources

class PixelConvert {
    companion object {
        fun dpToPx(dp: Float): Float {
            return dp * Resources.getSystem().displayMetrics.density
        }

        fun pxToDp(px: Float): Float {
            return px / Resources.getSystem().displayMetrics.density
        }
    }
}