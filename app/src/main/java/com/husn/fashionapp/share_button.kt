package com.husn.fashionapp

import android.content.Intent
import android.widget.Toast
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ShareCompat

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
        Icon(
            imageVector = Icons.Default.Share,
            contentDescription = contentDescription
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