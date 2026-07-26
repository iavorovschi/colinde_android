package com.miki.colinde.pdf

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.ahmer.pdfviewer.PDFView
import com.ahmer.pdfviewer.listener.OnLoadCompleteListener
import com.ahmer.pdfviewer.listener.OnPageChangeListener
import com.ahmer.pdfviewer.scroll.DefaultScrollHandle
import com.ahmer.pdfviewer.util.FitPolicy

class PdfReaderController internal constructor(
    private val pdfView: PDFView,
    private val scrollHandler: DefaultScrollHandle
) {
    val currentPage: Int
        get() = pdfView.currentPage

    val totalPages: Int
        get() = pdfView.pagesCount

    fun jumpTo(page: Int) {
        pdfView.jumpTo(page)
        scrollHandler.syncWith(pdfView)
    }

}

@Composable
fun PdfReader(
    horizontalScroll: Boolean,
    pageByPage: Boolean,
    initialPage: Int,
    onLoaded: () -> Unit,
    onPageChanged: (Int) -> Unit,
    onControllerReady: (PdfReaderController) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentOnLoaded by rememberUpdatedState(onLoaded)
    val currentOnPageChanged by rememberUpdatedState(onPageChanged)
    val currentOnControllerReady by rememberUpdatedState(onControllerReady)

    key(horizontalScroll) {
        AndroidView(
            factory = { context ->
                PDFView(context, null).also { pdfView ->
                    val scrollHandler = DefaultScrollHandle(context)
                    val controller = PdfReaderController(pdfView, scrollHandler)
                    currentOnControllerReady(controller)

                    pdfView.setBestQuality(true)
                    pdfView.fromAsset(PDF_ASSET)
                        .swipeHorizontal(horizontalScroll)
                        .scrollHandle(scrollHandler)
                        .defaultPage(initialPage)
                        .enableAntialiasing(true)
                        .fitEachPage(true)
                        .pageSnap(pageByPage)
                        .pageFling(pageByPage)
                        .pageFitPolicy(FitPolicy.BOTH)
                        .autoSpacing(true)
                        .onLoad(object : OnLoadCompleteListener {
                            override fun loadComplete(totalPages: Int) {
                                currentOnLoaded()
                                pdfView.post { scrollHandler.syncWith(pdfView) }
                            }
                        })
                        .onPageChange(object : OnPageChangeListener {
                            override fun onPageChanged(page: Int, totalPages: Int) {
                                currentOnPageChanged(page)
                                pdfView.post { scrollHandler.syncWith(pdfView) }
                            }
                        })
                        .load()
                }
            },
            update = { pdfView ->
                pdfView.setPageMode(pageByPage)
            },
            onRelease = { pdfView ->
                pdfView.recycle()
            },
            modifier = modifier.fillMaxSize()
        )
    }
}

private const val PDF_ASSET = "book.pdf"

private fun PDFView.setPageMode(enabled: Boolean) {
    setPageSnap(enabled)
    setPageFling(enabled)
}

private fun DefaultScrollHandle.syncWith(pdfView: PDFView) {
    setScroll(pdfView.getPositionOffset())
    setPageNumber(pdfView.currentPage + 1)
    hideDelayed()
}
