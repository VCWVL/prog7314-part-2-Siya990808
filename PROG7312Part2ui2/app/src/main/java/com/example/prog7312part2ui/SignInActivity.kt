package com.example.prog7312part2ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in)

        // Sign Up link click
        findViewById<TextView>(R.id.account_link).setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // Sign In button click
        findViewById<Button>(R.id.sign_in_button).setOnClickListener {
            // For now, just go to dashboard
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }
}