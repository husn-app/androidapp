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
import com.husn.fashionapp.ui.theme.AppTheme

class FeedActivity : ComponentActivity() {
    private lateinit var signInHelper: SignInHelper
    private val productsState = mutableStateOf<List<Product>>(emptyList())
    private val fetch_utility = Fetchutilities(this)
    private val isWishlistedState = mutableStateOf<Boolean>(false)

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
                        FeedScreen(products = productsState.value, onWishlistChange = { productId, newValue ->
                            updateProductWishlistStatus(productId, newValue)
                        })
                    } else {
                        StyleSenseApp(context = this)
                    }
                }
            }
        }
        fetch_utility.fetchProductsList(relative_url = "/api/feed") { products ->
            runOnUiThread {
                productsState.value = products ?: emptyList()
            }
        }
    }

    private fun updateProductWishlistStatus(productId: Int, newValue: Boolean) {
        productsState.value = productsState.value.map { product ->
            if (product.index == productId) {
                product.copy(isWishlisted = newValue)
            } else {
                product
            }
        }
    }
}

@Composable
fun FeedScreen(products: List<Product>,
               onWishlistChange: (Int, Boolean) -> Unit){
    var context = LocalContext.current
    Scaffold(
        backgroundColor = Color.Transparent,
        bottomBar = { BottomBar(context = context, selectedItem = 1) } // BottomBar placed correctly
    ) { innerPadding -> // Use innerPadding to avoid content overlapping the BottomBar

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            item {
                TopNavBar()
            }
            items(products) { product ->
                MainProductView(product,
                    isWishlisted = product.isWishlisted,
                    onWishlistChange = { newValue ->
                        onWishlistChange(product.index, newValue)
                    },
                    clickable = {
                    val intent = Intent(context, ProductDetailsActivity::class.java).apply {
                        putExtra("product_index", product.index)
                    }
                    context.startActivity(intent)
                })
            }
        }
    }
}