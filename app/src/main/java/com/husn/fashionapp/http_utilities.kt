package com.husn.fashionapp

import android.content.Context
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

fun post_url_request(context: Context, url: String, requestBodyJson: JSONObject = JSONObject(), requestReferrer: String = "android") : Request {
    val mediaType = "application/json; charset=utf-8".toMediaType()
    val requestBodyString = requestBodyJson.apply {
        put("referrer", requestReferrer)
    }.toString()
    val requestBody = requestBodyString.toRequestBody(mediaType)
    var request = Request.Builder()
        .url(url)
        .addHeader("platform", "android")
        .post(requestBody)
    request = addCookiesToRequest(request, context)
    return request.build()
}