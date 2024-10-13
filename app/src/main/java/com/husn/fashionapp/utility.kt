package com.husn.fashionapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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

interface SignInCallback {
    fun onSignInClicked()
}

val LocalSignInHelper = staticCompositionLocalOf<SignInHelper?> { null }


@Composable
fun HusnLogo(modifier: Modifier = Modifier){
    SetStatusBarColor()
    Row(
        modifier = Modifier.fillMaxWidth(),
//        modifier = Modifier.padding(8.dp) // Adds padding around the Row
        horizontalArrangement = Arrangement.SpaceBetween,
//        verticalAlignment = Alignment.CenterVertically
    ) {
        val context = LocalContext.current
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
//        Spacer(modifier = Modifier.width(12.dp))
        // Observe the sign-in state
        val isUserSignedIn = AuthManager.isUserSignedIn

        val signInText = if (isUserSignedIn) "Sign out" else "Sign in"
        val signInHelper = LocalSignInHelper.current
        println("husn_logo:$isUserSignedIn\n $signInText \n $signInHelper")
        Text(text=signInText, fontSize = 20.sp, modifier = Modifier.padding(16.dp).clickable {
            if (isUserSignedIn) {
                AuthManager.signOut()
            } else {
                signInHelper?.signIn()
            }
        }
//            val baseUrl = context.getString(R.string.husn_base_url)
//            val url = "$baseUrl/login_mobile"
//            var sessionCookie = getSessionCookieFromStorage(context) ?: ""
//            println("Sign in: $sessionCookie")
//
//            val request = Request.Builder()
//                .url(url)
//                .addHeader("Cookie", sessionCookie)
//                .addHeader("platform", "android")
//                .build()
//
//            client.newCall(request).enqueue(object : Callback {
//                override fun onFailure(call: Call, e: IOException) {
//                    e.printStackTrace()
//                }
//
//                override fun onResponse(call: Call, response: Response) {
//                    if (response.isSuccessful) {
//                        val responseData = response.body?.string()
//                        println("response_body:$responseData")
//                    } else {
//                        //println("Request failed with status: ${response.code}")
//                    }
//                }
//            })
//            }
        )
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

fun saveSessionCookie(cookie: String?, context: Context) {
    if(cookie == null)
        return
    val sharedPreferences = context.getSharedPreferences("SessionPref", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("session_cookie", cookie)  // Save the session cookie with the key "session_cookie"
    editor.apply()  // Apply changes asynchronously
}

fun getSessionCookieFromStorage(context: Context): String? {
    val sharedPreferences = context.getSharedPreferences("SessionPref", Context.MODE_PRIVATE)
    return sharedPreferences.getString("session_cookie", null)  // Return the session cookie or null if not found
}

