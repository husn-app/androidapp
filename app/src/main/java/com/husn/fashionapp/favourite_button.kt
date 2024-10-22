import android.content.Context
import androidx.compose.material.Icon
import androidx.compose.material.IconToggleButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.fashionapp.R
import com.husn.fashionapp.AuthManager
import com.husn.fashionapp.LocalSignInHelper
import com.husn.fashionapp.post_url_request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

@Composable
fun FavoriteButton(
    isWishlisted: Boolean,
    onWishlistChange: (Boolean) -> Unit,
    productId: Int,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
//    var isWishlisted by remember { mutableStateOf(initialIsWishlisted) }
    val signInHelper = LocalSignInHelper.current

    IconToggleButton(
        checked = isWishlisted,
        onCheckedChange = {
            if (!AuthManager.isUserSignedIn) {
                // User is not signed in, launch signInHelper
                signInHelper?.signIn(onSignInSuccess = {
                    // After successful sign-in, proceed to send the POST request
                    coroutineScope.launch {
                        val newIsWishlisted = sendWishlistRequest(
                            productId = productId,
                            context = context
                        )
//                        isWishlisted = newIsWishlisted
                        onWishlistChange(newIsWishlisted)
                    }
                })
            } else {
                // User is signed in, proceed to send the POST request
                coroutineScope.launch {
                    val newIsWishlisted = sendWishlistRequest(
                        productId = productId,
                        context = context
                    )
//                    isWishlisted = newIsWishlisted
                    onWishlistChange(newIsWishlisted)
                }
            }
        }
    ) {
        if (isWishlisted) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Remove from Wishlist",
                tint = Color.Red
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.FavoriteBorder,
                contentDescription = "Add to Wishlist",
                tint = Color.Black
            )
        }
    }
}

suspend fun sendWishlistRequest(
    productId: Int,
    context: Context
): Boolean {
    return withContext(Dispatchers.IO) {
        val baseUrl = context.getString(R.string.husn_base_url)
        val url = "$baseUrl/api/wishlist/$productId"
        println("sendWishlistRequest: $url")
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
                println("sendWishlistRequest: $newIsWishlisted")
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
