package com.husn.fashionapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.husn.fashionapp.ui.theme.AppTheme
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                StyleSenseApp(this)
            }
        }
    }
}

val client = OkHttpClient()
fun sendSearchQuery(context: Context, query: String) {
//    val baseUrl = context.getString(R.string.husn_base_url)
    val url = "https://husn.app//api/query"

    // Create a JSON object to hold the request body
    val jsonObject = JSONObject()
    jsonObject.put("query", query)

    // Create the request body with JSON media type
    val requestBody = jsonObject.toString()
        .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

    // Build the POST request
    val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .build()

    // Execute the request asynchronously
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            // Handle failure
            e.printStackTrace()
        }

        override fun onResponse(call: Call, response: Response) {
            // Handle success
            if (response.isSuccessful) {
                val responseData = response.body?.string()
                // Do something with the response data
                responseData?.let {
                    // Start a new activity with the search result data
                    val intent = Intent(context, SearchResultsActivity::class.java)
                    intent.putExtra("query", query)
                    intent.putExtra("responseData", it)
                    context.startActivity(intent)
                }
//                ////println(responseData)
            } else {
                //println("Request failed with status: ${response.code}")
            }
        }
    })
}

@Preview(showBackground = true)
@Composable
fun PreviewStyleSenseApp() {
    val mockContext = LocalContext.current  // Use LocalContext for mock context in Preview
    StyleSenseApp(context = mockContext)
}

@Composable
fun HusnLogo(modifier: Modifier = Modifier){
    // Display beige-icon.png
    Row(
//        modifier = Modifier.padding(8.dp) // Adds padding around the Row
    ) {
        val context = LocalContext.current
        Text(
            text = "Husn",
            fontSize = 24.sp, // Set a large font size
            fontWeight = FontWeight.Bold, // Bold font weight
            modifier = Modifier
                .padding(16.dp) // Padding around the text
                .clickable {
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                },
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun StyleSenseApp(context: Context) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        HusnLogo()

        Spacer(modifier = Modifier.height(250.dp))
        // Search Bar

        SearchBar(context = context)  // Call SearchBar here

        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth(),
//                    .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val linkTexts = listOf(
                "Christmas dinner dress",
                "Red Corset",
                "Parachute jeans with Pleats",
                "Jumpsuit for diwali",
            )
            val searchQueries = listOf(
                "Christmas dinner dress",
                "Sexy Red Corset",
                "Parachute jeans with Pleats",
                "Jumpsuit for diwali",
            )

            linkTexts.forEachIndexed { index, linkText ->
                Text(
                    text = linkText,
                    modifier = Modifier
                        .clickable { sendSearchQuery(context, searchQueries[index]) }
//                .padding(8.dp),
                    , fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String = "",
    context: Context = LocalContext.current,
    modifier: Modifier = Modifier
) {
    var searchText by remember { mutableStateOf(TextFieldValue(query)) }
//    val textColor = MaterialTheme.colorScheme.onSurface // For the text color
    val textColor = Color.Black
    val backgroundColor = Color.White

    Box(
        modifier = modifier
            .fillMaxWidth()  // Occupy full width but not full height
            .wrapContentHeight(),  // Limit height to the SearchBar's height
        contentAlignment = Alignment.Center
    ) {
//        SearchBar()  // Call SearchBar here
//    }
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = {
                if (query.isEmpty()) {
                    Text("Search...", color = textColor)
                } else {
                    Text(query, color = textColor)
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(24.dp)),  // Apply rounded corners
            shape = RoundedCornerShape(24.dp),  // Shape for the OutlinedTextField
//            colors = TextFieldDefaults.outlinedTextFieldColors(
//                containerColor = Color.White,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = backgroundColor,
                unfocusedContainerColor = backgroundColor,
                disabledContainerColor = backgroundColor,
                focusedTextColor = textColor, // Text color when focused
                unfocusedTextColor = textColor
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    sendSearchQuery(context, searchText.text)
                }
            ),
            singleLine = true  // Ensure it's a single-line search bar
        )
    }
}


data class Product(
    val additionalInfo: String? = "",
    val articleType: String? = "",
    val brand: String? = "",
    val category: String? = "",
    val gender: String? = "",
    val index: Int? = 0,
    val landingPageUrl: String? = "",
    val masterCategory: String? = "",
    val price: Int? = 0,
    val primaryColour: String? = "",
    val product: String? = "",
    val productId: Int? = 0,
    val productName: String? = "",
    val rating: Double = 0.0,
    val ratingCount: Int? = 0,
    val searchImage: String? = "",
    val sizes: String? = "",
    val subCategory: String? = ""
) {
    constructor(json: JSONObject) : this(
        additionalInfo = json.optString("additionalInfo", ""),
        articleType = json.optString("articleType", ""),
        brand = json.optString("brand", ""),
        category = json.optString("category", ""),
        gender = json.optString("gender", ""),
        index = json.optInt("index", 0),
        landingPageUrl = json.optString("landingPageUrl", ""),
        masterCategory = json.optString("masterCategory", ""),
        price = json.optInt("price", 0),
        primaryColour = json.optString("primaryColour", ""),
        product = json.optString("product", ""),
        productId = json.optInt("productId", 0),
        productName = json.optString("productName", ""),
        rating = json.optDouble("rating", 0.0),
        ratingCount = json.optInt("ratingCount", 0),
        searchImage = json.optString("searchImage", ""),
        sizes = json.optString("sizes", ""),
        subCategory = json.optString("subCategory", "")
    )
}
