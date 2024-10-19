package com.husn.fashionapp

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fashionapp.R
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import androidx.compose.runtime.staticCompositionLocalOf
import android.widget.ImageView
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import okhttp3.RequestBody

@Composable
fun SetStatusBarColor() {
    val systemUiController = rememberSystemUiController()
    val statusBarColor = MaterialTheme.colorScheme.primary // Or any other color you prefer
    val useDarkIcons = !isSystemInDarkTheme() && statusBarColor.luminance() > 0.5f
    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons
        )
    }
}

val LocalSignInHelper = staticCompositionLocalOf<SignInHelper?> { null }


@Composable
fun TopNavBar(modifier: Modifier = Modifier){
    SetStatusBarColor()
    val context = LocalContext.current
    val isUserSignedIn = AuthManager.isUserSignedIn
    val signInHelper = LocalSignInHelper.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "Husn",
            fontSize = 24.sp, // Set a large font size
            fontWeight = FontWeight.Bold, // Bold font weight
            fontFamily = FontFamily.Serif,
            color = Color.Black,
            modifier = Modifier
                .padding(16.dp) // Padding around the text
                .clickable {
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                },
            textAlign = TextAlign.Center
        )

        val signInText = if (isUserSignedIn) "Sign out" else "Sign in"
        println("husn_logo:$isUserSignedIn\n $signInText \n $signInHelper")

        Text(text=signInText, fontSize = 20.sp, modifier = Modifier.padding(16.dp).clickable {
            if (isUserSignedIn) {
                signInHelper?.signOut(context)
            } else {
                signInHelper?.signIn()
            }
        })

    }
}

@Composable
fun SearchBar(
    query: String = "",
    context: Context = LocalContext.current,
    modifier: Modifier = Modifier,
    searchBarFraction: Float = 0.9f
) {
    var searchText by remember { mutableStateOf(TextFieldValue(query)) }
//    val textColor = MaterialTheme.colorScheme.onSurface // For the text color
    val textColor = Color.Black
    val backgroundColor = Color.White

    Box(
        modifier = modifier
            .fillMaxWidth()  // Occupy full width but not full height.
//            .height(50.dp),
            .wrapContentHeight(),  // Limit height to the SearchBar's height
        contentAlignment = Alignment.Center
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = {
                if (searchText.text.isEmpty()) {
                    Text("Search...", color = textColor)
                } else {
                    Text(query, color = textColor)
                }
//                searchText
            },
            modifier = Modifier
                .fillMaxWidth(searchBarFraction),
//                .clip(RoundedCornerShape(16.dp)),  // Apply rounded corners
            shape = RoundedCornerShape(25.dp),  // Shape for the OutlinedTextField
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

@Composable
fun OpenWishlistActivity(context: Context) {
    val isUserSignedIn = AuthManager.isUserSignedIn
    val signInHelper = LocalSignInHelper.current
    if (!isUserSignedIn) {
        signInHelper?.signIn(onSignInSuccess = {
            // Open WishlistActivity upon successful sign-in
            val intent = Intent(context, WishlistActivity::class.java)
            context.startActivity(intent)
        })
    } else {
        // If already signed in, directly open WishlistActivity
        val intent = Intent(context, WishlistActivity::class.java)
        context.startActivity(intent)
    }
}

@Composable
fun BottomBar(context: Context) {
    val isUserSignedIn = AuthManager.isUserSignedIn
    val signInHelper = LocalSignInHelper.current
    BottomNavigation(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color.Transparent, // Or your desired background color
        elevation = 0.dp
    ) {

        BottomNavigationItem(
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") },
            selected = false, // Handle selection state if needed with navigation
            onClick = {
                val intent = Intent(context, OnboardingActivity::class.java)
                context.startActivity(intent)
            },
            selectedContentColor = Color.Black, // Customize selected icon color
            unselectedContentColor = Color.Gray // Customize unselected icon color
        )
        BottomNavigationItem(
            icon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Inspiration") },
            selected = false,
            onClick = {
                val intent = Intent(context, InspirationsActivity::class.java)
                context.startActivity(intent)
            },
            selectedContentColor = Color.Black,
            unselectedContentColor = Color.Gray
        )
        BottomNavigationItem(
            icon = { Icon(imageVector = Icons.Default.Favorite, contentDescription = "Wishlist") },
            selected = false,
            onClick = {
                if (!isUserSignedIn) {
                    signInHelper?.signIn(onSignInSuccess = {
                        // Open WishlistActivity upon successful sign-in
                        val intent = Intent(context, WishlistActivity::class.java)
                        context.startActivity(intent)
                    })
                } else {
                    // If already signed in, directly open WishlistActivity
                    val intent = Intent(context, WishlistActivity::class.java)
                    context.startActivity(intent)
                }
            },
            selectedContentColor = Color.Black,
            unselectedContentColor = Color.Gray
        )
    }
}