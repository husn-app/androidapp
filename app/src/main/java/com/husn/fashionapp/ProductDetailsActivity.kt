package com.husn.fashionapp

import FavoriteButton
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.analytics.FirebaseAnalytics
import com.husn.fashionapp.ui.theme.AppTheme


class ProductDetailsActivity : ComponentActivity() {
    private lateinit var signInHelper: SignInHelper
    private val fetch_utility = Fetchutilities(this)
    private val productsState = mutableStateOf<List<Product>>(emptyList())
    private val currentProductstate = mutableStateOf<Product>(Product())
    private val isWishlistedState = mutableStateOf<Boolean>(false)
    private val isLoading = mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //WindowCompat.setDecorFitsSystemWindows(window, false)
        val appLinkIntent: Intent = intent
        val appLinkAction: String? = appLinkIntent.action
        val appLinkData: Uri? = appLinkIntent.data
//        val productIndex = intent.getIntExtra("product_index", 0)
        val productIndex = extractProductIndexFromIntent(intent)
        val referrer = intent.getStringExtra("referrer") ?: ""

        val signInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            signInHelper.handleSignInResult(result.data)
        }

        signInHelper = SignInHelper(this, signInLauncher, this)

        setContent {
            AppTheme {
                CompositionLocalProvider(LocalSignInHelper provides signInHelper) {
                    Crossfade(targetState = isLoading.value) { loading ->
                        if (loading) {
                            ProductLoadingScreen()
                        } else {
                            SearchResultsScreen(
                                query = "",
                                products = productsState.value,
                                currentProduct = currentProductstate.value,
                                referrer = "product/product_id=${currentProductstate.value.index}",
                                MainProductView = { product ->  // Passing the MainProductView as a lambda
                                    MainProductView(product = product,
                                        isWishlisted = isWishlistedState.value,
                                        onWishlistChange = { newValue ->
                                            isWishlistedState.value = newValue
                                            // Optionally, handle any side effects here
                                        })  // Call your MainProductView composable here
                                })
                        }
                    }
                }
            }
        }
        fetch_utility.fetchProductData(index = productIndex, referrer = referrer) { currentProduct, products ->
            runOnUiThread {
                currentProductstate.value = currentProduct
                if (products != null) {
                    productsState.value = products
                }
                isWishlistedState.value = currentProduct.isWishlisted
                isLoading.value = false
            }
        }
        // ATTENTION: This was auto-generated to handle app links.
    }

    private fun extractProductIndexFromIntent(intent: Intent): Int {
        if (Intent.ACTION_VIEW == intent.action) {
            intent.data?.let { uri ->
                val segments = uri.pathSegments
                if (segments.size >= 3) {
                    return segments[2].toIntOrNull() ?: 0
                }
            }
        }
        return intent.getIntExtra("product_index", 0)
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewProductDetailsScreen() {
    AppTheme {
        CompositionLocalProvider(
            LocalSignInHelper provides null, // Provide null or a mock SignInHelper if needed
            LocalContext provides LocalContext.current
        ) {
            SearchResultsScreen(
                query = "",
                products = getDummyProductsList(),
                currentProduct = getDummyProduct(),
                MainProductView = { product ->  // Passing the MainProductView as a lambda
                    MainProductView(product = product)  // Call your MainProductView composable here
                })
        }
    }
}


@Composable
fun MainProductView(product: Product, modifier: Modifier = Modifier, isWishlisted: Boolean = false, onWishlistChange: (Boolean) -> Unit = {}, clickable: () -> Unit = {}) {
    val context = LocalContext.current
    val fetch_utility = Fetchutilities(context)
    val firebaseAnalytics = remember(context) {
        try {
            FirebaseAnalytics.getInstance(context)
        } catch (e: Exception) {
            null
        }
    }
    val processedUrl = remember(product) { fetch_utility.makeProductUrl(context, product) }
    val iconSize = 28.dp
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .padding(8.dp),
    ) {
        ImageFromUrl(product.primaryImage, clickable = clickable)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(1f).padding(horizontal = 0.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Row (horizontalArrangement = Arrangement.Start){
                FavoriteButton(isWishlisted = isWishlisted, onWishlistChange = onWishlistChange, productId = product.index, iconSize = iconSize)
                ShareButton(url = processedUrl, iconSize = iconSize)
                Image(
                    painter = painterResource(id = R.drawable.myntra_logo1),
                    contentDescription = "Inspiration",
                    modifier = Modifier.padding(horizontal = 4.dp).size(size = 36.dp)
                        .clickable(interactionSource = interactionSource, indication = null) {
                            val bundle = Bundle().apply {
                                putString(FirebaseAnalytics.Param.ITEM_ID, product.index.toString())
                                putString(FirebaseAnalytics.Param.ITEM_NAME, product.productName)
                                putString(FirebaseAnalytics.Param.CONTENT_TYPE, "logo")
                            }
                            firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT, bundle)

                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(product.productUrl))
                            context.startActivity(intent)
                            //Load myntra in webview
//                            val intent = Intent(context, WebViewActivity::class.java)
//                            intent.putExtra("URL", product.productUrl) // Put the URL you want to open
//                            context.startActivity(intent)
                        }
                )
            }
            Text(text = "Rs ${product.price}", color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
        }

        Row(modifier = Modifier.fillMaxWidth(1f).padding(start = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = product.brand, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
            if (product.rating > 0) {
                Row(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
//                            color = Color(0xFFC8BEA1),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "%.2f".format(product.rating),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFF68827F),
                        modifier = Modifier.width(16.dp)
                    )
                }
            }
        }
        var productName = product.productName.replace(product.brand, "", ignoreCase = true).trimStart()
//        Spacer(modifier = Modifier.height(4.dp))
        Text(text = productName, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, lineHeight = 14.sp,
            modifier = Modifier.padding(start = 8.dp))
    }
}
