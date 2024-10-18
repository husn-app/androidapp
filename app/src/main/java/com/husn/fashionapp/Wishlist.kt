package com.husn.fashionapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.fashionapp.R
import com.husn.fashionapp.ui.theme.AppTheme
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import okio.IOException
import org.json.JSONArray
import org.json.JSONObject

class WishlistActivity : ComponentActivity() {
    private lateinit var signInHelper: SignInHelper
    private val client = OkHttpClient()
    private val productsState = mutableStateOf<List<Product>>(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val signInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            signInHelper.handleSignInResult(result.data) {

            }
        }
        signInHelper = SignInHelper(this, signInLauncher, this)

        setContent {
            AppTheme {
                CompositionLocalProvider(LocalSignInHelper provides signInHelper) {
                    WishlistScreen(products = productsState.value)
                }
            }
        }
        fetchWishlistData()
    }

    private fun fetchWishlistData() {
        val baseUrl = getString(R.string.husn_base_url)
        val url = "$baseUrl/wishlist_android"
        val request = get_url_request(this, url)

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                // Handle error appropriately
//                runOnUiThread {
//                    // Optionally, display an error message or take appropriate action
//                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val session = response.header("Set-Cookie")
                    saveSessionCookie(session, this@WishlistActivity)

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
                        // Update the products state on the main thread
                        runOnUiThread {
                            productsState.value = productsList
                        }
                    }
                } else {
                    println("Wishlist response failed with status: ${response.code}")
                    // Handle error appropriately
//                    runOnUiThread {
//                        // Optionally, display an error message or take appropriate action
//                    }
                }
            }
        })
    }

    private fun sanitizeJson(jsonString: String): String {
        return jsonString.replace("NaN", "0")
    }
}

@Composable
fun WishlistScreen(products: List<Product>){
    val context = LocalContext.current
    Scaffold(
//        scaffoldState = scaffoldState,
        backgroundColor = Color.Transparent,
        bottomBar = { BottomBar(context = context) } // BottomBar placed correctly
    ) { innerPadding -> // Use innerPadding to avoid content overlapping the BottomBar
        if (AuthManager.isUserSignedIn) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding)
            ) {
                item {
                    TopNavBar()
                }
                itemsIndexed(products.chunked(2)) { index, productPair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Display the first product in the row
                        ProductItemBriefView(/**/
                            product = productPair[0],
                            modifier = Modifier
                                .weight(1f) // Ensure the first product takes up half the space
//                        .padding(end = 8.dp) // Add spacing between the two products
//                            .padding(start = 8.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // Display the second product in the row if available
                        if (productPair.size > 1) {
                            ProductItemBriefView(
                                product = productPair[1],
                                modifier = Modifier
                                    .weight(1f) // Ensure the second product takes up half the space
//                            .padding(end = 8.dp) // Add spacing between the two products
                            )
                        } else {
                            // Add an empty Box to take up the second half of the row
                            Box(
                                modifier = Modifier
                                    .weight(1f)
//                            .padding(end = 8.dp) // Add spacing between the two products
                            )
                        }
                    }
                }
            }
        } else {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }
}