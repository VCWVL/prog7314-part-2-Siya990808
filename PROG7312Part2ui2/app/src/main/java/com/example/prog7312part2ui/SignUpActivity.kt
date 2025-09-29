package com.example.prog7312part2ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.prog7312part2ui.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val emailField = findViewById<EditText>(R.id.email_input)
        val usernameField = findViewById<EditText>(R.id.username_input)
        val passwordField = findViewById<EditText>(R.id.password_input)

        // Go back to SignIn
        findViewById<TextView>(R.id.account_link).setOnClickListener {
            finish()
        }

        // Sign Up button
        findViewById<Button>(R.id.sign_up_button).setOnClickListener {
            val email = emailField.text.toString().trim()
            val username = usernameField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (!isValidEmail(email)) {
                emailField.error = "Enter a valid email"
                return@setOnClickListener
            }
            if (username.isEmpty()) {
                usernameField.error = "Enter a username"
                return@setOnClickListener
            }
            if (password.length < 6) {
                passwordField.error = "Password must be at least 6 characters"
                return@setOnClickListener
            }

            // FirebaseAuth Sign Up
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid ?: ""
                        val user = User(uid, email, username)

                        // Save user to Firestore
                        db.collection("users").document(uid).set(user)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, SignInActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Error saving user: ${it.message}", Toast.LENGTH_LONG).show()
                            }
                    } else {
                        Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
