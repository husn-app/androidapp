package com.husn.fashionapp

import android.content.Context
import okhttp3.Request

fun addCookiesToRequest(requestBuilder: Request.Builder, context: Context): Request.Builder {
    // Get stored cookies (key-value pairs) from SharedPreferences
    val cookies = getSessionCookieFromStorage(context)

    if (cookies.isNotEmpty()) {
        // Combine cookies into a single string for the "Cookie" header in the format "key1=value1; key2=value2"
        val cookieHeaderValue = cookies.entries.joinToString("; ") { "${it.key}=${it.value}" }
        requestBuilder.addHeader("Cookie", cookieHeaderValue)
        println("Added cookies before request: $cookies")
    }
    else{
        requestBuilder.addHeader("Cookie", "")
        println("No cookies to add before request")
    }
    return requestBuilder
}

fun saveSessionCookie(cookies: List<String>, context: Context) {
    if(cookies == null)
        return
    val sharedPreferences = context.getSharedPreferences("SessionPref", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    val cookieSet = cookies.toSet()
    for (cookie in cookieSet) {
        val keyValuePairs = cookie.split(";")[0]
        val keyValue = keyValuePairs.split("=")
        var key = keyValue[0]
        var value = keyValue[1]
        editor.putString(
            key,
            value
        )
        println("key: $key, value: $value")
    }
    editor.apply()  // Apply changes asynchronously
}

fun getSessionCookieFromStorage(context: Context): Map<String, String> {
    val sharedPreferences = context.getSharedPreferences("SessionPref", Context.MODE_PRIVATE)
    val allEntries: Map<String, *> = sharedPreferences.all

    val cookies: Map<String, String> = allEntries.filterValues { it is String }
        .mapValues { it.value as String }

    // Print the retrieved cookies
    if (cookies.isNotEmpty()) {
        for ((key, value) in cookies) {
            println("Retrieved key: $key, value: $value")
        }
    } else {
        println("No cookies found in storage.")
    }

    return cookies
}

//fun clearSessionCookie(context: Context) {
//    val sharedPreferences = context.getSharedPreferences("SessionPref", Context.MODE_PRIVATE)
//    val cookies = sharedPreferences.getStringSet("session_cookies", null)
//
//    val editor = sharedPreferences.edit()
//    editor.remove("session_cookie") // Remove the cookie specifically
//    editor.apply()
//}

//fun clearSessionCookie(context: Context) {
//    val sharedPreferences = context.getSharedPreferences("SessionPref", Context.MODE_PRIVATE)
//    val editor = sharedPreferences.edit()
//
//    // Retrieve all the entries in SharedPreferences
//    val allEntries = sharedPreferences.all
//
//    // Iterate through the entries and remove all key-value pairs (cookies)
//    for ((key, value) in allEntries) {
//        if (value is String) {
//            editor.remove(key)  // Remove each cookie stored as a separate key
//            println("Removed cookie key: $key")
//        }
//    }
//
//    editor.apply()  // Apply changes asynchronously
//    println("All session cookies have been cleared.")
//}

fun clearSessionCookie(context: Context) {
    val sharedPreferences = context.getSharedPreferences("SessionPref", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()

    // Remove all key-value pairs stored in the SharedPreferences under "SessionPref"
    editor.clear()  // Clear all data in the shared preferences
    editor.apply()  // Apply changes asynchronously

    println("All session cookies have been cleared.")
}

fun getSavedKeyValue(key: String, context: Context): String? {
    val sharedPreferences = context.getSharedPreferences("SessionPref", Context.MODE_PRIVATE)
    return sharedPreferences.getString(key, null) // Returns null if the key doesn't exist
}