package com.husn.fashionapp

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
import androidx.compose.runtime.mutableStateOf
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
import coil.compose.AsyncImage
import com.example.fashionapp.R
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
    private val inspirationsState = mutableStateOf<Map<String, List<Pair<String, List<InspirationProduct>>>>>(emptyMap())
    private val genderState = mutableStateOf<String>("WOMAN")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val signInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            signInHelper.handleSignInResult(result.data) {
            }
        }
        signInHelper = SignInHelper(this, signInLauncher, this)

        setContent {
            AppTheme {
                CompositionLocalProvider(LocalSignInHelper provides signInHelper) {
                    InspirationScreen(
                        inspirationsMap = inspirationsState.value, gender = genderState.value,
                        onGenderChange = { newGender -> genderState.value = newGender }
                    )
                }
            }
        }
        fetchInspirationData()
    }

    private fun fetchInspirationData() {
        val baseUrl = getString(R.string.husn_base_url)
        val url = "$baseUrl/inspirations_android"
        val request = get_url_request(this, url)

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
//                runOnUiThread {
//                    isRefreshing.value = false
//                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val session = response.header("Set-Cookie")
                    saveSessionCookie(session, this@InspirationsActivity)

                    val responseDataString = response.body?.string()
                    responseDataString?.let {
                        val responseData = try {
                            JSONObject(sanitizeJson(it))
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        }
                        val inspirationsJson = responseData?.getJSONObject("inspirations")
                        val gender = responseData?.getString("gender")
                        val inspirationsMap = mutableMapOf<String, List<Pair<String, List<InspirationProduct>>>>()
                        if (inspirationsJson != null) {
                            for (genderKey in listOf("MAN", "WOMAN")) {
                                val categoryArray = inspirationsJson.getJSONArray(genderKey)
                                val inspirationsList = mutableListOf<Pair<String, List<InspirationProduct>>>()
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
                                inspirationsMap[genderKey] = inspirationsList
                            }
                        }
                        // Update the inspirations state on the main thread
                        runOnUiThread {
                            inspirationsState.value = inspirationsMap
                            gender?.let {
                                genderState.value = gender
                            }
                        }
                    }
                } else {
                    println("Inspiration response failed with status: ${response.code}")
//                    runOnUiThread {
//                        isRefreshing.value = false
//                    }
                }
            }
        })
    }

    private fun sanitizeJson(jsonString: String): String {
        return jsonString.replace("NaN", "0")
    }
}

@Composable
fun InspirationScreen(
    inspirationsMap: Map<String, List<Pair<String, List<InspirationProduct>>>>,
    gender: String,
    onGenderChange: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val signInHelper = LocalSignInHelper.current
    val inspirations = inspirationsMap[gender] ?: emptyList()
    val oppositeGender = if (gender == "MAN") "WOMAN" else "MAN"
    val formatted_gender = if(gender == "MAN") "Men" else "Women"
    val formatted_opp_gender = if(oppositeGender == "MAN") "Men" else "Women"

    Scaffold(
        topBar = { TopNavBar() },
        backgroundColor = Color.Transparent,
        bottomBar = { BottomBar(context = context) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (!AuthManager.isUserSignedIn) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("This page has $formatted_gender's inspirations.")
                    Row {
                        Text(
                            text = "Login",
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable {
                                signInHelper?.signIn()
                            }
                        )
                        Text(" to see personalized feed. ")
                    }
                    Text(
                        text = "$formatted_opp_gender's inspirations.",
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable {
                            onGenderChange(oppositeGender)
                        }
                    )
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(inspirations) { (categoryName, productsList) ->
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
                            style = MaterialTheme.typography.headlineSmall,
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
                                                sendSearchQuery(context, product.inspiration_subcategory_query)
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
}
