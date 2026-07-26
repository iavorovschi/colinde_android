package com.miki.colinde.data

import android.content.Context
import androidx.core.content.edit

class CarolBookPreferences(context: Context) {
    private val preferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    var horizontalScroll: Boolean
        get() = preferences.getBoolean(KEY_HORIZONTAL_SCROLL, true)
        set(value) = preferences.edit { putBoolean(KEY_HORIZONTAL_SCROLL, value) }

    var pageByPage: Boolean
        get() = preferences.getBoolean(KEY_PAGE_BY_PAGE, true)
        set(value) = preferences.edit { putBoolean(KEY_PAGE_BY_PAGE, value) }

    var lastPage: Int
        get() = preferences.getInt(KEY_LAST_PAGE, 0)
        set(value) = preferences.edit { putInt(KEY_LAST_PAGE, value) }

    private companion object {
        const val FILE_NAME = "my_preferences"
        const val KEY_HORIZONTAL_SCROLL = "horizontal_scroll"
        const val KEY_PAGE_BY_PAGE = "scroll_page_by_page"
        const val KEY_LAST_PAGE = "last_page"
    }
}
