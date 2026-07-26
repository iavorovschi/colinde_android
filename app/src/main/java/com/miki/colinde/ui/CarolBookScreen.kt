package com.miki.colinde.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.miki.colinde.data.CarolBookPreferences
import com.miki.colinde.pdf.PdfReader
import com.miki.colinde.pdf.PdfReaderController
import com.miki.colinde.ui.components.BookmarksDialog
import com.miki.colinde.ui.components.CarolBookTopBar
import com.miki.colinde.ui.components.JumpToPageDialog

@Composable
fun CarolBookScreen(preferences: CarolBookPreferences) {
    var horizontalScroll by remember { mutableStateOf(preferences.horizontalScroll) }
    var pageByPage by remember { mutableStateOf(preferences.pageByPage) }
    var lastPage by remember { mutableIntStateOf(preferences.lastPage) }
    var isLoading by remember { mutableStateOf(true) }
    var openDialog by remember { mutableStateOf<CarolBookDialog?>(null) }
    var controller by remember { mutableStateOf<PdfReaderController?>(null) }

    val jumpToPage: (Int) -> Unit = { page ->
        controller?.jumpTo(page)
        openDialog = null
    }

    Scaffold(
        topBar = {
            CarolBookTopBar(
                enabled = !isLoading,
                horizontalScroll = horizontalScroll,
                pageByPage = pageByPage,
                onBookmarksClick = { openDialog = CarolBookDialog.Bookmarks },
                onJumpToPageClick = { openDialog = CarolBookDialog.JumpToPage },
                onHorizontalScrollChange = { enabled ->
                    lastPage = controller?.currentPage ?: lastPage
                    horizontalScroll = enabled
                    preferences.horizontalScroll = enabled
                    isLoading = true
                },
                onPageByPageChange = { enabled ->
                    pageByPage = enabled
                    preferences.pageByPage = enabled
                }
            )
        },
        containerColor = Color.Black
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = contentPadding.calculateTopPadding())
        ) {
            PdfReader(
                horizontalScroll = horizontalScroll,
                pageByPage = pageByPage,
                initialPage = lastPage,
                onLoaded = { isLoading = false },
                onPageChanged = { page ->
                    lastPage = page
                    preferences.lastPage = page
                },
                onControllerReady = { controller = it }
            )

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White
                    )
                }
            }
        }
    }

    when (openDialog) {
        CarolBookDialog.Bookmarks -> BookmarksDialog(
            onDismiss = { openDialog = null },
            onPageSelected = jumpToPage
        )

        CarolBookDialog.JumpToPage -> JumpToPageDialog(
            currentPage = controller?.currentPage ?: 0,
            totalPages = controller?.totalPages ?: 0,
            onDismiss = { openDialog = null },
            onPageSelected = jumpToPage
        )

        null -> Unit
    }
}

private enum class CarolBookDialog {
    Bookmarks,
    JumpToPage
}
