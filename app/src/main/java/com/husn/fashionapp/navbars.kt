package com.husn.fashionapp

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest


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
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall
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
                        .size(44.dp)
                        .clip(CircleShape)
                        .clickable { signInHelper?.signIn() },
                    contentScale = ContentScale.Crop
                )
            }
            DropdownMenu(
                expanded = showDropdown,
                onDismissRequest = { showDropdown = false },
                modifier = Modifier.background(color = MaterialTheme.colorScheme.surface)
            ) {
                DropdownMenuItem(
                    text = { Text("Sign out", color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, fontFamily = FontFamily.Serif,
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
fun BottomBar(selectedItem: Int = 0) {
    val isPreview = LocalInspectionMode.current
    val isUserSignedIn = if (isPreview) false else AuthManager.isUserSignedIn
//    val isUserSignedIn = AuthManager.isUserSignedIn
    val signInHelper = LocalSignInHelper.current
    val iconSize = 28.dp
    val context = LocalContext.current
    var currentItem by remember { mutableStateOf(selectedItem) }
    Column {
        Divider()
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
                        modifier = Modifier.size(iconSize), // Use the same iconSize as other icons
//                        colorFilter = ColorFilter.tint(if (selectedItem == 1) Color.Black else Color.Gray)
                    )
                },
                selected = currentItem == 1, // Handle selection state if needed with navigation
                onClick = {
//                    currentItem = 1
                    if (!isUserSignedIn) {
                        signInHelper?.signIn()
                    }
                    else {
                        val intent = Intent(context, FeedActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        context.startActivity(intent)
                    }
                },
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
            )
        }
    }
}