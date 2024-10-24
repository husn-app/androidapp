package com.husn.fashionapp
import FavoriteButton
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.google.firebase.analytics.FirebaseAnalytics
import com.husn.fashionapp.ui.theme.AppTheme


class ProductDetailsActivity : ComponentActivity() {
    private lateinit var signInHelper: SignInHelper
    private val fetch_utility = Fetchutilities(this)
    private val productsState = mutableStateOf<List<Product>>(emptyList())
    private val currentProductstate = mutableStateOf<Product>(Product())
    private val isWishlistedState = mutableStateOf<Boolean>(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

//        val productIndex = intent.getIntExtra("product_index", 0)
        val productIndex = extractProductIndexFromIntent(intent)

        val signInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            signInHelper.handleSignInResult(result.data)
        }

        signInHelper = SignInHelper(this, signInLauncher, this)

        setContent {
            AppTheme {
                CompositionLocalProvider(LocalSignInHelper provides signInHelper) {
                    FullScreenContent {
                        SearchResultsScreen(
                            query = "",
                            products = productsState.value,
                            currentProduct = currentProductstate.value,
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
        fetch_utility.fetchProductData(index = productIndex) { currentProduct, products ->
            runOnUiThread {
                currentProductstate.value = currentProduct
                if (products != null) {
                    productsState.value = products
                }
                isWishlistedState.value = currentProduct.isWishlisted
            }
        }
    }

    private fun extractProductIndexFromIntent(intent: Intent): Int {
        if (Intent.ACTION_VIEW == intent.action) {
            intent.data?.let { uri ->
                val segments = uri.pathSegments
                if (segments.size >= 3) {
                    return segments[2].toIntOrNull() ?: 0
//                    val indexString = segments.lastOrNull()
//                    return indexString?.toIntOrNull() ?: 0
                }
            }
        }
        return intent.getIntExtra("product_index", 0)
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewProductDetailsScreen() {
    SearchResultsScreen(
        query = "",
        products = getDummyProductsList(),
        currentProduct = getDummyProduct(),
        MainProductView = { product ->  // Passing the MainProductView as a lambda
            MainProductView(product = product)  // Call your MainProductView composable here
        })
}


@Composable
fun MainProductView(product: Product, modifier: Modifier = Modifier, isWishlisted: Boolean = false, onWishlistChange: (Boolean) -> Unit = {}, clickable: () -> Unit = {}) {
    val context = LocalContext.current
    val fetch_utility = Fetchutilities(context)
    val firebaseAnalytics = remember { FirebaseAnalytics.getInstance(context) }
    val processedUrl = remember(product) { fetch_utility.makeProductUrl(context, product) }

    Column(
        modifier = modifier
            .padding(8.dp),
    ) {
        ImageFromUrl(product.primaryImage, clickable = clickable)
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth(1f).padding(start = 0.dp, end = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Row (horizontalArrangement = Arrangement.Start){
                FavoriteButton(isWishlisted = isWishlisted, onWishlistChange = onWishlistChange, productId = product.index)
                ShareButton(url = processedUrl)//product.productUrl)
                Spacer(modifier = Modifier.width(8.dp))
                DisplaySvgIconFromAssets(
                    fileName = "myntra-logo.svg",
                    modifier = Modifier
                        .size(36.dp)
                        .padding(top = 12.dp)
                        .clickable {
                            val bundle = Bundle().apply {
                                putString(FirebaseAnalytics.Param.ITEM_ID, product.index.toString())
                                putString(FirebaseAnalytics.Param.ITEM_NAME, product.productName)
                                putString(FirebaseAnalytics.Param.CONTENT_TYPE, "logo")
                            }
                            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT, bundle)

                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(product.productUrl))
                            context.startActivity(intent)
                            //Load myntra in webview
//                            val intent = Intent(context, WebViewActivity::class.java)
//                            intent.putExtra("URL", product.productUrl) // Put the URL you want to open
//                            context.startActivity(intent)
                        }
                )
            }
            Text(text = "Rs ${product.price}", color = Color.Black, fontSize = 20.sp, modifier = Modifier.padding(top = 12.dp))
        }

        Row(modifier = Modifier.fillMaxWidth(1f).padding(start = 14.dp, end = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = product.brand, color = Color.Black/*MaterialTheme.colorScheme.primary*/, fontSize = 16.sp)
            if (product.rating > 0) {
                Row(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = Color(0xFFC8BEA1),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "%.2f".format(product.rating),
                        color = Color.Black,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFF68827F),  //Burlywood
                        modifier = Modifier.width(16.dp).padding(top = 3.dp)
                    )
                }
            }
        }
        var productName = product.productName.replace(product.brand, "", ignoreCase = true).trimStart()
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = productName, color = Color.Black, fontSize = 12.sp, lineHeight = 14.sp,
            modifier = Modifier.padding(start = 14.dp))
    }
}

@Composable
fun DisplaySvgIconFromAssets(fileName: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // Create an ImageLoader with SVG support
    val imageLoader = ImageLoader.Builder(context)
        .components {
            add(SvgDecoder.Factory())
        }
        .build()

    // Build the asset URI
    val assetUri = "file:///android_asset/$fileName"
//    val assetUri = R.drawable.myntra_logo // This also works.
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(context)
            .data(assetUri)
            .build(),
        imageLoader = imageLoader
    )
    // Display the image
    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}
