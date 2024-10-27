package com.husn.fashionapp


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.analytics.FirebaseAnalytics
import com.husn.fashionapp.ui.theme.AppTheme
import org.json.JSONObject


class SearchResultsActivity : ComponentActivity() {
    private lateinit var signInHelper: SignInHelper
    private val fetch_utility = Fetchutilities(this)
    private val productsState = mutableStateOf<List<Product>>(emptyList())
    private val isLoading = mutableStateOf<Boolean>(true)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //WindowCompat.setDecorFitsSystemWindows(window, false)

        val signInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            signInHelper.handleSignInResult(result.data)
        }

        signInHelper = SignInHelper(this, signInLauncher, this)

        val query = intent.getStringExtra("query") ?: ""
        val referrer = intent.getStringExtra("referrer") ?: ""
        setContent {
            AppTheme {
                CompositionLocalProvider(LocalSignInHelper provides signInHelper) {
                    Crossfade(targetState = isLoading.value) { loading ->
                        if (loading) {
                            WishlistLoadingScreen(showSearchBar = true, query = query)
                        } else {
                            SearchResultsScreen(query = query, products = productsState.value, referrer = "search/query=$query")
                        }
                    }
                }
            }
        }
        fetch_utility.fetchProductsList(relative_url = "/api/query", referrer=referrer, requestBodyJson = JSONObject().apply {
            put("query", query)
        })
        { products ->
            runOnUiThread {
                productsState.value = products ?: emptyList()
                isLoading.value = false
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewSearchResultsScreen() {
    AppTheme {
        CompositionLocalProvider(
            LocalSignInHelper provides null, // Provide null or a mock SignInHelper if needed
            LocalContext provides LocalContext.current
        ) {
            SearchResultsScreen(query = "Sample Query", products = getDummyProductsList())
        }
    }
}

@Composable
fun SearchResultsScreen(query: String, products: List<Product>, currentProduct: Product? = null, referrer: String = "", MainProductView: @Composable ((product: Product) -> Unit)? = null) {
    Scaffold(
        backgroundColor = MaterialTheme.colorScheme.background,
        bottomBar = { BottomBar() } // BottomBar placed correctly
    ) { innerPadding -> // Use innerPadding to avoid content overlapping the BottomBar
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            item {
                TopNavBar()
                SearchBar(query = query, referrer = referrer)
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
                    ProductItemBriefView(
                        product = productPair[0],
                        modifier = Modifier
                            .weight(1f),
                        referrer = "$referrer/rank=${2*index}"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Display the second product in the row if available
                    if (productPair.size > 1) {
                        ProductItemBriefView(
                            product = productPair[1],
                            modifier = Modifier
                                .weight(1f), // Ensure the second product takes up half the space
//                            .padding(end = 8.dp) // Add spacing between the two products
                            referrer = "$referrer/rank=${2*index + 1}"
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
    textScale: Float = 1f,  // Default scale factor
    referrer: String = ""
) {
    val context = LocalContext.current
    val firebaseAnalytics = remember {
        try {
            FirebaseAnalytics.getInstance(context)
        } catch (e: Exception) {
            null
        }
    }
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        ImageFromUrl(product.primaryImage, clickable = {
            val bundle = Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_ID, product.index.toString())
                putString(FirebaseAnalytics.Param.ITEM_NAME, product.brand)
                putString(FirebaseAnalytics.Param.CONTENT_TYPE, "product")
            }
            firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle)

            val intent = Intent(context, ProductDetailsActivity::class.java).apply {
                putExtra("product_index", product.index)
                putExtra("referrer", referrer)
            }
            context.startActivity(intent)
        })
        Spacer(modifier = Modifier.height(4.dp))
        Column(horizontalAlignment = Alignment.Start, modifier = Modifier.padding(start = 4.dp)) {
            Text(
                text = product.brand,
                color = MaterialTheme.colorScheme.primary,
//                color = Color.Black,
                fontSize = (14.sp * textScale),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontFamily = FontFamily.SansSerif
            )
            Text(
                text = product.productName.replace(product.brand, "").trimStart(),
                color = MaterialTheme.colorScheme.secondary,
                fontSize = (10.sp * textScale),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}