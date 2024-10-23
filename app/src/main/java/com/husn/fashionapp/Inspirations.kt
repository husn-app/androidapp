package com.husn.fashionapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.fashionapp.R
import com.google.firebase.analytics.FirebaseAnalytics
import com.husn.fashionapp.ui.theme.AppTheme
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import okio.IOException
import org.json.JSONObject

class InspirationsActivity : ComponentActivity() {
    private lateinit var signInHelper: SignInHelper
    private val client = OkHttpClient()
    private val inspirationsState = mutableStateOf<List<Pair<String, List<InspirationProduct>>>>(
        emptyList()
    )
//    private val genderState = mutableStateOf<String>("WOMAN")
    private val fetch_utility = Fetchutilities(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val signInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            signInHelper.handleSignInResult(result.data) {
                fetchInspirationData()
            }
        }
        signInHelper = SignInHelper(this, signInLauncher, this)
        if (!AuthManager.isUserSignedIn) {
            signInHelper.signIn()
//            signInHelper?.signIn(onSignInSuccess = {
//                val intent = Intent(this, WishlistActivity::class.java)
//                startActivity(intent)
//            })
        } else {
            // Fetch data if already signed in
            fetchInspirationData()
        }

        setContent {
            AppTheme {
                CompositionLocalProvider(LocalSignInHelper provides signInHelper) {
                    InspirationScreen(
                        inspirations = inspirationsState.value //, gender = genderState.value,
//                            onGenderChange = { newGender -> genderState.value = newGender }
                    )
                }
            }
        }
    }

    private fun fetchInspirationData() {
        val baseUrl = getString(R.string.husn_base_url)
        var url = "$baseUrl/api/inspiration"
        AuthManager.gender?.let{
            url = "$url/${AuthManager.gender}"
        }
        val request = post_url_request(this, url)

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
//                    val session = response.header("Set-Cookie")
                    val headers = response.headers
                    val cookies = headers.values("Set-Cookie")
                    println("fetchInspirationData success. cookies: $cookies\n response: $response")
                    saveSessionCookie(cookies, this@InspirationsActivity)

                    val responseDataString = response.body?.string()
                    println("fetchInspirationData responseDataString $responseDataString")
                    responseDataString?.let {
                        val responseData = try {
                            JSONObject(fetch_utility.sanitizeJson(it))
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        }
                        val categoryArray = responseData?.getJSONArray("inspirations")
                        println("fetchInspirationData categoryArray: $categoryArray")
//                        val gender = responseData?.getString("gender")
                        val inspirationsList = mutableListOf<Pair<String, List<InspirationProduct>>>()
                        if (categoryArray != null) {
//                            for (genderKey in listOf("MAN", "WOMAN")) {
//                                val categoryArray = inspirationsJson.getJSONArray(genderKey)

                                for (i in 0 until categoryArray.length()) {
                                    val categoryObj = categoryArray.getJSONObject(i)
                                    val categoryName = categoryObj.getString("category")
                                    val productsArray = categoryObj.getJSONArray("products")
                                    val productsList = mutableListOf<InspirationProduct>()
                                    for (j in 0 until productsArray.length()) {
                                        val productObj = productsArray.getJSONObject(j)
                                        productsList.add(InspirationProduct(productObj))
                                    }
                                    inspirationsList.add(Pair(categoryName, productsList))
                                }
//                                inspirationsMap[genderKey] = inspirationsList
//                            }
                        }
                        // Update the inspirations state on the main thread
                        runOnUiThread {
                            inspirationsState.value = inspirationsList
//                            gender?.let {
//                                genderState.value = gender
//                            }
                        }
                    }
                } else {
                    println("Inspiration response failed with status: ${response.code}")
                }
            }
        })
    }
}

@Composable
fun InspirationScreen(
    inspirations: List<Pair<String, List<InspirationProduct>>>,
    gender: String = "",
    onGenderChange: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val signInHelper = LocalSignInHelper.current
//    val inspirations = inspirationsMap[gender] ?: emptyList()
//    val oppositeGender = if (gender == "MAN") "WOMAN" else "MAN"
//    val formattedGender = if(gender == "MAN") "Men" else "Women"
//    val formattedOppGender = if(oppositeGender == "MAN") "Men" else "Women"
    val firebaseAnalytics = remember { FirebaseAnalytics.getInstance(context) }
    val randomizedInspirations = remember(inspirations) {
        inspirations.map { (category, products) ->
            category to products.shuffled()
        }.shuffled()
    }

    Scaffold(
        backgroundColor = Color.Transparent,
        bottomBar = { BottomBar(context = context, selectedItem = 2) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            item {
                TopNavBar()
            }
//            if (!AuthManager.isUserSignedIn) {
//                item {
//                    Column(
//                        modifier = Modifier.padding(16.dp)
//                    ) {
//                        Text("This page has $formattedGender's inspirations.")
//                        Row {
//                            Text(
//                                text = "Login",
//                                textDecoration = TextDecoration.Underline,
//                                modifier = Modifier.clickable {
//                                    signInHelper?.signIn(){
//                                        val intent = Intent(context, InspirationsActivity::class.java)
//                                        context.startActivity(intent)
//                                    }
//                                }
//                            )
//                            Text(" to see personalized feed. ")
//                        }
//                        Text(
//                            text = "$formattedOppGender's inspirations.",
//                            textDecoration = TextDecoration.Underline,
//                            modifier = Modifier.clickable {
//                                onGenderChange(oppositeGender)
//                            }
//                        )
//                    }
//                }
//            }
            items(randomizedInspirations) { (categoryName, productsList) ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = categoryName,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(productsList) { product ->
                            Column(
                                modifier = Modifier
                                    .width(250.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AsyncImage(
                                    model = product.primary_image,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(0.75f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable {
                                            val bundle = Bundle().apply {
                                                putString(FirebaseAnalytics.Param.SEARCH_TERM, product.inspiration_subcategory_query)
                                            }
                                            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle)

                                            val intent = Intent(context, SearchResultsActivity::class.java).apply {
                                                putExtra("query", product.inspiration_subcategory_query)
                                            }
                                            context.startActivity(intent)
                                        },
                                    contentScale = ContentScale.Crop
                                )
                                Text(
                                    text = product.inspiration_subcategory_name,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
