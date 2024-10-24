package com.husn.fashionapp

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ShareCompat
import com.example.fashionapp.R

@Composable
fun ShareButton(
    url: String,
    modifier: Modifier = Modifier,
    contentDescription: String = "Share"
) {
    val context = LocalContext.current

    IconButton(
        onClick = {
            shareUrl(context, url)
        },
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = R.drawable.insta_share_icon2),
            contentDescription = "Inspiration",
            modifier = Modifier.size(size = 28.dp) // Use the same iconSize as other icons
        )
    }
}

private fun shareUrl(context: android.content.Context, url: String) {
    if (url.isNotBlank()) {
        val formattedUrl = if (url.startsWith("http://") || url.startsWith("https://")) {
            url
        } else {
            "https://$url"
        }

        val shareIntent = ShareCompat.IntentBuilder(context)
            .setType("text/plain")
            .setText(formattedUrl) // Sharing only the URL
            .intent

        if (shareIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(Intent.createChooser(shareIntent, "Share via"))
        } else {
            Toast.makeText(context, "No application available to share the URL.", Toast.LENGTH_SHORT).show()
        }
    } else {
        Toast.makeText(context, "Invalid URL.", Toast.LENGTH_SHORT).show()
    }
}