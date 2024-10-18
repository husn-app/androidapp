// SignInHelper.kt
package com.husn.fashionapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.example.fashionapp.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Request
import okio.IOException
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth

object AuthManager {
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    // Observable sign-in state
    var isUserSignedIn by mutableStateOf(firebaseAuth.currentUser != null)
//        private set

    // Initialize the AuthStateListener
    init {
        firebaseAuth.addAuthStateListener { auth ->
            isUserSignedIn = auth.currentUser != null
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }
}

class SignInHelper(
    private val activity: Activity,
    private val signInLauncher: ActivityResultLauncher<Intent>,
    private val context: Context // Add Context parameter
) {
    private val client = OkHttpClient()
    private val baseUrl = activity.getString(R.string.husn_base_url)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val googleSignInClient: GoogleSignInClient
    private var onSignInSuccessCallback: (() -> Unit)? = null

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.server_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(activity, gso)
    }

    fun signIn(onSignInSuccess: () -> Unit = {}) {
        onSignInSuccessCallback = onSignInSuccess
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    fun handleSignInResult(data: Intent?, onSignInSuccess: () -> Unit = {}) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        println("sign_in: $task")
        try {
            val account = task.result
            println("sign_in account: $account \n idToken: ${account?.idToken}")
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: Exception) {
            println("sign_in failed: ${e}")
            e.printStackTrace()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String, onSignInSuccess: () -> Unit = {}) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        AuthManager.firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    println("Firebase sign-in successful")
                    sendIdTokenToServer(idToken){
                        onSignInSuccessCallback?.invoke()
                        // Reset the callback
                        onSignInSuccessCallback = null
                    }
                } else {
                    println("Firebase sign-in failed: ${task.exception}")
                }
            }
    }

    private fun sendIdTokenToServer(idToken: String, onSuccess: () -> Unit = {}) {
        val url = "$baseUrl/login_android"
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = "{\"idToken\": \"$idToken\"}".toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("platform", "android")
            .build()

        coroutineScope.launch {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    println("backend response: $responseBody")
                    val session = response.header("Set-Cookie")
                    println("response_header:$session\nsignInHelper_session_cookie:$session")
                    saveSessionCookie(session, context)
                    // ... process response (e.g., navigation) ...
                    onSuccess()
                } else {
                    println("backend error: ${response.code} ${response.body}")
                }
            } catch (e: IOException) {
                println("Network error: ${e.message}")
            }
        }
    }

    fun signOut(context: Context) {
        googleSignInClient.signOut().addOnCompleteListener {
            // You can handle sign-out completion here if needed
            println("Signed out from Google Sign-In")
        }
        AuthManager.signOut()
        clearSessionCookie(context)

        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)

        // Finish current activity so that the user cannot go back to this screen
        if (context is Activity) {
            context.finish()
        }
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

fun clearSessionCookie(context: Context) {
    val sharedPreferences = context.getSharedPreferences("SessionPref", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.remove("session_cookie") // Remove the cookie specifically
    editor.apply()
}
