package com.hemnet.test.common.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import okhttp3.OkHttpClient

@Composable
fun PropertyImage(thumb: String, modifier: Modifier = Modifier) {
    Box(
        Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(4.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp))
    ) {
        val imageLoader = ImageLoader.Builder(LocalContext.current)
            .okHttpClient {
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .header(
                                "User-Agent",
                                "Mozilla/5.0 (Android) Coil"
                            ).build()
                        chain.proceed(request)
                    }.build()
            }.build()

        Image(
            painter = rememberAsyncImagePainter(
                model = thumb,
                imageLoader = imageLoader
            ),
            modifier = modifier,
            contentDescription = "thumb",
            contentScale = ContentScale.Crop,
        )
    }
}
