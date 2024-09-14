package com.example.fashionapp
//import androidx.compose.material3.MaterialTheme
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.fashionapp.ui.theme.AppTheme
import org.json.JSONArray
import org.json.JSONObject


class ProductDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve the product data from the intent
        val productDataString = intent.getStringExtra("productData") ?: ""

        // Parse the response data
        var responseData: JSONObject = JSONObject()
        var currentProductJson: JSONObject = JSONObject()
        var productsJsonArray: JSONArray = JSONArray()
        try {
            responseData = JSONObject(productDataString)
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
                ProductDetailsScreen(currentProduct = currentProduct, relatedProducts = products)
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewProductDetailsScreen() {
    val dummyCurrentProduct = Product(
        additionalInfo = "Comfortable and stylish and a veyr tremendous title",
        articleType = "Jeans",
        brand = "Brand super duper big brand master blaster A",
        category = "Bottomwear",
        gender = "Men",
        index = 1,
        landingPageUrl = "dresses/tokyo+talkies/tokyo-talkies-abstract-printed-sheath-mini-dress/21627304/buy",
        masterCategory = "Apparel",
        price = 1999,
        primaryColour = "Blue",
        product = "Denim Jeans",
        productId = 101,
        productName = "Blue Denim Jeans",
        rating = 4.5,
        ratingCount = 150,
        searchImage = "https://example.com/image1.jpg",
        sizes = "M,L,XL",
        subCategory = "Casual Wear"
    )

    val dummyRelatedProducts = listOf(
        Product(
            additionalInfo = "Elegant and comfortable and a hugely gigantic big piece of text",
            articleType = "Shirt",
            brand = "Brand B sbse bada brand duniya ka brand",
            category = "Topwear",
            gender = "Women",
            index = 2,
            landingPageUrl = "dresses/sera/sera-black-bodycon-mini-dress/16404534/buy",
            masterCategory = "Apparel",
            price = 1299,
            primaryColour = "Red",
            product = "Cotton Shirt",
            productId = 102,
            productName = "Red Cotton Shirt",
            rating = 4.0,
            ratingCount = 200,
            searchImage = "https://example.com/image2.jpg",
            sizes = "S,M,L",
            subCategory = "Formal Wear"
        ),
        Product(
            additionalInfo = "Sporty and durable and super elastic and next level shit",
            articleType = "Sneakers",
            brand = "Brand C second sbse bada duniya ka bhot achha brand",
            category = "Footwear",
            gender = "Unisex",
            index = 3,
            landingPageUrl = "shoes/nike/nike-air-max-270/12345678/buy",
            masterCategory = "Footwear",
            price = 7999,
            primaryColour = "Black",
            product = "Running Shoes",
            productId = 103,
            productName = "Nike Air Max 270",
            rating = 4.8,
            ratingCount = 300,
            searchImage = "https://example.com/image3.jpg",
            sizes = "8,9,10",
            subCategory = "Sports Wear"
        )
    )

    ProductDetailsScreen(currentProduct = dummyCurrentProduct, relatedProducts = dummyRelatedProducts)
}

@Composable
fun ProductDetailsScreen(currentProduct: Product, relatedProducts: List<Product>) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()  // Occupy full width but not full height
                .wrapContentHeight(),  // Limit height to the SearchBar's height
            contentAlignment = Alignment.Center
        ) {
            SearchBar()  // Call SearchBar here
        }
        // Display current product
        ProductItemView(product = currentProduct)

        Spacer(modifier = Modifier.height(16.dp))

        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        val availableHeight = configuration.screenHeightDp.dp - 200.dp
        LazyRow(
            contentPadding = PaddingValues(horizontal = 8.dp),  // Add padding to the row
            horizontalArrangement = Arrangement.spacedBy(8.dp)  // Spacing between items
        ) {
            items(relatedProducts) { product ->
                ProductItemBriefView(
                    product = product,
                    textScale = 0.6f,
                    modifier = Modifier
                        .width(screenWidth / 2.5f)  // Responsive width, adjust based on screen size
                        .wrapContentHeight()  // Let the height wrap to the content naturally
                )
            }
        }
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
                .aspectRatio(1.0f) // Maintain aspect ratio
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(horizontal = 10.dp),
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
                        Text(text = "${"%.2f".format(product.rating)} ★", color = Color.Gray, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(14.dp))
                    }
                    Text(text = "Rs ${product.price}", color = Color.Gray, fontSize = 12.sp)
                }
                product.brand?.let { Text(text = it, color = Color.Gray, fontSize = 16.sp) }
                Text(text = "${product.additionalInfo}", color = Color.Gray, fontSize = 12.sp)
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
