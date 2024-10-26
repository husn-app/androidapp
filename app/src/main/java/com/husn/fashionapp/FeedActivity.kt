package com.husn.fashionapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import com.husn.fashionapp.ui.theme.AppTheme

class FeedActivity : ComponentActivity() {
    private lateinit var signInHelper: SignInHelper
    private val productsState = mutableStateOf<List<Product>>(emptyList())
    private val isLoading = mutableStateOf(true)
    private val fetch_utility = Fetchutilities(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        WindowCompat.setDecorFitsSystemWindows(window, false)

        val signInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {}
        signInHelper = SignInHelper(this, signInLauncher, this)
        if (!AuthManager.isUserSignedIn) {
            signInHelper.signIn()
        } else {
            fetchFeedProducts()
        }

        if (AuthManager.onboardingStage == null || AuthManager.onboardingStage != "COMPLETE") {
            println("launching OnboardingActivity from feedactivity")
            val intent = Intent(this, OnboardingActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish() // Finish FeedActivity to clean up the back stack
            return
        }

        setContent {
            AppTheme {
                CompositionLocalProvider(LocalSignInHelper provides signInHelper) {
                    Crossfade(targetState = isLoading.value) { loading ->
                        if (loading) {
                            FeedLoadingScreen()
                        } else {
                            FeedScreen(
                                products = productsState.value,
                                onWishlistChange = { productId, newValue ->
                                    updateProductWishlistStatus(productId, newValue)
                                })
                        }
                    }
                }
            }
        }
    }
    private fun fetchFeedProducts() {
        fetch_utility.fetchProductsList(relative_url = "/api/feed") { products ->
            runOnUiThread {
                productsState.value = products ?: emptyList()
                isLoading.value = false
                if (productsState.value.isEmpty()) {
                    finish() // Go to previous screen if productsState is empty
                }
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
        backgroundColor = MaterialTheme.colorScheme.background,
        bottomBar = { BottomBar(selectedItem = 1) } // BottomBar placed correctly
    ) { innerPadding -> // Use innerPadding to avoid content overlapping the BottomBar

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            item {
                TopNavBar()
                SearchBar()
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