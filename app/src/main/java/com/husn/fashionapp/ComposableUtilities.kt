package com.husn.fashionapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.fashionapp.R
import com.google.firebase.analytics.FirebaseAnalytics

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ImageFromUrl(url: String, clickable: () -> Unit = {}, modifier: Modifier = Modifier){
    val painter = rememberAsyncImagePainter(url, placeholder = painterResource(id = R.drawable.grey_image5) )
    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.75f) // Maintain aspect ratio
            .clip(RoundedCornerShape(16.dp))
            .clickable{
                clickable()
            }
    )
}

@Composable
fun SearchBar(
    query: String = "",
    context: Context = LocalContext.current,
    modifier: Modifier = Modifier,
    searchBarFraction: Float = 0.96f,
) {
    var searchText by remember { mutableStateOf(TextFieldValue(query)) }
    val firebaseAnalytics = remember {
        try {
            FirebaseAnalytics.getInstance(context)
        } catch (e: Exception) {
            null
        }
    }

    val textColor = Color.Black
    val backgroundColor = Color.White
    val focusedBorderColor = Color(0xffc8bea1)

    Box(
        modifier = modifier
            .fillMaxWidth()  // Occupy full width but not full height.
            .height(52.dp)
            .wrapContentHeight(),  // Limit height to the SearchBar's height
        contentAlignment = Alignment.Center
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = {
                if (searchText.text.isEmpty()) {
                    Text("Search...", color = textColor, style = MaterialTheme.typography.bodyMedium)
                } else {
                    Text(query, color = textColor, style = MaterialTheme.typography.bodyMedium)
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
                unfocusedTextColor = textColor,
                focusedBorderColor = focusedBorderColor
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    val bundle = Bundle().apply {
                        putString(FirebaseAnalytics.Param.SEARCH_TERM, searchText.text)
                    }
                    firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SEARCH, bundle)

                    val intent = Intent(context, SearchResultsActivity::class.java).apply {
                        putExtra("query", searchText.text)
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    context.startActivity(intent)
                }
            ),
            singleLine = true,  // Ensure it's a single-line search bar
            textStyle = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun InPlaceSearchBar(
    query: String = "",
    context: Context = LocalContext.current,
    modifier: Modifier = Modifier,
    searchBarFraction: Float = 0.96f,
    onSearch: (String) -> Unit
) {
    var searchText by remember { mutableStateOf(TextFieldValue(query)) }
    val firebaseAnalytics = remember { FirebaseAnalytics.getInstance(context) }

    val textColor = Color.Black
    val backgroundColor = Color.White
    val focusedBorderColor = Color(0xffc8bea1)

    Box(
        modifier = modifier
            .fillMaxWidth()  // Occupy full width but not full height.
            .height(52.dp)
            .wrapContentHeight(),  // Limit height to the SearchBar's height
        contentAlignment = Alignment.Center
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = {
                if (searchText.text.isEmpty()) {
                    Text("Search...", color = textColor, style = MaterialTheme.typography.bodyMedium)
                } else {
                    Text(query, color = textColor, style = MaterialTheme.typography.bodyMedium)
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
                unfocusedTextColor = textColor,
                focusedBorderColor = focusedBorderColor
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    val bundle = Bundle().apply {
                        putString(FirebaseAnalytics.Param.SEARCH_TERM, searchText.text)
                    }
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle)

                    onSearch(searchText.text)
                }
            ),
            singleLine = true,  // Ensure it's a single-line search bar
            textStyle = MaterialTheme.typography.bodyMedium
        )
    }
}