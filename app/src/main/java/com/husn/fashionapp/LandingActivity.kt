package com.husn.fashionapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.* // ktlint-disable no-wildcard-imports
import androidx.compose.material.* // ktlint-disable no-wildcard-imports
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.fashionapp.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.husn.fashionapp.AuthManager
import com.husn.fashionapp.SignInHelper
import com.husn.fashionapp.OnboardingActivity // Correct import for OnboardingActivity
import com.husn.fashionapp.FeedActivity // Correct import for FeedActivity
import com.husn.fashionapp.TopNavBar
import com.husn.fashionapp.ui.theme.AppTheme

class LandingActivity : ComponentActivity() {

//    private lateinit var signInLauncher: ActivityResultLauncher<Intent>
    private lateinit var signInHelper: SignInHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //WindowCompat.setDecorFitsSystemWindows(window, false)
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

        onBackPressedDispatcher.addCallback(this) {
            if (!AuthManager.isUserSignedIn) {
                finishAffinity() // This will close all activities and exit the app
            } else {
                finish() // Handle normal finish behavior when signed in
            }
        }
    }
}

@Composable
fun LandingPageScreen() {
    val isUserSignedIn = AuthManager.isUserSignedIn
    val signInHelper = LocalSignInHelper.current
    val context = LocalContext.current
    Scaffold(
//        topBar = {TopNavBar()},
        backgroundColor = MaterialTheme.colorScheme.background,
        bottomBar = { if( isUserSignedIn) BottomBar()} // BottomBar placed correctly
    ) { innerPadding -> // Use innerPadding to avoid content overlapping the BottomBar
        TopNavBar()
        if (!isUserSignedIn) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(bottom = 24.dp), // Add some gap from the bottom of the screen
                contentAlignment = Alignment.BottomCenter
            ) {
                Image(
                    painter = painterResource(id = R.drawable.android_neutral_rd_3x),
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