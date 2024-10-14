package com.husn.fashionapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.husn.fashionapp.ui.theme.AppTheme
import org.json.JSONArray
import org.json.JSONObject

class WishlistActivity : ComponentActivity() {
//    private lateinit var signInHelper: SignInHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val signInLauncher = registerForActivityResult(
//            ActivityResultContracts.StartActivityForResult()
//        ) { result ->
//            signInHelper.handleSignInResult(result.data)
//        }
//
//        signInHelper = SignInHelper(this, signInLauncher, this)


        // Retrieve the search query and response data from the intent
//        val query = intent.getStringExtra("query") ?: ""
        val responseDataString = intent.getStringExtra("responseData") ?: ""

        // Parse the response data
        val responseData = try {
            JSONObject(sanitizeJson(responseDataString))
        } catch (e: Exception) {

            //println("Error parsing response data: ${e.message}")
            onBackPressedDispatcher.onBackPressed()
            null

        }
        val productsJsonArray = responseData?.getJSONArray("products") ?: JSONArray()
        val products = mutableListOf<Product>()
        for (i in 0 until productsJsonArray.length()) {
            products.add(Product(productsJsonArray.getJSONObject(i)))
        }

        setContent {
            AppTheme {
//                CompositionLocalProvider(LocalSignInHelper provides signInHelper) {
                WishlistScreen(products = products)
//                }
            }
        }
    }

    // Helper function to sanitize the input JSON string
    private fun sanitizeJson(jsonString: String): String {
        return jsonString.replace("NaN", "0")
    }
}

@Composable
fun WishlistScreen(products: List<Product>){
    val context = LocalContext.current
    LazyColumn(
        modifier = Modifier.fillMaxSize()//.padding(top = 8.dp)
    ) {
        item {
            TopNavBar()
        }
        if(AuthManager.isUserSignedIn) {
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
                            .weight(1f) // Ensure the first product takes up half the space
//                        .padding(end = 8.dp) // Add spacing between the two products
//                            .padding(start = 8.dp)
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
        else{
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }
}