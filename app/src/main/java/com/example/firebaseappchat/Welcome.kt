package com.example.firebaseappchat

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaseappchat.databinding.ActivityWelcomeBinding
import com.example.firebaseappchat.registerlogin.LoginActivity
import com.example.firebaseappchat.registerlogin.SignUpActivity
import com.google.firebase.auth.FirebaseAuth


class Welcome : AppCompatActivity() {
    private lateinit var data : FirebaseAuth
    private lateinit var binding : ActivityWelcomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.BtnDK.setOnClickListener{
            btnDK()
        }
        binding.BtnDangNhap.setOnClickListener{
            btnDN()
        }
    }

    private fun btnDN() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun btnDK() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }
}