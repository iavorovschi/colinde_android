package com.miki.colinde.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.miki.colinde.R

@Composable
fun CarolBookTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color.White,
            onPrimary = Color.Black,
            surface = colorResource(R.color.dark_grey),
            onSurface = Color.White,
            background = Color.Black,
            onBackground = Color.White
        ),
        content = content
    )
}
