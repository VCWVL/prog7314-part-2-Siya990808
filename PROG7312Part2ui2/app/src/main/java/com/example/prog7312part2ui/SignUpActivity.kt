package com.example.prog7312part2ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up)

        // Sign In link click
        findViewById<TextView>(R.id.account_link).setOnClickListener {
            finish() // Go back to SignInActivity
        }

        // Sign Up button click
        findViewById<Button>(R.id.sign_up_button).setOnClickListener {
            // For now, just go to dashboard
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }
}