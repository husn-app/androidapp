package com.husn.fashionapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.husn.fashionapp.ui.theme.AppTheme

class WishlistActivity : ComponentActivity() {
    private lateinit var signInHelper: SignInHelper
    private val productsState = mutableStateOf<List<Product>>(emptyList())
    private val fetch_utility = Fetchutilities(this)
    private val isLoading = mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

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
                    if(isLoading.value){
                        LoadingScreen()
                    }
                    else {
                        WishlistScreen(products = productsState.value)
                    }
                }
            }
        }
        fetch_utility.fetchProductsList(relative_url = "/api/wishlist") { products ->
            runOnUiThread {
                productsState.value = products ?: emptyList()
                isLoading.value = false
            }
        }
    }
}

@Composable
fun WishlistScreen(products: List<Product>){
    val context = LocalContext.current
    Scaffold(
        backgroundColor = Color.Transparent,
        bottomBar = { BottomBar(selectedItem = 3) } // BottomBar placed correctly
    ) { innerPadding -> // Use innerPadding to avoid content overlapping the BottomBar
        if (products.isEmpty()) {
            TopNavBar()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Empty Wishlist",
                    tint = Color.Red,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Your wishlist is empty. Browse products to add to your wishlist.",
                    color = Color.DarkGray,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        else if (AuthManager.isUserSignedIn) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding)
            ) {
                item{
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
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // Display the second product in the row if available
                        if (productPair.size > 1) {
                            ProductItemBriefView(
                                product = productPair[1],
                                modifier = Modifier
                                    .weight(1f)
                            )
                        } else {
                            // Add an empty Box to take up the second half of the row
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                            )
                        }
                    }
                }
            }
        } else {
            val intent = Intent(context, FeedActivity::class.java)
            context.startActivity(intent)
        }
    }
}