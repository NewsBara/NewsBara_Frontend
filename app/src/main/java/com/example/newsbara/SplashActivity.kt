package com.example.newsbara

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.newsbara.presentation.home.HomeActivity
import com.example.newsbara.presentation.login.LoginActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            val sharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)
            val accessToken = sharedPreferences.getString("accessToken", null)

            if (accessToken.isNullOrEmpty()) {
                // 로그인 안 되어 있으면 로그인 화면으로
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                // 로그인 되어 있으면 홈 화면으로
                startActivity(Intent(this, HomeActivity::class.java))
            }

            finish()
        }, 2000)
    }
}
