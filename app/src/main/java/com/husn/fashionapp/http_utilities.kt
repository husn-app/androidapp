package com.husn.fashionapp

import android.content.Context
import android.webkit.WebView
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

fun get_url_request(context: Context, url: String) : Request {
    var request = Request.Builder()
        .url(url)
        .addHeader("platform", "android")
        .get()
    request = addCookiesToRequest(request, context)
    return request.build()
}

fun post_url_request(context: Context, url: String, requestBodyJson: JSONObject = JSONObject(), requestReferrer: String = "") : Request {
    val mediaType = "application/json; charset=utf-8".toMediaType()
    val requestBodyString = requestBodyJson.apply {
        put("referrer", "android/$requestReferrer")
    }.toString()
    val requestBody = requestBodyString.toRequestBody(mediaType)
    val defaultUserAgent = System.getProperty("http.agent") ?: "android_okhttp"
    var request = Request.Builder()
        .url(url)
        .addHeader("platform", "android")
        .addHeader("User-Agent", defaultUserAgent)
        .post(requestBody)
    request = addCookiesToRequest(request, context)
    return request.build()
}