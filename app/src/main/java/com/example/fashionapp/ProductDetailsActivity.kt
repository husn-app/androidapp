package com.example.fashionapp
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import org.json.JSONObject
import androidx.compose.ui.layout.ContentScale
import java.io.InputStream


class ProductDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve the product data from the intent
        val productDataString = intent.getStringExtra("productData") ?: ""

        // Parse the response data
        val responseData = JSONObject(productDataString)
        val currentProductJson = responseData.getJSONObject("current_product")
        val productsJsonArray = responseData.getJSONArray("products")

        // Convert current product and products JSON array to Product objects
        val currentProduct = Product(
            additionalInfo = currentProductJson.optString("additionalInfo"),
            articleType = currentProductJson.optString("articleType"),
            brand = currentProductJson.getString("brand"),
            category = currentProductJson.optString("category"),
            gender = currentProductJson.optString("gender"),
            index = currentProductJson.getInt("index"),
            landingPageUrl = currentProductJson.getString("landingPageUrl"),
            masterCategory = currentProductJson.optString("masterCategory"),
            price = currentProductJson.getInt("price"),
            primaryColour = currentProductJson.optString("primaryColour"),
            product = currentProductJson.getString("product"),
            productId = currentProductJson.optInt("productId"),
            productName = currentProductJson.getString("productName"),
            rating = currentProductJson.getDouble("rating").toFloat(),
            ratingCount = currentProductJson.optInt("ratingCount"),
            searchImage = currentProductJson.getString("searchImage"),
            sizes = currentProductJson.optString("sizes"),
            subCategory = currentProductJson.optString("subCategory")
        )

        val products = mutableListOf<Product>()
        for (i in 1 until productsJsonArray.length()) {  // Start loop from 1 to skip the first product
            val productJson = productsJsonArray.getJSONObject(i)
            val product = Product(
                additionalInfo = productJson.optString("additionalInfo"),
                articleType = productJson.optString("articleType"),
                brand = productJson.getString("brand"),
                category = productJson.optString("category"),
                gender = productJson.optString("gender"),
                index = productJson.getInt("index"),
                landingPageUrl = productJson.getString("landingPageUrl"),
                masterCategory = productJson.optString("masterCategory"),
                price = productJson.getInt("price"),
                primaryColour = productJson.optString("primaryColour"),
                product = productJson.getString("product"),
                productId = productJson.optInt("productId"),
                productName = productJson.getString("productName"),
                rating = productJson.getDouble("rating").toFloat(),
                ratingCount = productJson.optInt("ratingCount"),
                searchImage = productJson.getString("searchImage"),
                sizes = productJson.optString("sizes"),
                subCategory = productJson.optString("subCategory")
            )
            products.add(product)
        }

        setContent {
            MaterialTheme {
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
        rating = 4.5f,
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
            rating = 4.0f,
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
            rating = 4.8f,
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
        // Search Bar
        var searchQuery by remember { mutableStateOf("") }
        val context = LocalContext.current
        Box(
            modifier = Modifier
//                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search...") },
                modifier = Modifier
                    .fillMaxWidth(1f),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        sendSearchQuery(context, searchQuery)
                    }
                )
            )
        }

        // Display current product
        ProductItemView(product = currentProduct)

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow {
            items(relatedProducts) { product ->
                // Pass a custom modifier to ensure height wraps content
                ProductItemBriefView(
                    product = product,
                    textScale = 0.6f,
                    modifier = Modifier
                        .width(200.dp)  // Set the width as needed
                        .wrapContentHeight()  // Ensure height adjusts to content
//                        .padding(8.dp)
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
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    Text(text = "Rs ${product.price}", color = Color.Gray, fontSize = 16.sp)
                }
                Text(text = product.brand, color = Color.Gray, fontSize = 18.sp)
                Text(text = "${product.additionalInfo}", color = Color.Gray, fontSize = 14.sp)
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
