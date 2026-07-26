package com.miki.colinde.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.miki.colinde.R

@Composable
fun BookmarksDialog(onDismiss: () -> Unit, onPageSelected: (Int) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.book_marks_name)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Bookmarks.forEach { bookmark ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPageSelected(bookmark.page) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(bookmark.title),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            painter = painterResource(R.drawable.ic_forward_arrow),
                            contentDescription = null
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.button_cancel))
            }
        },
        containerColor = colorResource(R.color.dark_grey),
        titleContentColor = Color.White,
        textContentColor = Color.White
    )
}

@Composable
fun JumpToPageDialog(
    currentPage: Int,
    totalPages: Int,
    onDismiss: () -> Unit,
    onPageSelected: (Int) -> Unit
) {
    var pageNumber by remember(currentPage) { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.jump_to_page_title)) },
        text = {
            Column {
                OutlinedTextField(
                    value = pageNumber,
                    onValueChange = {
                        pageNumber = it.filter(Char::isDigit)
                        hasError = false
                    },
                    label = { Text(stringResource(R.string.jump_to_page_hint)) },
                    isError = hasError,
                    supportingText = {
                        if (hasError) {
                            Text(stringResource(R.string.page_number_error))
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                Text(
                    text = "${currentPage + 1}/$totalPages",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedPage = pageNumber.toIntOrNull()
                    if (selectedPage != null && selectedPage in 1..totalPages) {
                        onPageSelected(selectedPage - 1)
                    } else {
                        hasError = true
                    }
                }
            ) {
                Text(stringResource(R.string.button_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.button_cancel))
            }
        },
        containerColor = colorResource(R.color.dark_grey),
        titleContentColor = Color.White,
        textContentColor = Color.White
    )
}

private data class Bookmark(@StringRes val title: Int, val page: Int)

private val Bookmarks = listOf(
    Bookmark(R.string.content_name, 413),
    Bookmark(R.string.literal_content_name, 404),
    Bookmark(R.string.old_carols_name, 317),
    Bookmark(R.string.greetings_name, 334),
    Bookmark(R.string.viflaim_name, 338),
    Bookmark(R.string.songs_name, 347),
    Bookmark(R.string.musical_notes_name, 359)
)
