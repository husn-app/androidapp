package com.example.fashionapp

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.fashionapp.ui.theme.AppTheme
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import org.json.JSONArray
import org.json.JSONObject
//import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import kotlin.math.max

//import androidx.compose.foundation.rememberScrollbarAdapter

class SearchResultsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve the search query and response data from the intent
        val query = intent.getStringExtra("query") ?: ""
        val responseDataString = intent.getStringExtra("responseData") ?: ""

        // Parse the response data
        val responseData = try {
            JSONObject(sanitizeJson(responseDataString))
        } catch (e: Exception) {

             println("Error parsing response data: ${e.message}")
             onBackPressedDispatcher.onBackPressed()
            null

        }
        val productsJsonArray = responseData?.getJSONArray("products") ?: JSONArray()
        println("parsed productsJsonArray $productsJsonArray")
        // Convert the JSON array to a list of products
        val products = mutableListOf<Product>()
        for (i in 0 until productsJsonArray.length()) {
            val productJson = productsJsonArray.getJSONObject(i)
            val product = Product(productJson)
            products.add(product)
        }

        setContent {
            AppTheme {
                SearchResultsScreen(query = query, products = products)
            }
        }
    }

    // Helper function to sanitize the input JSON string
    private fun sanitizeJson(jsonString: String): String {
        // Implement sanitization logic here (e.g., remove or replace NaN)
        return jsonString.replace("NaN", "0") // Replace NaN with default numeric value (e.g., 0)
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewSearchResultsScreen() {
    val dummyProducts = listOf(
        Product(
            additionalInfo = "Comfortable and stylish",
            articleType = "Jeans",
            brand = "Brand A",
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
        ),
        Product(
            additionalInfo = "Elegant and comfortable",
            articleType = "Shirt",
            brand = "Brand B",
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
            additionalInfo = "Elegant and comfortable",
            articleType = "Shirt",
            brand = "Brand B",
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
        )
    )
    SearchResultsScreen(query = "Sample Query", products = dummyProducts)
}
@Composable
fun SearchResultsScreen(query: String, products: List<Product>, currentProduct: Product? = null, ProductItemView: @Composable ((product: Product) -> Unit)? = null) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(top = 8.dp)
    ) {
//        HusnLogo()
////        Box(
////            modifier = Modifier
////                .fillMaxWidth()  // Occupy full width but not full height
////                .wrapContentHeight()  // Limit height to the SearchBar's height
////            .padding(end = 12.dp), // Adding padding to leave space for scrollbar
////            contentAlignment = Alignment.Center
////        ) {
//        SearchBar(query = query)  // Call SearchBar here
////        SearchBar()
////        }
//
//        Spacer(modifier = Modifier.height(12.dp))
//        // Products list
//        ProductsListView(products)
        item {
            HusnLogo()
        }

        // Display the search bar
        item {
            SearchBar(query = query)
            Spacer(modifier = Modifier.height(8.dp)) // Optional: add spacing after the search bar
        }
        if(ProductItemView != null && currentProduct != null){
            item{
                ProductItemView(currentProduct)
            }
        }

        itemsIndexed(products.chunked(2)) { index, productPair ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                // Display the first product in the row
                ProductItemBriefView(
                    product = productPair[0],
                    modifier = Modifier
                        .weight(1f) // Ensure the first product takes up half the space
                        .padding(end = 8.dp) // Add spacing between the two products
//                            .padding(start = 8.dp)
                )

                // Display the second product in the row if available
                if (productPair.size > 1) {
                    ProductItemBriefView(
                        product = productPair[1],
                        modifier = Modifier
                            .weight(1f) // Ensure the second product takes up half the space
                            .padding(end = 8.dp) // Add spacing between the two products
                    )
                } else {
                    // Add an empty Box to take up the second half of the row
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp) // Add spacing between the two products
                    )
                }
            }
        }
    }
}

