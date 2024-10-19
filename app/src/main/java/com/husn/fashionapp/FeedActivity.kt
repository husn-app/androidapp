package com.husn.fashionapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.fashionapp.R
import com.husn.fashionapp.ui.theme.AppTheme
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import okio.IOException

class FeedActivity : ComponentActivity() {
    private lateinit var signInHelper: SignInHelper
    private val productsState = mutableStateOf<List<Product>>(emptyList())
    private val fetch_utility = Fetchutilities(this)

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
                    if (productsState.value.isNotEmpty()) {
                        FeedScreen(products = productsState.value)
                    } else {
                        StyleSenseApp(context = this)
                    }
                }
            }
        }
        fetch_utility.fetchWishlistData(relative_url = "feed_android") { products ->
            runOnUiThread {
                productsState.value = products ?: emptyList()
            }
        }
    }
}

@Composable
fun FeedScreen(products: List<Product>){
    var context = LocalContext.current
    Scaffold(
        topBar = { TopNavBar() } ,
        backgroundColor = Color.Transparent,
        bottomBar = { BottomBar(context = context) } // BottomBar placed correctly
    ) { innerPadding -> // Use innerPadding to avoid content overlapping the BottomBar
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            items(products) { product ->
                MainProductView(product, clickable = {
                    val baseUrl = context.getString(R.string.husn_base_url)
                    val url = "$baseUrl/api/product/${product.index}"
                    val request = get_url_request(context, url)
                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            e.printStackTrace()
                        }

                        override fun onResponse(call: Call, response: Response) {
                            if (response.isSuccessful) {
                                val session = response.header("Set-Cookie")
                                saveSessionCookie(session, context)

                                val responseData = response.body?.string()
                                responseData?.let {
                                    val intent = Intent(context, ProductDetailsActivity::class.java)
                                    intent.putExtra("productData", it)
                                    context.startActivity(intent)
                                }
                            } else {
                                //println("Request failed with status: ${response.code}")
                            }
                        }
                    })
                })
            }
        }
    }
}