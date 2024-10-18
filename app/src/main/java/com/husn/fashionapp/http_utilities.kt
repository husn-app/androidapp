package com.husn.fashionapp

import android.content.Context
import okhttp3.Request
import okhttp3.RequestBody

fun get_url_request(context: Context, url: String) : Request {
    var sessionCookie = getSessionCookieFromStorage(context) ?: ""
    val request = Request.Builder()
        .url(url)
        .addHeader("platform", "android")
        .addHeader("Cookie", sessionCookie)
        .get()
        .build()
    return request
}

fun post_url_request(context: Context, url: String, requestBody: RequestBody) : Request {
    var sessionCookie = getSessionCookieFromStorage(context) ?: ""
    val request = Request.Builder()
        .url(url)
        .addHeader("platform", "android")
        .addHeader("Cookie", sessionCookie)
        .post(requestBody)
        .build()
    return request
}
