package com.example.newsbara.presentation.signup

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.newsbara.R
import com.example.newsbara.data.model.signup.SignUpRequest
import com.example.newsbara.presentation.login.LoginActivity
import com.example.newsbara.presentation.util.ResultState
import dagger.hilt.android.AndroidEntryPoint
import kotlin.jvm.java


@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etName = findViewById<EditText>(R.id.etName)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val checkbox = findViewById<CheckBox>(R.id.checkbox_terms)
        val btnSignUp = findViewById<Button>(R.id.btnSignUp)

        btnSignUp.setOnClickListener {
            if (!checkbox.isChecked) {
                Toast.makeText(this, "약관 동의가 필요합니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = SignUpRequest(
                email = etEmail.text.toString(),
                password = etPassword.text.toString(),
                phone = etPhone.text.toString(),
                name = etName.text.toString()
            )

            viewModel.signUp(request)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.signUpResult.collect { state ->
                when (state) {
                    is ResultState.Success -> {
                        Toast.makeText(this@SignUpActivity, "회원가입 성공", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
                        finish()
                    }

                    is ResultState.Failure -> {
                        Toast.makeText(this@SignUpActivity, state.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }
    }
}

