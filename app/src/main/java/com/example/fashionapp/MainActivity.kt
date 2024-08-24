package com.example.fashionapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fashionapp.ui.theme.FashionAppTheme
import coil.compose.rememberAsyncImagePainter



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FashionAppTheme {
                StyleSenseApp()
            }
        }
    }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyleSenseApp() {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "StyleSense",
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Magenta
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("denim jeans with knee pockets") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // Product List
            LazyColumn {
                items(getDemoProducts().chunked(2)) { productPair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp),
                        horizontalArrangement = Arrangement.spacedBy(0.dp),
                    ) {
                        productPair.forEach { product ->
                            ProductItem(product, Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

data class Product(
    val index: Int,
    val brand: String,
    val productName: String,
    val landingPageUrl: String,
    val price: Int,
    val rating: Float,
    val thumbnailUrl : String,
)

@Composable
fun ProductItem(product: Product, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(8.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(product.thumbnailUrl),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = product.productName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = "Brand: ${product.brand}", color = Color.Gray, fontSize = 14.sp)
        Text(text = "$${product.price}", color = Color.Gray, fontSize = 14.sp)
        Text(text = "Rating: ${product.rating}", color = Color.Gray, fontSize = 14.sp)
    }
}

fun getDemoProducts(): List<Product> {
    return listOf(
        Product(index=1, brand="Ethnovog", productName="My First Product", landingPageUrl="", price=2000, rating=3.4f, thumbnailUrl="https://assets.myntassets.com/assets/images/17004950/2022/2/1/5e4aedfc-2a28-4246-ba75-2f5c6b8edd331643714188939EavanWomenBurgundySelfDesign1.jpg"),
        Product(index=2, brand="Unknown", productName="Leather Jacket", landingPageUrl="", price=8999, rating=0.0f, thumbnailUrl="https://assets.myntassets.com/assets/images/17004950/2022/2/1/5e4aedfc-2a28-4246-ba75-2f5c6b8edd331643714188939EavanWomenBurgundySelfDesign1.jpg"),
        Product(index=3, brand="Unknown", productName="Sneakers", landingPageUrl="", price=6999, rating=0.0f, thumbnailUrl="https://assets.myntassets.com/assets/images/17004950/2022/2/1/5e4aedfc-2a28-4246-ba75-2f5c6b8edd331643714188939EavanWomenBurgundySelfDesign1.jpg"),
        Product(index=4, brand="Unknown", productName="T-Shirt", landingPageUrl="", price=1999, rating=0.0f, thumbnailUrl="https://assets.myntassets.com/assets/images/17004950/2022/2/1/5e4aedfc-2a28-4246-ba75-2f5c6b8edd331643714188939EavanWomenBurgundySelfDesign1.jpg"),
        Product(index=5, brand="Unknown", productName="Sunglasses", landingPageUrl="", price=2999, rating=0.0f, thumbnailUrl="https://assets.myntassets.com/assets/images/17004950/2022/2/1/5e4aedfc-2a28-4246-ba75-2f5c6b8edd331643714188939EavanWomenBurgundySelfDesign1.jpg")
    )
}