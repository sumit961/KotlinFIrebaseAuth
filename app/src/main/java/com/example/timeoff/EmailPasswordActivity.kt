package com.example.timeoff

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class EmailPasswordActivity : Activity() {

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = Firebase.auth
        // [END initialize_auth]
        // Get email and password from the Intent
        val email = intent.getStringExtra("email")
        val password = intent.getStringExtra("password")
        val isLogin = intent.getBooleanExtra("isLogin", false)

        // Make sure email and password are not null
        if (email != null && password != null) {
            if (isLogin) {
                // If isLogin is true, sign in the user
                signIn(email, password)
            } else {
                // If isLogin is false, create a new account
                createAccount(email, password)
            }
        }
    }





    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Account creation succeeded
                    val user = auth.currentUser
                    Toast.makeText(
                        baseContext,
                        "Account created successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                    updateUI(user)
                } else {
                    // Task failed: handle error
                    val exception = task.exception
                    Log.w("CreateAccount", "createUserWithEmail:failure", exception) // Log the exception

                    when (exception) {
                        is FirebaseAuthUserCollisionException -> {
                            // Email is already in use
                            Toast.makeText(
                                baseContext,
                                "This email is already registered. Please log in instead.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> {
                            // Handle other types of exceptions
                            Toast.makeText(
                                baseContext,
                                "Error: ${exception?.localizedMessage}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    updateUI(null)
                }
            }
    }





    private fun signIn(email: String, password: String) {
        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUI(null)
                }
            }
        // [END sign_in_with_email]
    }

    private fun sendEmailVerification() {
        // [START send_email_verification]
        val user = auth.currentUser!!
        user.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                // Email Verification sent
            }
        // [END send_email_verification]
    }

    private fun updateUI(user: FirebaseUser?) {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun reload() {
        // If the user is signed in, navigate to MainActivity (or any other activity)
        startActivity(Intent(this, HomeActivity::class.java))
        finish()  // Close the current activity so the user can't go back to the login screen
    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}