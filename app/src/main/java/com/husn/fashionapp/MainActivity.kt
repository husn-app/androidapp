package com.husn.fashionapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.husn.fashionapp.ui.theme.AppTheme


class MainActivity : ComponentActivity(){
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var signInHelper: SignInHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //WindowCompat.setDecorFitsSystemWindows(window, false)

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

@Preview(showBackground = true)
@Composable
fun PreviewStyleSenseApp() {
    val mockContext = LocalContext.current  // Use LocalContext for mock context in Preview
    StyleSenseApp(context = mockContext)
}

@Composable
fun StyleSenseApp(context: Context) {
    Scaffold(
        topBar = { TopNavBar() },
        backgroundColor = MaterialTheme.colorScheme.background,
        bottomBar = { BottomBar() } // BottomBar placed correctly
    ) { innerPadding -> // Use innerPadding to avoid content overlapping the BottomBar

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Apply padding here
        ) {
            Spacer(modifier = Modifier.height(250.dp))

            SearchBar(context = context, searchBarFraction = 0.9f)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Search for your favourite outfits or checkout the Inspirations tab!",
                modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp).padding(top = 8.dp),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Check back here in some time to see your personalized feed.",
                modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp).padding(top = 8.dp),
                fontSize = 16.sp
            )

        }
    }
}