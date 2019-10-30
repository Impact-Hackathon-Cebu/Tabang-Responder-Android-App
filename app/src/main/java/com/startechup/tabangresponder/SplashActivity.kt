package com.startechup.tabangresponder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        button_login.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        button_signup.setOnClickListener {
            startActivity(Intent(this, CreateUserActivity::class.java))
            finish()
        }
    }

}
