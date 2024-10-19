package com.husn.fashionapp

import android.content.Context
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import okio.IOException
import org.json.JSONArray
import org.json.JSONObject


class Fetchutilities(private val context: Context, private val client: OkHttpClient = OkHttpClient()) {
    fun fetchWishlistData(relative_url: String, callback: (List<Product>?) -> Unit = { products: List<Product>? -> }) {
//        val baseUrl = getString(R.string.husn_base_url)
        val baseUrl = "http://10.0.2.2:5000"
        val url = "$baseUrl/$relative_url"
        val request = get_url_request(context, url)

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val session = response.header("Set-Cookie")
                    saveSessionCookie(session, context)

                    val responseDataString = response.body?.string()
                    responseDataString?.let {
                        val responseData = try {
                            JSONObject(sanitizeJson(it))
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        }
                        val productsJsonArray = responseData?.getJSONArray("products") ?: JSONArray()
                        val productsList = mutableListOf<Product>()
                        for (i in 0 until productsJsonArray.length()) {
                            productsList.add(Product(productsJsonArray.getJSONObject(i)))
                        }
                        callback(productsList)
                    }
                } else {
                    println("Wishlist response failed with status: ${response.code}")
                }
            }
        })
    }

    fun sanitizeJson(jsonString: String): String {
        return jsonString.replace("NaN", "0")
    }
}