package com.shingekinokyojin.wallrose.services

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.shingekinokyojin.wallrose.MainActivity
import com.shingekinokyojin.wallrose.R
import com.shingekinokyojin.wallrose.ui.composables.floating.MyFloatingContent
import com.shingekinokyojin.wallrose.utils.PixelConvert
import com.shingekinokyojin.wallrose.utils.ServiceLifecycleOwner
import com.shingekinokyojin.wallrose.utils.ServiceSavedStateRegistryOwner
import kotlin.math.abs


class FloatingWindowService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View
    private lateinit var floatingWindow:View
    private var isBottomInputVisible = false


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager


        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_window_arouse_layout, null)
        floatingWindow = LayoutInflater.from(this).inflate(R.layout.floating_window_layout, null)
        floatingWindow.visibility = View.GONE



        floatingWindow.findViewById<ImageView>(R.id.float_hide).setOnClickListener {
            floatingView.visibility = View.VISIBLE
            floatingWindow.visibility = View.GONE
            isBottomInputVisible = false
        }

        floatingWindow.findViewById<ImageView>(R.id.float_app).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)

            // 获取 ActivityManager 以检查活动栈
            val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val tasks = activityManager.appTasks

            // 检查 MainActivity 是否已经是栈顶
            val isMainActivityAtTop = tasks.any { task ->
                task.taskInfo.topActivity?.className == MainActivity::class.java.name
            }

            // 检查 MainActivity 是否是前台活动
            val isMainActivityForeground = isAppInForeground(this, packageName)


            Log.d("FloatingWindowService", "onCreate: isMainActivityAtTop: $isMainActivityAtTop, isMainActivityForeground: $isMainActivityForeground")

            // 根据 MainActivity 是否在栈顶和是否前台设置不同的标志
            if(isMainActivityAtTop && !isMainActivityForeground){
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }else if(!isMainActivityAtTop){
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }

        }


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

        val layoutParams2 = WindowManager.LayoutParams().apply {
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
            format = PixelFormat.TRANSLUCENT
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            y = 0
            gravity = Gravity.BOTTOM
        }

        val editText = floatingWindow.findViewById<EditText>(R.id.float_edit)

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
                        // 点击事件
                        Log.d("FloatingWindowService", "onCreate: 点击事件")
                        if (isBottomInputVisible) {
                            floatingView.visibility = View.VISIBLE
                            floatingWindow.visibility = View.GONE
                            isBottomInputVisible = false
                        } else {
                            floatingWindow.visibility = View.VISIBLE
                            floatingView.visibility = View.GONE
                            isBottomInputVisible = true
                        }
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
        windowManager.addView(floatingWindow, layoutParams2)

    }


    fun isAppInForeground(context: Context, packageName: String): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcesses = activityManager.runningAppProcesses ?: return false

        for (processInfo in runningAppProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && processInfo.processName == packageName) {
                return true
            }
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        windowManager.removeView(floatingView)
        windowManager.removeView(floatingWindow)
    }
}