package com.husn.fashionapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.example.fashionapp.R
import com.husn.fashionapp.ui.theme.AppTheme

class LandingActivity : ComponentActivity() {

//    private lateinit var signInLauncher: ActivityResultLauncher<Intent>
    private lateinit var signInHelper: SignInHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val signInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            signInHelper.handleSignInResult(result.data) //{
            //}
        }
        signInHelper = SignInHelper(this, signInLauncher, this)
        if(AuthManager.isUserSignedIn){
            val intent = Intent(this, FeedActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        }
        setContent {
            AppTheme {
                CompositionLocalProvider(LocalSignInHelper provides signInHelper) {
                    LandingPageScreen()
                }
            }
        }

//        onBackPressedDispatcher.addCallback(this) {
//            if (!AuthManager.isUserSignedIn) {
//                finishAffinity() // This will close all activities and exit the app
//            } else {
//                finish() // Handle normal finish behavior when signed in
//            }
//        }
    }
}

@Preview(showBackground = true)
@Composable
fun StylishTextBoxPreview() {
    AppTheme {
        LandingPageScreen()
    }
}

@Composable
fun StylishTextBox() {
    Text(
        text = "Husn",
//            style = MaterialTheme.typography.displayLarge, // Updated to Material3 style
        color = MaterialTheme.colorScheme.primary,
        fontSize = 100.sp,
        fontFamily = FontFamily.Cursive,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 64.dp)
    )
}

@Composable
fun LandingPageScreen() {
    val isUserSignedIn = AuthManager.isUserSignedIn
    val signInHelper = LocalSignInHelper.current
    val context = LocalContext.current
    Scaffold(
        backgroundColor = Color.Transparent,
        bottomBar = { if (isUserSignedIn) BottomBar() } // BottomBar placed correctly
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // Place StylishTextBox at the top center
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 40.dp), // Optional padding to adjust position
//                contentAlignment = Alignment.TopCenter
//            ) {
                StylishTextBox()
//            }

            // Sign-in button logic
            if (!isUserSignedIn) {
                val isDarkTheme = isSystemInDarkTheme()
                val imageResId = if (isDarkTheme) {
                    R.drawable.android_dark_rd_3x
                } else {
                    R.drawable.android_light_rd_3x
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(bottom = 320.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = "Google sign in",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .clickable {
                                signInHelper?.signIn()
                            }
                    )
                }
            }
        }
    }
}
