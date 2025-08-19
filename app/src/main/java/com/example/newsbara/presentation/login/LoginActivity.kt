package com.example.newsbara.presentation.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.newsbara.R
import com.example.newsbara.data.model.login.LoginRequest
import com.example.newsbara.presentation.OnboardingActivity
import com.example.newsbara.presentation.util.ResultState
import com.example.newsbara.presentation.home.HomeActivity
import com.example.newsbara.presentation.signup.SignUpActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etLoginEmail)
        val etPassword = findViewById<EditText>(R.id.etLoginPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val textRegister = findViewById<TextView>(R.id.text_register)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "이메일과 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.login(LoginRequest(email, password))
        }

        lifecycleScope.launchWhenStarted {
            viewModel.loginResult.collect { result ->
                when (result) {
                    is ResultState.Success -> {
                        val accessToken = result.data.accessToken

                        getSharedPreferences("auth", MODE_PRIVATE)
                            .edit()
                            .putString("accessToken", accessToken)
                            .apply()

                        Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        finish()
                    }

                    is ResultState.Failure -> {
                        Toast.makeText(this@LoginActivity, result.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }

        textRegister.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}
