import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

import com.husn.fashionapp.AuthManager
import com.husn.fashionapp.LocalSignInHelper
import com.husn.fashionapp.R
import com.husn.fashionapp.WishlistActivity
import com.husn.fashionapp.post_url_request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.json.JSONObject

@Composable
fun FavoriteButton(
    isWishlisted: Boolean,
    onWishlistChange: (Boolean) -> Unit,
    productId: Int,
    iconSize: Dp
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val signInHelper = LocalSignInHelper.current
    val interactionSource = remember { MutableInteractionSource() }
    Image(
        painter = painterResource(id = if (isWishlisted) R.drawable.heart_filled else R.drawable.heart_unfilled),
        contentDescription = "Inspiration",
        modifier = Modifier
            .padding(top=4.dp).padding(horizontal=4.dp).size(iconSize)
            .clickable(interactionSource = interactionSource, indication = null) {
                if (!AuthManager.isUserSignedIn) {
                    // User is not signed in, launch signInHelper
                    signInHelper?.signIn(onSignInSuccess = {
                        // After successful sign-in, proceed to send the POST request
                        coroutineScope.launch {
                            val newIsWishlisted = sendWishlistRequest(
                                productId = productId,
                                context = context
                            )
                            onWishlistChange(newIsWishlisted)
                        }
                        val intent = Intent(context, WishlistActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        context.startActivity(intent)
                    })
                } else {
                    // User is signed in, proceed to send the POST request
                    coroutineScope.launch {
                        val newIsWishlisted = sendWishlistRequest(
                            productId = productId,
                            context = context
                        )
                        onWishlistChange(newIsWishlisted)
                    }
                }
            }
    )
}

suspend fun sendWishlistRequest(
    productId: Int,
    context: Context
): Boolean {
    return withContext(Dispatchers.IO) {
        val baseUrl = context.getString(R.string.husn_base_url)
        val url = "$baseUrl/api/wishlist/$productId"
//        println("sendWishlistRequest: $url")
        val client = OkHttpClient()
        val requestBodyJson = JSONObject("{\"productId\": $productId}")//.apply{
//            put("productId", productId)
//        }
        val request = post_url_request(context, url, requestBodyJson)

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                // Parse the responseBody JSON to get the is_wishlisted parameter
                val jsonObject = JSONObject(responseBody)
                val newIsWishlisted = jsonObject.getBoolean("wishlist_status")
//                println("sendWishlistRequest: $newIsWishlisted")
                newIsWishlisted
            } else {
                println("sendWishlistRequest: response failed")
                // Handle error response
                false
            }
        } catch (e: Exception) {
            println("sendWishlistRequest: exception raised: $e")
            // Handle exceptions
            false
        }
    }
}
