package com.example.firebaseappchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firebaseappchat.databinding.ActivityWelcomeBinding
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
        val intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
    }

    private fun btnDK() {
        val intent = Intent(this,SignUpActivity::class.java)
        startActivity(intent)
    }
}