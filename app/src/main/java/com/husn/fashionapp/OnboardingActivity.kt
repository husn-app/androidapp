package com.husn.fashionapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Scaffold
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fashionapp.R
import com.husn.fashionapp.ui.theme.AppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response
import okio.IOException
import org.json.JSONObject

class OnboardingActivity : ComponentActivity() {
    private lateinit var signInHelper: SignInHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //WindowCompat.setDecorFitsSystemWindows(window, false)

        val signInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            signInHelper.handleSignInResult(result.data) //{

            //}
        }
        signInHelper = SignInHelper(this, signInLauncher, this)

        setContent {
            AppTheme {
                CompositionLocalProvider(LocalSignInHelper provides signInHelper) {
                    GenderAgeInputScreen()
                }
            }
        }
    }
}

@Composable
fun GenderAgeInputScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // State variables
    var selectedGender by remember { mutableStateOf<String?>(null) }
    var age by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopNavBar() },
        backgroundColor = MaterialTheme.colorScheme.background,
//        bottomBar = { BottomBar(context = context) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GenderOption(
                    drawableRes = R.drawable.boy_bg2, // Your custom drawable
                    label = "Man",
                    isSelected = selectedGender == "MAN",
                    onClick = { selectedGender = "MAN" }
                )
                Spacer(modifier = Modifier.width(24.dp))
                GenderOption(
                    drawableRes = R.drawable.girl_bg, // Your custom drawable
                    label = "Woman",
                    isSelected = selectedGender == "WOMAN",
                    onClick = { selectedGender = "WOMAN" }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "What's your age",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = age,
                onValueChange = { input ->
                    // Allow only numbers
                    if (input.all { it.isDigit() }) {
                        age = input
                    }
                },
                label = { Text("Age") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        val ageInt = age.toIntOrNull() ?: 0
                        if (ageInt < 12 || ageInt > 72) {
                            Toast.makeText(
                                context,
                                "Please enter age between 12 and 72",
                                Toast.LENGTH_SHORT
                            ).show()
                        }else{
                            handleSubmission(selectedGender, age, context, coroutineScope)
                        }
                    }
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                shape = RoundedCornerShape(25.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Show Submit button only if gender is selected and age is not empty
            if (selectedGender != null && age.isNotBlank()) {
                val ageInt = age.toIntOrNull() ?: 0
                if (ageInt >= 0 && ageInt <= 72) {
                    Button(
                        onClick = {
                            handleSubmission(selectedGender, age, context, coroutineScope)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        shape = RoundedCornerShape(25.dp),
                    ) {
                        Text(text = "Let's go!", color = MaterialTheme.colorScheme.onPrimary, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

fun handleSubmission(
    selectedGender: String?,
    age: String,
    context: Context,
    coroutineScope: CoroutineScope
) {
    if (selectedGender != null && age.isNotBlank()) {
        coroutineScope.launch(Dispatchers.IO) {
            sendPostRequest(
                gender = selectedGender,
                age = age,
                context = context
            )
        }
    }
}

@Composable
fun GenderOption(
    drawableRes: Int,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(8.dp)
    ) {
        Image(
            painter = painterResource(id = drawableRes),
            contentDescription = label,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .border(
                    BorderStroke(
                        width = if (isSelected) 4.dp else 2.dp,
                        color = MaterialTheme.colorScheme.outline
                    ),
                    shape = CircleShape
                ),
//                .padding(8.dp),
            contentScale = ContentScale.FillWidth
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, color = MaterialTheme.colorScheme.primary)
    }
}

suspend fun sendPostRequest(
    gender: String,
    age: String,
    context: Context,
) {
    val client = OkHttpClient()
    val baseUrl = context.getString(R.string.husn_base_url)
    val url = "$baseUrl/api/onboarding"

    val json = JSONObject().apply {
        put("gender", gender)
        put("age", age.toIntOrNull() ?: 0)
    } //.toString()
    val request = post_url_request(context, url, json)

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                val headers = response.headers
                val cookies = headers.values("Set-Cookie")
                saveSessionCookie(cookies, context)
                val responseBody = response.body?.string()
//                println("onboardingactivity response: $responseBody")

                val intent = Intent(context, FeedActivity::class.java)
                context.startActivity(intent)
            }
        }
    })
}
