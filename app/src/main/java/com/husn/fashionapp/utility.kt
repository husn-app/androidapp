package com.husn.fashionapp

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.draw.clip
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
import com.google.accompanist.systemuicontroller.rememberSystemUiController

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

@Composable
fun HusnLogo(modifier: Modifier = Modifier){
    SetStatusBarColor()
    Row(
//        modifier = Modifier.padding(8.dp) // Adds padding around the Row
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