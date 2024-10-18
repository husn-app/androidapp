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
import androidx.compose.material3.Button
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
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

        Button(onClick = {
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
        }) {
            Text("Wishlist")
        }

//        if (showWishlist) {
//            // Launch the WishlistActivity once the data is ready
//            LaunchedEffect(Unit) {
//                val intent = Intent(context, WishlistActivity::class.java)
//                context.startActivity(intent)
//                showWishlist = false // Reset the flag
//            }
//        }

//        if(isUserSignedIn) {
//        Text(text = "wishlist", fontSize = 20.sp, modifier = Modifier.padding(16.dp).clickable {
//            if (!isUserSignedIn) {
//                signInHelper?.signIn()
//            }
//            val intent = Intent(context, WishlistActivity::class.java)
//            context.startActivity(intent)
//        })
//            val baseUrl = context.getString(R.string.husn_base_url)
//            val url = "$baseUrl/wishlist_android"
//            val request = get_url_request(context, url)
//            client.newCall(request).enqueue(object : Callback {
//                override fun onFailure(call: Call, e: IOException) {
//                    e.printStackTrace()
//                }
//
//                override fun onResponse(call: Call, response: Response) {
//                    if (response.isSuccessful) {
//                        val session = response.header("Set-Cookie")
//                        saveSessionCookie(session, context)
//
//                        val responseData = response.body?.string()
//                        responseData?.let {
//                            // Start a new activity with the search result data
//                            val intent = Intent(context, WishlistActivity::class.java)
//                            intent.putExtra("responseData", it)
//                            context.startActivity(intent)
//                        }
//                    } else {
//                        println("wishlist_response failed with status: ${response.code}\n${response.body}")
//                    }
//                }
//            })
//        })
//        }
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
//        SearchBar()  // Call SearchBar here
//    }
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
fun BottomBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
//        Text(text = "Home", fontSize = 20.sp)
//        SvgIcon(svgResource = R.drawable.home_icon)
        Text(text = "Inspiration", fontSize = 20.sp)
        Text(text = "Wishlist", fontSize = 20.sp)
    }
}

@Composable
fun SvgIcon(svgResource: Int, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val drawable = remember {
        ContextCompat.getDrawable(context, svgResource) // Load SVG from resources
    }

    AndroidView(
        factory = { ctx ->
            ImageView(ctx).apply {
                setImageDrawable(drawable)

            }
        },
        modifier = modifier.size(24.dp) // Adjust size as needed
    )
}