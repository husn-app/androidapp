package com.husn.fashionapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.fashionapp.R
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.analytics.FirebaseAnalytics

val LocalSignInHelper = staticCompositionLocalOf<SignInHelper?> { null }
@Composable
fun TopNavBar(modifier: Modifier = Modifier.statusBarsPadding()){
    val context = LocalContext.current
    val isUserSignedIn = AuthManager.isUserSignedIn
    val signInHelper = LocalSignInHelper.current
    var showDropdown by remember { mutableStateOf(false) }
    Row(
        modifier = modifier.fillMaxWidth().padding(top = 0.dp).padding(bottom = 16.dp).padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "Husn",
            fontSize = 28.sp, // Set a large font size
            fontWeight = FontWeight.Bold, // Bold font weight
            fontFamily = FontFamily.Serif,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Box{
            if (isUserSignedIn) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(AuthManager.pictureUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable { showDropdown = true },
                    contentScale = ContentScale.Crop)
//                    placeholder = painterResource(R.drawable.profile_placeholder) // Make sure to have a placeholder image
            }
            else {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(R.drawable.google_signin_1)
                        .build(),
                    contentDescription = "Google Sign In",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { signInHelper?.signIn() },
                    contentScale = ContentScale.Crop
                )
            }

            DropdownMenu(
                expanded = showDropdown,
                onDismissRequest = { showDropdown = false },
                modifier = Modifier.background(color = Color.White)
//                    offset = DpOffset(x = (-40).dp, y = 4.dp)
            ) {
                DropdownMenuItem(
                    text = { Text("Sign out", color = Color.Black, fontSize = 16.sp, fontFamily = FontFamily.Serif,
                        textAlign = TextAlign.Center) },
                    onClick = {
                        signInHelper?.signOut(context)
                        showDropdown = false
                    }
                )
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String = "",
    context: Context = LocalContext.current,
    modifier: Modifier = Modifier,
    searchBarFraction: Float = 0.96f
) {
    var searchText by remember { mutableStateOf(TextFieldValue(query)) }
    val firebaseAnalytics = remember { FirebaseAnalytics.getInstance(context) }

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
            },
            modifier = Modifier
                .fillMaxWidth(searchBarFraction),
//                .clip(RoundedCornerShape(16.dp)),  // Apply rounded corners
            shape = RoundedCornerShape(25.dp),  // Shape for the OutlinedTextField
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
                    val bundle = Bundle().apply {
                        putString(FirebaseAnalytics.Param.SEARCH_TERM, searchText.text)
                    }
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle)

                    val intent = Intent(context, SearchResultsActivity::class.java).apply {
                        putExtra("query", searchText.text)
                    }
                    context.startActivity(intent)
                }
            ),
            singleLine = true  // Ensure it's a single-line search bar
        )
    }
}

@Composable
fun BottomBar(selectedItem: Int = 0) {
    val isUserSignedIn = AuthManager.isUserSignedIn
    val signInHelper = LocalSignInHelper.current
    val iconSize = 28.dp
    val context = LocalContext.current
    var currentItem by remember { mutableStateOf(selectedItem) }
    println("BottomBar: $currentItem")
    Column {
        Divider(
            color = Color(0xFFC8BEA1), // Customize the color as needed
        )

        BottomNavigation(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color.Transparent, // Or your desired background color
            elevation = 0.dp
        ) {

            BottomNavigationItem(
                icon = {
                    Image(
                        painter = painterResource(id = if (selectedItem == 1) R.drawable.home_filled else R.drawable.home_unfilled),
                        contentDescription = "Inspiration",
                        modifier = Modifier.size(iconSize) // Use the same iconSize as other icons
                    )
                },
                selected = currentItem == 1, // Handle selection state if needed with navigation
                onClick = {
//                    currentItem = 1
                    val intent = Intent(context, FeedActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    context.startActivity(intent)
                },
                selectedContentColor = Color.Black, // Customize selected icon color
                unselectedContentColor = Color.Red // Customize unselected icon color
            )
            BottomNavigationItem(
                icon = {
                    Image(
                        painter = painterResource(id = if (selectedItem == 2) R.drawable.inspiration_filled else R.drawable.inspiration_unfilled),
                        contentDescription = "Inspiration",
                        modifier = Modifier.size(iconSize) // Use the same iconSize as other icons
                    )
                },
                selected = currentItem == 2,
                onClick = {
//                    currentItem = 2
                    val intent = Intent(context, InspirationsActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    context.startActivity(intent)
                },
                selectedContentColor = Color.Black,
                unselectedContentColor = Color.Gray
            )
            BottomNavigationItem(
                icon = {
                    Image(
                        painter = painterResource(id = if (selectedItem == 3) R.drawable.wishlist_filled else R.drawable.wishlist_empty),
                        contentDescription = "Inspiration",
                        modifier = Modifier.size(iconSize) // Use the same iconSize as other icons
                    )
                },
                selected = currentItem == 3,
                onClick = {
//                    currentItem = 3
                    if (!isUserSignedIn) {
                        signInHelper?.signIn(onSignInSuccess = {
                            val intent = Intent(context, WishlistActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            context.startActivity(intent)
                        })
                    } else {
                        val intent = Intent(context, WishlistActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        context.startActivity(intent)
                    }
                },
                selectedContentColor = Color.Black,
                unselectedContentColor = Color.Gray
            )
        }
    }
}