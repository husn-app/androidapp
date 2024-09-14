package com.example.fashionapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.fashionapp.ui.theme.AppTheme
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
    val url = "https://husn.app/api/query"

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
                println(responseData)
            } else {
                println("Request failed with status: ${response.code}")
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

//@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyleSenseApp(context: Context) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        // Display beige-icon.png
        val imageModifier = Modifier
            .size(200.dp)
            .padding(bottom = 16.dp)
        Image(
            painter = rememberAsyncImagePainter(model = "file:///android_asset/beige-icon.png"),
            contentDescription = "Beige Icon",
            modifier = imageModifier
        )
        // Search Bar
        Spacer(modifier = Modifier.height(250.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        )
        //     .padding(16.dp),
        // contentAlignment = Alignment.Center
        {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("denim jeans with knee pockets") },
                modifier = Modifier
                    .fillMaxWidth(0.8f),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        sendSearchQuery(context, searchQuery)
                    }
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth(),
//                    .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val linkModifier = Modifier
                .clickable { sendSearchQuery(context, "Christmas dinner set") }
//                    .padding(8.dp)

            val linkTexts = listOf(
                "Christmas dinner set",
                "Red Corset",
                "Parachute jeans with Pleats",
                "Jumpsuit for diwali"
            )
            val searchQueries = listOf(
                "Christmas dinner set",
                "Sexy Red Corset",
                "Parachute jeans with Pleats",
                "Jumpsuit for diwali"
            )

            linkTexts.forEachIndexed { index, linkText ->
                Text(
                    text = linkText,
                    modifier = Modifier
                        .clickable { sendSearchQuery(context, searchQueries[index]) }
//                .padding(8.dp),
//            color = Color.Blue,
                    , fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun SearchBar(
    searchQuery: String,
    context: Context,
    modifier: Modifier = Modifier,
    placeholder: @Composable () -> Unit = { Text("") }
) {
    var searchText by remember { mutableStateOf(TextFieldValue(searchQuery)) }
    Box(
        modifier = modifier
//             .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = placeholder,
            modifier = Modifier
                .fillMaxWidth(0.8f),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    sendSearchQuery(context, searchText.toString())
                }
            )
        )
    }
}


data class Product(
    val additionalInfo: String?,
    val articleType: String?,
    val brand: String,
    val category: String?,
    val gender: String?,
    val index: Int,
    val landingPageUrl: String,
    val masterCategory: String?,
    val price: Int,
    val primaryColour: String?,
    val product: String,
    val productId: Int?,
    val productName: String,
    val rating: Float,
    val ratingCount: Int?,
    val searchImage: String,
    val sizes: String?,
    val subCategory: String?
)
