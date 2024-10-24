package com.husn.fashionapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fashionapp.R



//@Composable
//fun LandingPage(onSignInClick: () -> Unit) {
//    // Main Layout
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.SpaceBetween // Pushes content to top and bottom
//    ) {
//
//        // Top Section: Title and Feature Highlights
//        Column {
//            Text(
//                text = "Welcome to My Awesome App",
//                style = MaterialTheme.typography.titleMedium,
//                fontWeight = FontWeight.Bold
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//        }
//
//        // Bottom Section: Google Sign-In Button
//        GoogleSignInButton(onClick = onSignInClick)
//    }
//}
//
//
//@Composable
//fun GoogleSignInButton(onClick: () -> Unit) {
//    Button(
//        onClick = onClick,
//        modifier = Modifier.fillMaxWidth(),
//        colors = ButtonColors()
//    )
//    //ButtonDefaults.buttonColors(
////            backgroundColor = MaterialTheme.colorScheme.primary
////        )
//     {
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            Icon(
//                painter = painterResource(id = R.drawable.google_signin_1), // Replace with your Google logo drawable
//                contentDescription = "Google Sign-In",
////                tint = Color.Unspecified // Or set a tint if desired
//            )
//            Spacer(modifier = Modifier.width(8.dp))
//            Text("Continue with Google")
//        }
//    }
//}