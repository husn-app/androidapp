package com.example.fashionapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import org.json.JSONObject

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
                ProductDetailsScreen(currentProduct = currentProduct, relatedProducts = products)
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewProductDetailsScreen() {
    val dummyCurrentProduct = Product(
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
    )

    val dummyRelatedProducts = listOf(
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
        ),
        Product(
            additionalInfo = "Sporty and durable",
            articleType = "Sneakers",
            brand = "Brand C",
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
        // Display current product
        // Text(text = "Current Product", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        ProductItem(product = currentProduct)

        Spacer(modifier = Modifier.height(16.dp))

        // Display related products
        // Text(text = "Related Products", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        LazyRow {
            items(relatedProducts) { product ->
                ProductItem(product = product, modifier = Modifier.padding(horizontal = 8.dp))
            }
        }
    }
}
