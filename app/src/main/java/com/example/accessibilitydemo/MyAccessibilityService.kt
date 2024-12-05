package com.example.accessibilitydemo

import android.accessibilityservice.AccessibilityService
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.TextView
import android.widget.Toast

class MyAccessibilityService : AccessibilityService() {

    private lateinit var windowManager: WindowManager
    private lateinit var popupView: View

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        // التحقق إذا كان النص المحدد قد تغير
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED) {
            val source = event.source ?: return
            val fullText = source.text?.toString() ?: return

            // تحقق أن المؤشرات ضمن حدود النص
            val fromIndex = event.fromIndex
            val toIndex = event.toIndex

            if (fromIndex in 0 until fullText.length && toIndex in 0..fullText.length && fromIndex != toIndex) {
                val selectedText = fullText.substring(fromIndex, toIndex)
                Log.d("SelectedText", "Selected text: $selectedText")
                showPopup("You selected: $selectedText")
                Toast.makeText(this, "Selected: $selectedText", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onInterrupt() {
        // عند انقطاع الخدمة
    }



    private fun showPopup(message: String) {
        // تهيئة نافذة عائمة جديدة فقط إذا كانت غير مضافة
        if (!::popupView.isInitialized) {
            popupView = LayoutInflater.from(this).inflate(R.layout.floating_window, null)
        }
        val textView = popupView.findViewById<TextView>(R.id.popup_text)
        textView.text = message

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        try {
            windowManager.addView(popupView, layoutParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        Handler().postDelayed({
            if (::popupView.isInitialized) {
                try {
                    windowManager.removeView(popupView)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }, 3000)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::popupView.isInitialized) {
            windowManager.removeView(popupView)
        }
    }
}
