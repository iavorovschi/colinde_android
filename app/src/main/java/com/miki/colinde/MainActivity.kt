package com.miki.colinde

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.miki.colinde.data.CarolBookPreferences
import com.miki.colinde.ui.CarolBookScreen
import com.miki.colinde.ui.theme.CarolBookTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val preferences = CarolBookPreferences(this)
        setContent {
            CarolBookTheme {
                CarolBookScreen(preferences)
            }
        }
    }
}
