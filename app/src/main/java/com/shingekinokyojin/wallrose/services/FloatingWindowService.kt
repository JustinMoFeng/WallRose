package com.shingekinokyojin.wallrose.services

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.shingekinokyojin.wallrose.R
import com.shingekinokyojin.wallrose.ui.composables.floating.MyFloatingContent
import com.shingekinokyojin.wallrose.utils.PixelConvert
import kotlin.math.abs

class FloatingWindowService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val inflater = LayoutInflater.from(this)
        floatingView = inflater.inflate(R.layout.floating_window_layout, null)

        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f


        Log.d("FloatingWindowService", "onCreate: ")

        val layoutParams = WindowManager.LayoutParams().apply {
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
            format = PixelFormat.TRANSLUCENT
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            gravity = Gravity.TOP or Gravity.START
        }

        val clickThreshold = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            10f,
            resources.displayMetrics
        ).toInt()

        val pixelValue = PixelConvert.dpToPx(25f)

        floatingView.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = layoutParams.x
                    initialY = layoutParams.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY

                    initialTouchX >= initialX && initialTouchX < initialX + pixelValue && initialTouchY >= initialY && initialTouchY < initialY + pixelValue
                }
                MotionEvent.ACTION_UP -> {
                    // 当手指抬起时判断是否为点击
                    if (abs(event.rawX - initialTouchX) < clickThreshold && abs(event.rawY - initialTouchY) < clickThreshold) {
                        view.performClick()
                    }
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    // 更新窗口位置
                    val deltaX = event.rawX - initialTouchX
                    val deltaY = event.rawY - initialTouchY

                    // 仅在用户实际移动时更新窗口位置
                    if (abs(deltaX) > clickThreshold || abs(deltaY) > clickThreshold) {
                        layoutParams.x = initialX + deltaX.toInt()
                        layoutParams.y = initialY + deltaY.toInt()
                        windowManager.updateViewLayout(floatingView, layoutParams)
                    }

                    true
                }
                else -> false
            }
        }

        windowManager.addView(floatingView, layoutParams)

    }

    override fun onDestroy() {
        super.onDestroy()
        windowManager.removeView(floatingView)
    }
}