package com.example.prog7312part2ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in)

        auth = FirebaseAuth.getInstance()

        val emailField = findViewById<EditText>(R.id.email_input)
        val passwordField = findViewById<EditText>(R.id.password_input)

        // Sign Up link
        findViewById<TextView>(R.id.account_link).setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // Sign In button
        findViewById<Button>(R.id.sign_in_button).setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (!isValidEmail(email)) {
                emailField.error = "Enter a valid email"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                passwordField.error = "Enter password"
                return@setOnClickListener
            }

            // Firebase Sign In
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, DashboardActivity::class.java))
                        finish()
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
