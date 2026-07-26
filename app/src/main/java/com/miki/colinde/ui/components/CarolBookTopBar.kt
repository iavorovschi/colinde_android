package com.miki.colinde.ui.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.miki.colinde.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarolBookTopBar(
    enabled: Boolean,
    horizontalScroll: Boolean,
    pageByPage: Boolean,
    onBookmarksClick: () -> Unit,
    onJumpToPageClick: () -> Unit,
    onHorizontalScrollChange: (Boolean) -> Unit,
    onPageByPageChange: (Boolean) -> Unit
) {
    var showSettings by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        title = { Text(stringResource(R.string.app_name)) },
        navigationIcon = {
            AppBarButton(
                icon = R.drawable.ic_bookmarks,
                description = R.string.book_marks_name,
                enabled = enabled,
                onClick = onBookmarksClick
            )
        },
        actions = {
            AppBarButton(
                icon = R.drawable.ic_search,
                description = R.string.jump_to_page_name,
                enabled = enabled,
                onClick = onJumpToPageClick
            )
            Box {
                AppBarButton(
                    icon = R.drawable.ic_dots_vert,
                    description = R.string.settings_name,
                    enabled = enabled,
                    onClick = { showSettings = true }
                )
                SettingsMenu(
                    expanded = showSettings,
                    horizontalScroll = horizontalScroll,
                    pageByPage = pageByPage,
                    onDismiss = { showSettings = false },
                    onHorizontalScrollChange = { horizontal ->
                        showSettings = false
                        onHorizontalScrollChange(horizontal)
                    },
                    onPageByPageChange = { enabled ->
                        showSettings = false
                        onPageByPageChange(enabled)
                    }
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(R.color.dark_grey),
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}

@Composable
private fun AppBarButton(
    @DrawableRes icon: Int,
    @StringRes description: Int,
    enabled: Boolean,
    onClick: () -> Unit
) {
    IconButton(enabled = enabled, onClick = onClick) {
        Icon(
            painter = painterResource(icon),
            contentDescription = stringResource(description)
        )
    }
}

@Composable
private fun SettingsMenu(
    expanded: Boolean,
    horizontalScroll: Boolean,
    pageByPage: Boolean,
    onDismiss: () -> Unit,
    onHorizontalScrollChange: (Boolean) -> Unit,
    onPageByPageChange: (Boolean) -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        containerColor = colorResource(R.color.dark_grey)
    ) {
        SettingItem(
            title = stringResource(R.string.horizontal_scroll_name),
            checked = horizontalScroll,
            onClick = { onHorizontalScrollChange(!horizontalScroll) }
        )
        SettingItem(
            title = stringResource(R.string.page_by_page_name),
            checked = pageByPage,
            onClick = { onPageByPageChange(!pageByPage) }
        )
    }
}

@Composable
private fun SettingItem(title: String, checked: Boolean, onClick: () -> Unit) {
    DropdownMenuItem(
        text = { Text(title) },
        leadingIcon = {
            Checkbox(checked = checked, onCheckedChange = null)
        },
        onClick = onClick
    )
}
