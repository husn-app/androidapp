package com.husn.fashionapp


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.analytics.FirebaseAnalytics
import com.husn.fashionapp.ui.theme.AppTheme
import org.json.JSONObject


class SearchResultsActivity : ComponentActivity() {
    private lateinit var signInHelper: SignInHelper
    private val fetch_utility = Fetchutilities(this)
    private val productsState = mutableStateOf<List<Product>>(emptyList())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val signInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            signInHelper.handleSignInResult(result.data)
        }

        signInHelper = SignInHelper(this, signInLauncher, this)

        val query = intent.getStringExtra("query") ?: ""
        setContent {
            AppTheme {
                CompositionLocalProvider(LocalSignInHelper provides signInHelper) {
                    SearchResultsScreen(query = query, products = productsState.value)
                }
            }
        }
        fetch_utility.fetchProductsList(relative_url = "/api/query", requestBodyJson = JSONObject().apply {
            put("query", query)
        })
        { products ->
            runOnUiThread {
                productsState.value = products ?: emptyList()
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewSearchResultsScreen() {
    SearchResultsScreen(query = "Sample Query", products = getDummyProductsList())
}

@Composable
fun SearchResultsScreen(query: String, products: List<Product>, currentProduct: Product? = null, MainProductView: @Composable ((product: Product) -> Unit)? = null, searchBarFraction: Float = 0.96f) {
    val context: Context = LocalContext.current
    Scaffold(
//        topBar = { TopNavBar() },
        backgroundColor = Color.Transparent,
        bottomBar = { BottomBar(context = context) } // BottomBar placed correctly
    ) { innerPadding -> // Use innerPadding to avoid content overlapping the BottomBar

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            item {
                TopNavBar()
            }
            item {
                SearchBar(query = query, searchBarFraction = searchBarFraction)
                Spacer(modifier = Modifier.height(8.dp)) // Optional: add spacing after the search bar
            }
            if (MainProductView != null && currentProduct != null) {
                item {
                    MainProductView(currentProduct)
                }
            }

            itemsIndexed(products.chunked(2)) { index, productPair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Display the first product in the row
                    ProductItemBriefView(/**/
                        product = productPair[0],
                        modifier = Modifier
                            .weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Display the second product in the row if available
                    if (productPair.size > 1) {
                        ProductItemBriefView(
                            product = productPair[1],
                            modifier = Modifier
                                .weight(1f) // Ensure the second product takes up half the space
//                            .padding(end = 8.dp) // Add spacing between the two products
                        )
                    } else {
                        // Add an empty Box to take up the second half of the row
                        Box(
                            modifier = Modifier
                                .weight(1f)
//                            .padding(end = 8.dp) // Add spacing between the two products
                        )
                    }
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
    val firebaseAnalytics = remember { FirebaseAnalytics.getInstance(context) }
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Image(
            painter = rememberAsyncImagePainter(product.primaryImage),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.75f) // Maintain aspect ratio
                .clip(RoundedCornerShape(16.dp))
                .clickable {
                    val bundle = Bundle().apply {
                        putString(FirebaseAnalytics.Param.ITEM_ID, product.index.toString())
                        putString(FirebaseAnalytics.Param.ITEM_NAME, product.brand)
                        putString(FirebaseAnalytics.Param.CONTENT_TYPE, "product")
                    }
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)

                    val intent = Intent(context, ProductDetailsActivity::class.java).apply {
                        putExtra("product_index", product.index)
                    }
                    context.startActivity(intent)
                }
        )
        Spacer(modifier = Modifier.height(4.dp))
        Column(horizontalAlignment = Alignment.Start, modifier = Modifier.padding(start = 4.dp)) {
            Text(
                text = product.brand,
                color = MaterialTheme.colorScheme.primary,
                fontSize = (12.sp * textScale),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = product.productName.replace(product.brand, ""),
                color = MaterialTheme.colorScheme.primary,
                fontSize = (8.sp * textScale),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}