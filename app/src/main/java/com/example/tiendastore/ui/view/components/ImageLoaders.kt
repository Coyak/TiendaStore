package com.example.tiendastore.ui.view.components

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.MaterialTheme
import java.io.File

@Composable
fun ImageFromPath(path: String?, modifier: Modifier = Modifier, contentScale: ContentScale = ContentScale.Crop) {
    if (path.isNullOrBlank() || !File(path).exists()) {
        Box(modifier.background(MaterialTheme.colorScheme.surfaceVariant))
        return
    }
    val bmp = BitmapFactory.decodeFile(path)
    if (bmp != null) {
        Image(bitmap = bmp.asImageBitmap(), contentDescription = null, modifier = modifier, contentScale = contentScale)
    } else {
        Box(modifier.background(MaterialTheme.colorScheme.surfaceVariant))
    }
}

@Composable
fun ImageFromUri(uriString: String?, modifier: Modifier = Modifier, contentScale: ContentScale = ContentScale.Crop) {
    val context = LocalContext.current
    if (uriString.isNullOrBlank()) {
        Box(modifier.background(MaterialTheme.colorScheme.surfaceVariant))
        return
    }
    val bitmapState = produceState(initialValue = null as android.graphics.Bitmap?, uriString) {
        value = runCatching {
            val uri = Uri.parse(uriString)
            context.contentResolver.openInputStream(uri).use { input ->
                if (input != null) BitmapFactory.decodeStream(input) else null
            }
        }.getOrNull()
    }
    val bmp = bitmapState.value
    if (bmp != null) {
        Image(bitmap = bmp.asImageBitmap(), contentDescription = null, modifier = modifier, contentScale = contentScale)
    } else {
        Box(modifier.background(MaterialTheme.colorScheme.surfaceVariant))
    }
}

