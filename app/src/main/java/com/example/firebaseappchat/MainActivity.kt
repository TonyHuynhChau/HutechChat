package com.example.firebaseappchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firebaseappchat.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var data : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        data = FirebaseAuth.getInstance()
        binding.Settext.text = data.uid
    }
}