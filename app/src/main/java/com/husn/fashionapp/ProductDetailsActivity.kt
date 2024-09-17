package com.husn.fashionapp
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.husn.fashionapp.ui.theme.AppTheme
import org.json.JSONArray
import org.json.JSONObject


class ProductDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve the product data from the intent
        val productDataString = intent.getStringExtra("productData") ?: ""

        // Parse the response data
        var currentProductJson: JSONObject = JSONObject()
        var productsJsonArray: JSONArray = JSONArray()
        try {
            var responseData = JSONObject(productDataString)
            currentProductJson = responseData.getJSONObject("current_product")
            productsJsonArray = responseData.getJSONArray("products")
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

        setContent {
            AppTheme {
//                ProductDetailsScreen(currentProduct = currentProduct, relatedProducts = products)
                SearchResultsScreen(query = "", products = products, currentProduct = currentProduct, ProductItemView = { product ->  // Passing the ProductItemView as a lambda
                    ProductItemView(product = product)  // Call your ProductItemView composable here
                })
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewProductDetailsScreen() {
//    ProductDetailsScreen(currentProduct = dummyCurrentProduct, relatedProducts = dummyRelatedProducts)
    SearchResultsScreen(query = "", products = getDummyProductsList(), currentProduct = getDummyProduct(), ProductItemView = { product ->  // Passing the ProductItemView as a lambda
        ProductItemView(product = product)  // Call your ProductItemView composable here
    })
}

@Composable
fun ProductDetailsScreen(currentProduct: Product, relatedProducts: List<Product>) {
    Column(modifier = Modifier.fillMaxSize().padding(12.dp),
        verticalArrangement = Arrangement.SpaceEvenly
        ) {
            SearchBar()  // Call SearchBar here
        // Display current product
        ProductItemView(product = currentProduct)

        Spacer(modifier = Modifier.height(12.dp))
        ProductsListView(relatedProducts)

//        val configuration = LocalConfiguration.current
//        val screenWidth = configuration.screenWidthDp.dp
//        val availableHeight = configuration.screenHeightDp.dp - 200.dp
//        LazyRow(
//            contentPadding = PaddingValues(horizontal = 8.dp),  // Add padding to the row
//            horizontalArrangement = Arrangement.spacedBy(8.dp)  // Spacing between items
//        ) {
//            items(relatedProducts) { product ->
//                ProductItemBriefView(
//                    product = product,
//                    textScale = 0.6f,
//                    modifier = Modifier
//                        .height(availableHeight)
//                        .width(screenWidth / 2.5f)  // Responsive width, adjust based on screen size
//                        .wrapContentHeight()  // Let the height wrap to the content naturally
//                )
//            }
//        }
    }
}

@Composable
fun ProductItemView(product: Product, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberAsyncImagePainter(product.searchImage),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.75f) // Maintain aspect ratio
                .clip(RoundedCornerShape(16.dp))
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        )
            // verticalAlignment = Alignment.CenterVertically
        {
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.Start
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (product.rating > 0) {
                        Text(text = "${"%.2f".format(product.rating)} ★", color = Color.Gray, fontSize = 10.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(text = "Rs ${product.price}", color = Color.Gray, fontSize = 10.sp)
                }
                product.brand?.let { Text(text = it, color = Color.Gray, fontSize = 12.sp) }
                Text(text = "${product.additionalInfo}", color = Color.Gray, fontSize = 8.sp)
            }
            // Right-aligned clickable SVG icon that redirects to myntra.com/${product.landingPageUrl}
            val context = LocalContext.current
            DisplaySvgIconFromAssets(
                fileName = "myntra-logo.svg",
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://myntra.com/${product.landingPageUrl}"))
                        context.startActivity(intent)
                    }
            )
            // DisplaySvgIconFromAssets(fileName = "myntra-logo.svg", modifier = Modifier.weight(0.2f).aspectRatio(1.0f))
        }
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

    // Load the SVG file as a drawable into Coil
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
