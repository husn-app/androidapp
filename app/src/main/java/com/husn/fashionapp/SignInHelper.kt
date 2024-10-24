// SignInHelper.kt
package com.husn.fashionapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.fashionapp.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okio.IOException
import org.json.JSONObject

object AuthManager {
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    // Observable sign-in state
    var isUserSignedIn by mutableStateOf(firebaseAuth.currentUser != null)
        private set
    var gender: String? = null
    var pictureUrl: String? = null
    var onboardingStage: String? = null

    fun initialize(context: Context) {
        firebaseAuth.addAuthStateListener { auth ->
            isUserSignedIn = auth.currentUser != null
        }
        gender = getSavedKeyValue("gender", context)
        onboardingStage = getSavedKeyValue("onboarding_stage", context)
        pictureUrl = getSavedKeyValue("picture_url", context)
    }

    fun signOut() {
        firebaseAuth.signOut()
        gender = null
        pictureUrl = null
        onboardingStage = null
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
    private val fetch_utility = Fetchutilities(context)

    init {
        AuthManager.initialize(activity)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.server_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(activity, gso)
    }

    fun signIn(onSignInSuccess: (() -> Unit)? = null) {
        onSignInSuccessCallback = onSignInSuccess
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    fun handleSignInResult(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        println("sign_in: $task")
        try {
            val account = task.getResult(ApiException::class.java)
            println("sign_in account: $account \n idToken: ${account?.idToken}")
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: Exception) {
            println("sign_in failed: ${e}")
            e.printStackTrace()
            activity.finish()
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        AuthManager.firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    println("Firebase sign-in successful")
                    sendIdTokenToServer(idToken)

                } else {
                    println("Firebase sign-in failed: ${task.exception}")
                }
            }
    }

    private fun sendIdTokenToServer(idToken: String /*, onSuccess: (() -> Unit)? = null */) {
        val url = "$baseUrl/login_android"
        val requestBodyJson = JSONObject("{\"idToken\": \"$idToken\"}")
        val request = post_url_request(context, url, requestBodyJson)

        coroutineScope.launch {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
//                    println("backend response: $responseBody")
                    responseBody?.let{
                        var responseData =
                            JSONObject(fetch_utility.sanitizeJson(it))
                        if (responseData.has("gender") && !responseData.isNull("gender")) {
                            AuthManager.gender = responseData.getString("gender")
                            AuthManager.gender?.let { genderValue ->
                                putKeyValue("gender", genderValue, context)
                            }
                        }
                        else{
                            AuthManager.gender = null
                        }
                    }

                    val headers = response.headers
                    val cookies = headers.values("Set-Cookie")
//                    println("cookies: $cookies")
                    saveSessionCookie(cookies, context)
                    val onboarding_stage = getSavedKeyValue("onboarding_stage", context)

//                    AuthManager.gender = getSavedKeyValue("gender", context)
                    AuthManager.onboardingStage = getSavedKeyValue("onboarding_stage", context)
                    AuthManager.pictureUrl = getSavedKeyValue("picture_url", context)

//                    println("Inside signinhelper: gender=${AuthManager.gender}\tonboardingStage=${AuthManager.onboardingStage}\tpictureUrl=${AuthManager.pictureUrl}")
                    if(AuthManager.onboardingStage == null || AuthManager.onboardingStage != "COMPLETE"){
                        val intent = Intent(context, OnboardingActivity::class.java)
                        context.startActivity(intent)
                    }
                    else {
                        val intent = Intent(context, FeedActivity::class.java)
                        println("sing_in: onSignInSuccessCallback: $onSignInSuccessCallback")
                        if (onSignInSuccessCallback != null) {
                            println("sing_in: running onSuccess")
                            onSignInSuccessCallback?.let { it() }
                        } else {
                            println("sing_in: running feedactivity")
                            context.startActivity(intent)
                        }
                    }
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

        val intent = Intent(context, FeedActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)

        // Finish current activity so that the user cannot go back to this screen
        if (context is Activity) {
            context.finish()
        }
    }
}