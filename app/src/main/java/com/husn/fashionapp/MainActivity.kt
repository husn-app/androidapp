package com.husn.fashionapp

//import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fashionapp.R
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.husn.fashionapp.ui.theme.AppTheme
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.URLEncoder


class MainActivity : ComponentActivity(){
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var signInHelper: SignInHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true)
        firebaseAnalytics = Firebase.analytics

        val signInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            signInHelper.handleSignInResult(result.data)
        }

        signInHelper = SignInHelper(this, signInLauncher, this)

        setContent {
            AppTheme {
                CompositionLocalProvider(LocalSignInHelper provides signInHelper) {
                    StyleSenseApp(context = this)
                }
            }
        }
    }
}

val client = OkHttpClient()
fun sendSearchQuery(context: Context, query: String) {
    val baseUrl = context.getString(R.string.husn_base_url)
    val url = "$baseUrl/api/query?query=${URLEncoder.encode(query, "UTF-8")}"
    var sessionCookie = getSessionCookieFromStorage(context) ?: ""
    // Build the POST request
    val request = Request.Builder()
        .url(url)
        .addHeader("platform", "android")
        .addHeader("Cookie", sessionCookie)
        .get()
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
                val session = response.header("Set-Cookie")
                println("sendSearchQuery response_header:$session")
                saveSessionCookie(session, context)

                val responseData = response.body?.string()
                responseData?.let {
                    // Start a new activity with the search result data
                    val intent = Intent(context, SearchResultsActivity::class.java)
                    intent.putExtra("query", query)
                    intent.putExtra("responseData", it)
                    context.startActivity(intent)
                }
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
fun StyleSenseApp(context: Context) {
    Column(
        modifier = Modifier
            .fillMaxSize()
//            .padding(top = 0.dp)
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