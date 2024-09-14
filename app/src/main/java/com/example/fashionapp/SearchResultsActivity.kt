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
import androidx.compose.foundation.verticalScroll
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
        )
    )
    SearchResultsScreen(query = "Sample Query", products = dummyProducts)
}
@Composable
fun SearchResultsScreen(query: String, products: List<Product>) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()  // Occupy full width but not full height
                .wrapContentHeight(),  // Limit height to the SearchBar's height
            contentAlignment = Alignment.Center
        ) {
            SearchBar(query = query)  // Call SearchBar here
        }

        // Products list
//        LazyColumn {
//            items(products) { product ->
//                ProductItemBriefView(product, Modifier
//                    .fillMaxWidth()
//                    .padding(8.dp)
//                    )
//            }
//        }
        val listState = rememberLazyListState()
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // LazyColumn for product list
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 12.dp) // Adding padding to leave space for scrollbar
            ) {
                items(products) { product ->
                    ProductItemBriefView(
                        product = product,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }

            // Custom vertical scrollbar
//            VerticalScrollbar(
//                modifier = Modifier
//                    .align(Alignment.CenterEnd)
//                    .fillMaxHeight(),
//                scrollState = listState
//            )
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
        modifier = modifier,
//            .clickable {
//                // Optional: Handle entire item click if needed
//            },
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberAsyncImagePainter(product.searchImage),
            contentDescription = null,
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(1.0f) // Maintain aspect ratio
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
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (product.rating > 0) {
                        Text(
                            text = "${"%.2f".format(product.rating)} ★",
                            color = Color.Gray,
                            fontSize = (16.sp * textScale),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = "Rs ${product.price}",
                        color = Color.Gray,
                        fontSize = (16.sp * textScale),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                product.brand?.let {
                    Text(
                        text = it,
                        color = Color.Gray,
                        fontSize = (18.sp * textScale),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = "${product.additionalInfo}",
                    color = Color.Gray,
                    fontSize = (14.sp * textScale),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            // Right-aligned clickable SVG icon that redirects to myntra.com/${product.landingPageUrl}
            DisplaySvgIconFromAssets(
                fileName = "myntra-logo.svg",
                modifier = Modifier
                    .size(24.dp) // Adjusted size for better layout fit
                    .clickable {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://myntra.com/${product.landingPageUrl}")
                        )
                        context.startActivity(intent)
                    }
            )
        }
    }
}

@Composable
fun VerticalScrollbar(
    modifier: Modifier = Modifier,
    scrollState: LazyListState
) {
    val totalItems = scrollState.layoutInfo.totalItemsCount
    val firstVisibleItemIndex = scrollState.firstVisibleItemIndex
    val visibleItemsCount = scrollState.layoutInfo.visibleItemsInfo.size

    val proportion = if (totalItems > 0) {
        visibleItemsCount.toFloat() / totalItems.toFloat()
    } else {
        1f
    }

    // Calculate thumb height and position
    val thumbHeight = max(proportion * 300f, 24f) // Ensuring a minimum thumb size
    val thumbOffsetFraction = if (totalItems > 0) {
        firstVisibleItemIndex.toFloat() / totalItems.toFloat()
    } else {
        0f
    }

    // Multiply thumbOffsetFraction by a base dp value and convert it to Dp
    val thumbOffset = thumbOffsetFraction * 300f // This is in pixels (Float)

    Box(
        modifier = modifier
            .width(8.dp)
            .background(Color.LightGray) // Background for scrollbar track
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(thumbHeight.dp) // Convert Float to Dp using dp
                .offset(y = thumbOffset.dp) // Convert the offset to Dp explicitly
                .background(Color.DarkGray) // Color for the thumb
        )
    }
}