@Composable
fun ProductsListView(products: List<Product>){
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Group items into pairs and handle them in rows
        itemsIndexed(products.chunked(2)) { index, productPair ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                // Display the first product in the row
                ProductItemBriefView(
                    product = productPair[0],
                    modifier = Modifier
                        .weight(1f) // Ensure the first product takes up half the space
                        .padding(end = 8.dp) // Add spacing between the two products
//                            .padding(start = 8.dp)
                )

                // Display the second product in the row if available
                if (productPair.size > 1) {
                    ProductItemBriefView(
                        product = productPair[1],
                        modifier = Modifier
                            .weight(1f) // Ensure the second product takes up half the space
                            .padding(end = 8.dp) // Add spacing between the two products
                    )
                } else {
                    // Add an empty Box to take up the second half of the row
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp) // Add spacing between the two products
                    )
                }
            }
        }
    }
}

@Composable
fun ProductItemBriefView(
    product: Product,
    modifier: Modifier = Modifier,
    textScale: Float = 1f  // Default scale factor
) {
    val context = LocalContext.current
    Column(
        modifier = modifier.fillMaxWidth()
//        modifier = modifier,
//            .clickable {
//                // Optional: Handle entire item click if needed
//            },
//            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberAsyncImagePainter(product.searchImage),
            contentDescription = null,
            modifier = Modifier
//                .width(256.dp)
                .fillMaxWidth()
                .aspectRatio(0.75f) // Maintain aspect ratio
                .clip(RoundedCornerShape(16.dp))
                .clickable {
                    val url = "https://husn.app/api/product/${product.index}"
                    val request = Request.Builder()
                        .url(url)
                        .build()

                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            e.printStackTrace()
                        }

                        override fun onResponse(call: Call, response: Response) {
                            if (response.isSuccessful) {
                                val responseData = response.body?.string()
                                responseData?.let {
                                    val intent = Intent(context, ProductDetailsActivity::class.java)
                                    intent.putExtra("productData", it)
                                    context.startActivity(intent)
                                }
                            } else {
                                println("Request failed with status: ${response.code}")
                            }
                        }
                    })
                }
//                contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(4.dp))
//        Row(
//            modifier = Modifier
//                .fillMaxWidth(0.8f)
//                .padding(horizontal = 1.dp),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Column(
//                modifier = Modifier.weight(1f),
//                horizontalAlignment = Alignment.Start
//            ) {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    verticalAlignment = Alignment.CenterVertically,
//                ) {
//                    if (product.rating > 0) {
//                        Text(
//                            text = "${"%.2f".format(product.rating)} ★",
//                            color = Color.Gray,
//                            fontSize = (6.sp * textScale),
//                            maxLines = 1,
//                            overflow = TextOverflow.Ellipsis
//                        )
//                        Spacer(modifier = Modifier.width(8.dp))
//                    }
//                    Text(
//                        text = "Rs ${product.price}",
//                        color = Color.Gray,
//                        fontSize = (6.sp * textScale),
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis
//                    )
//                }
                Column(horizontalAlignment = Alignment.Start) {
                    product.brand?.let {
                        Text(
                            text = it,
                            color = Color.Gray,
                            fontSize = (12.sp * textScale),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = "${product.additionalInfo}",
                        color = Color.Gray,
                        fontSize = (8.sp * textScale),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
//            }
            // Right-aligned clickable SVG icon that redirects to myntra.com/${product.landingPageUrl}
//            DisplaySvgIconFromAssets(
//                fileName = "myntra-logo.svg",
//                modifier = Modifier
//                    .size(24.dp) // Adjusted size for better layout fit
//                    .clickable {
//                        val intent = Intent(
//                            Intent.ACTION_VIEW,
//                            Uri.parse("https://myntra.com/${product.landingPageUrl}")
//                        )
//                        context.startActivity(intent)
//                    }
//            )
//        }
    }
}