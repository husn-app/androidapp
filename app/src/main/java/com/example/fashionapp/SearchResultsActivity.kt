package com.example.fashionapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import org.json.JSONObject

class SearchResultsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve the search query and response data from the intent
        val query = intent.getStringExtra("query") ?: ""
        val responseDataString = intent.getStringExtra("responseData") ?: ""

        // Parse the response data
        val responseData = JSONObject(responseDataString)
        val productsJsonArray = responseData.getJSONArray("products")

        // Convert the JSON array to a list of products
        val products = mutableListOf<Product>()
        for (i in 0 until productsJsonArray.length()) {
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
                SearchResultsScreen(query = query, products = products)
            }
        }
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
            rating = 4.5f,
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
            rating = 4.0f,
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
        // Search Bar
        var searchText by remember { mutableStateOf(TextFieldValue(query)) }
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = searchText.text,
                fontSize = 18.sp,
                modifier = Modifier
//                    .background(Color.LightGray)
                    .padding(8.dp)
            )
        }


        // Products list
        LazyColumn {
            items(products) { product ->
                ProductItem(product, Modifier
                    .fillMaxWidth()
                    .padding(8.dp))
            }
        }
    }
}

// @Composable
// fun ProductItem(product: Product, modifier: Modifier) {
//     Column(
//         modifier = modifier
//             .padding(8.dp)
//             .fillMaxWidth(),
//         horizontalAlignment = Alignment.CenterHorizontally
//     ) {
//         // Replace with actual product UI (e.g., image, name, price)
//         Text(text = product.productName, fontSize = 20.sp)
//         Text(text = "\$${product.price}", fontSize = 16.sp)
//     }
// }

@Composable
fun ProductItem(product: Product, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(8.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(product.searchImage),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.0f) // Maintain aspect ratio
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = product.productName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = "Brand: ${product.brand}", color = Color.Gray, fontSize = 14.sp)
        Text(text = "$${product.price}", color = Color.Gray, fontSize = 14.sp)
        Text(text = "Rating: ${product.rating}", color = Color.Gray, fontSize = 14.sp)
    }
}

