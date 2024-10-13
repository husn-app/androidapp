// AuthManager.kt
package com.husn.fashionapp

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth

object AuthManager {
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    // Observable sign-in state
    var isUserSignedIn by mutableStateOf(firebaseAuth.currentUser != null)
        private set

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
