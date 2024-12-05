package com.example.accessibilitydemo

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // هنا يمكنك وضع واجهتك باستخدام Compose
        }

        if (!checkAccessibilityPermission()) {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }

        if (!checkDrawOverlayPermission()) {
            Toast.makeText(this, "Please enable the permission for overlay", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkAccessibilityPermission(): Boolean {
        var accessEnabled = 0
        try {
            accessEnabled =
                Settings.Secure.getInt(contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED)
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
        }

        if (accessEnabled == 0) {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            return false
        } else {
            return true
        }
    }

    // التحقق من إذن عرض النوافذ العائمة
    private fun checkDrawOverlayPermission(): Boolean {
        if (!Settings.canDrawOverlays(this)) {
            // إذا لم يكن لديك إذن، افتح الإعدادات للسماح بهذا الإذن
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            startActivityForResult(intent, 123) // Use an appropriate request code
            return false
        }
        return true
    }
}
