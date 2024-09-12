package com.example.fashionapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import okio.IOException
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
        val context = LocalContext.current
//        SearchBar(searchQuery = searchText.text, context = context, placeholder = {Text(searchText.text)})
        Box(
                 modifier = Modifier
 //                    .fillMaxSize()
                     .padding(16.dp),
                 contentAlignment = Alignment.Center
         )
         {
             OutlinedTextField(
                 value = searchText,
                 onValueChange = { searchText = it },
                 placeholder = { Text("$searchText") },
                 modifier = Modifier
                     .fillMaxWidth(0.8f)
                     .padding(16.dp),
                 keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                 keyboardActions = KeyboardActions(
                     onSearch = {
                         sendSearchQuery(context, searchText.text)
                     }
                 )
             )
         }


        // Products list
        LazyColumn {
            items(products) { product ->
                ProductItem(product, Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
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
                    })
            }
        }
    }
}

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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "${product.brand}", color = Color.Gray, fontSize = 18.sp)
            Text(text = "${"%.2f".format(product.rating)} ★", color = Color.Gray, fontSize = 16.sp)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "${product.additionalInfo}", color = Color.Gray, fontSize = 14.sp)
            Text(text = "Rs ${product.price}", color = Color.Gray, fontSize = 14.sp)
        }
    }
}

