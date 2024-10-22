package com.husn.fashionapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

class WebViewActivity : ComponentActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val url = intent.getStringExtra("URL") ?: "https://www.google.com"
        setContent {
            MyWebViewScreen(url)
        }
    }

    @Composable
    fun MyWebViewScreen(url: String) {
        var currentUrl by remember { mutableStateOf(url) }  // Track URL changes

        Surface(
            color = MaterialTheme.colors.background,
            modifier = Modifier.fillMaxSize()
        ) {
            MyWebView(currentUrl)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Composable
    fun MyWebView(url: String) {
        Column {
        TopNavBar()
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    webViewClient = WebViewClient() // Handle navigation within the WebView
                    settings.domStorageEnabled = true
                    settings.databaseEnabled = true
                    settings.loadsImagesAutomatically = true
                    settings.cacheMode = WebSettings.LOAD_DEFAULT
                    settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    webViewClient = WebViewClient()

                    loadUrl(url)
                }
            },
            update = { webView ->
                webView.loadUrl(url) // Or webView.loadUrl(url) if necessary
            },
            modifier = Modifier.fillMaxSize()
        )
            }
    }
}