package com.husn.fashionapp

import android.content.Context
import com.example.fashionapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import okio.IOException
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLDecoder


class Fetchutilities(private val context: Context, private val client: OkHttpClient = OkHttpClient()) {
    fun fetchProductsList(relative_url: String, requestBodyJson: JSONObject = JSONObject(), callback: (List<Product>?) -> Unit = { products: List<Product>? -> }) {
        val baseUrl = context.getString(R.string.husn_base_url)
        val url = "$baseUrl/$relative_url"
        val request = post_url_request(context, url, requestBodyJson)

        var retryCount = 0
        val maxRetries = 1

        fun makeRequest() {
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    if (retryCount < maxRetries) {
                        retryCount++
                        println("fetchProductsList request failed. Retrying (attempt ${retryCount + 1}). Error: ${e.message}")
                        // Introduce a short delay before retrying
                        CoroutineScope(Dispatchers.IO).launch {
                            delay(200) // Delay for 500ms
                            makeRequest()
                        }

                    } else {
                        println("fetchProductsList request failed after multiple retries. Error: ${e.message}")
                        callback(null) // Indicate failure to the caller
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val headers = response.headers
                        val cookies = headers.values("Set-Cookie")
                        saveSessionCookie(cookies, context)

                        val responseDataString = try {
                            response.body?.string()
                        } catch (e: IOException) {
                            onFailure(call, e)
                            e.printStackTrace()
                            // Go to previous screen or handle the error appropriately
                            return
                        }
                        responseDataString?.let {
                            val responseData = try {
                                JSONObject(sanitizeJson(it))
                            } catch (e: Exception) {
                                e.printStackTrace()
                                null
                            }
                            val productsJsonArray =
                                responseData?.getJSONArray("products") ?: JSONArray()
                            val productsList = mutableListOf<Product>()
                            for (i in 0 until productsJsonArray.length()) {
                                productsList.add(Product(productsJsonArray.getJSONObject(i)))
                            }
                            callback(productsList)
                        }
                    } else {
                        println("fetchProductsList response failed with status: ${response.code}\n $response")
                    }
                }
            })
        }
        makeRequest()
    }

    fun sanitizeJson(jsonString: String): String {
        return jsonString.replace("NaN", "0")
    }

    fun fetchProductData(
        index: Int,
        callback: (currentProduct: Product, products: List<Product>?) -> Unit
    ) {
        val baseUrl = context.getString(R.string.husn_base_url)
        val url = "$baseUrl/api/product/${index}"
        val request = post_url_request(context, url)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
//                    val session = response.header("Set-Cookie")
                    val headers = response.headers
                    val cookies = headers.values("Set-Cookie")
                    saveSessionCookie(cookies, context)

                    val responseData = response.body?.string()
//                    println("fetchProductData: responseData: $responseData")
                    responseData?.let {
//                        var is_wishlisted: Boolean = false
                        // Parse the response data
                        var currentProductJson: JSONObject = JSONObject()
                        var productsJsonArray: JSONArray = JSONArray()
                        try {
                            var responseData =
                                JSONObject(sanitizeJson(it))
                            currentProductJson = responseData.getJSONObject("product")
                            productsJsonArray = responseData.getJSONArray("similar_products")
//                            is_wishlisted = responseData.getBoolean("is_wishlisted")
                        } catch (e: Exception) {
                            println("fetchProductData: main product opening failed : $e")
//                            onBackPressedDispatcher.onBackPressed()
                        }

                        val currentProduct = Product(currentProductJson)

                        val products = mutableListOf<Product>()
                        for (i in 1 until productsJsonArray.length()) {  // Start loop from 1 to skip the first product
                            val productJson = productsJsonArray.getJSONObject(i)
                            val product = Product(productJson)
                            products.add(product)
                        }
                        callback(currentProduct, products)
                    }
                } else {
                    println("fetchProductData: fetching product with $index failed with status: ${response.code}\n${response}")
                }
            }
        })
    }

    fun makeProductUrl(context: Context, product: Product): String {
        val decodedUrl = URLDecoder.decode(product.productUrl, "UTF-8") // Decode URL-encoded characters
        val pathSegments = decodedUrl.split("/").filter { it.isNotBlank() }
//        println("productDetails: $pathSegments")
        val baseUrl = context.getString(R.string.husn_base_url)
        if (pathSegments.size < 3) {
            return baseUrl
        }

        val allUrl = "$baseUrl/product/${pathSegments[pathSegments.size - 3]}/${product.index}"
//        println("productUrl: $allUrl")
        return allUrl
//        return baseUrl
    }
}