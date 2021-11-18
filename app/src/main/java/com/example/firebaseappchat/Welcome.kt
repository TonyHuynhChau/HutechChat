package com.example.firebaseappchat

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaseappchat.databinding.ActivityWelcomeBinding
import com.example.firebaseappchat.messages.MainActivity
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
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun btnDK() {
        val intent = Intent(this, SignUpActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}