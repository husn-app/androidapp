package com.husn.fashionapp
import FavoriteButton
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.google.firebase.analytics.FirebaseAnalytics
import com.husn.fashionapp.ui.theme.AppTheme
import org.json.JSONArray
import org.json.JSONObject


class ProductDetailsActivity : ComponentActivity() {
    private lateinit var signInHelper: SignInHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve the product data from the intent
        val productDataString = intent.getStringExtra("productData") ?: ""
        var is_wishlisted: Boolean = false
        // Parse the response data
        var currentProductJson: JSONObject = JSONObject()
        var productsJsonArray: JSONArray = JSONArray()
        try {
            var responseData = JSONObject(productDataString)
            currentProductJson = responseData.getJSONObject("current_product")
            productsJsonArray = responseData.getJSONArray("products")
            is_wishlisted = responseData.getBoolean("is_wishlisted")
        } catch (e: Exception) {
            // Log the exception if needed
            onBackPressedDispatcher.onBackPressed()
        }

        // Convert current product and products JSON array to Product objects
        val currentProduct = Product(currentProductJson)

        val products = mutableListOf<Product>()
        for (i in 1 until productsJsonArray.length()) {  // Start loop from 1 to skip the first product
            val productJson = productsJsonArray.getJSONObject(i)
            val product = Product(productJson)
            products.add(product)
        }

        val signInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            signInHelper.handleSignInResult(result.data)
        }

        signInHelper = SignInHelper(this, signInLauncher, this)

        setContent {
            AppTheme {
                CompositionLocalProvider(LocalSignInHelper provides signInHelper) {
                    SearchResultsScreen(
                        query = "",
                        products = products,
                        currentProduct = currentProduct,
                        MainProductView = { product ->  // Passing the MainProductView as a lambda
                            MainProductView(product = product, is_wishlisted = is_wishlisted)  // Call your MainProductView composable here
                        })
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewProductDetailsScreen() {
    SearchResultsScreen(query = "", products = getDummyProductsList(), currentProduct = getDummyProduct(), MainProductView = { product ->  // Passing the MainProductView as a lambda
        MainProductView(product = product)  // Call your MainProductView composable here
    })
}

@Composable
fun MainProductView(product: Product, modifier: Modifier = Modifier, is_wishlisted: Boolean = false) {
    val context = LocalContext.current
    val firebaseAnalytics = remember { FirebaseAnalytics.getInstance(context) }

    Column(
        modifier = modifier
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberAsyncImagePainter(product.primaryImage),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.75f) // Maintain aspect ratio
                .clip(RoundedCornerShape(16.dp))
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth(1f).padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Row {
                FavoriteButton(initialIsWishlisted = is_wishlisted, productId = product.index)
                ShareButton(url = product.productUrl)
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
                            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)

                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(product.productUrl))
                            context.startActivity(intent)
                        }
                )
            }
            Text(text = "Rs ${product.price}", color = Color.Black, fontSize = 20.sp, modifier = Modifier.padding(top = 12.dp))
        }

        Row(modifier = Modifier.fillMaxWidth(1f).padding(start = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = product.brand, color = Color.Black/*MaterialTheme.colorScheme.primary*/, fontSize = 16.sp)
            Row {
                if (product.rating > 0) {
                    Text(
                        text = "%.2f".format(product.rating),
                        color = Color.Black,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFDEB887),  //Burlywood
                        modifier = Modifier.width(14.dp)
                    )
                }
            }
        }
        var productName = product.productName.replace(product.brand, "", ignoreCase = true).trimStart()
        Text(text = productName, color = Color.Black, fontSize = 12.sp, lineHeight = 14.sp,
            modifier = Modifier.padding(start = 20.dp))
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
