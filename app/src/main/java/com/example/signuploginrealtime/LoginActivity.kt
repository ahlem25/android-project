package com.example.signuploginrealtime

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {
    private lateinit var loginUsername: EditText
    private lateinit var loginPassword: EditText
    private lateinit var loginButton: Button
    private lateinit var signupRedirectText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginUsername = findViewById(R.id.login_username)
        loginPassword = findViewById(R.id.login_password)
        signupRedirectText = findViewById(R.id.signupRedirectText)
        loginButton = findViewById(R.id.login_button)

        loginButton.setOnClickListener {
            if (!validateUsername() || !validatePassword()) {
                // Handle validation failure if needed
            } else {
                checkUser()
            }
        }

        signupRedirectText.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateUsername(): Boolean {
        val username = loginUsername.text.toString()
        return if (username.isEmpty()) {
            loginUsername.error = "Username cannot be empty"
            false
        } else {
            loginUsername.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        val password = loginPassword.text.toString()
        return if (password.isEmpty()) {
            loginPassword.error = "Password cannot be empty"
            false
        } else {
            loginPassword.error = null
            true
        }
    }

    private fun checkUser() {
        val userUsername = loginUsername.text.toString().trim()
        val userPassword = loginPassword.text.toString().trim()
        val reference = FirebaseDatabase.getInstance().getReference("users")
        val checkUserDatabase = reference.orderByChild("username").equalTo(userUsername)

        checkUserDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    loginUsername.error = null
                    val passwordFromDB = snapshot.child(userUsername).child("password").getValue(String::class.java)
                    if (passwordFromDB == userPassword) {
                        loginUsername.error = null

                        // Navigate to MainActivity with the username
                        val intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
                            putExtra("username", userUsername)
                        }
                        startActivity(intent)
                    } else {
                        loginPassword.error = "Invalid Credentials"
                        loginPassword.requestFocus()
                    }
                } else {
                    loginUsername.error = "User does not exist"
                    loginUsername.requestFocus()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}
