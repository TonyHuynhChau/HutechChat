package com.example.firebaseappchat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaseappchat.databinding.ActivityWelcomeBinding
import com.example.firebaseappchat.registerlogin.LoginActivity
import com.example.firebaseappchat.registerlogin.SignUpActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth

class Welcome : AppCompatActivity() {
    private lateinit var data : FirebaseAuth
    private lateinit var binding : ActivityWelcomeBinding

    var selectIndex = 0
    val langNames = arrayOf("English","Vietnamese","Japanese")

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

    fun selectItem(view: View){
        var selectItem = langNames[selectIndex]
        val langDialog = MaterialAlertDialogBuilder(this)
        langDialog.setTitle("App Languages")
        langDialog.setSingleChoiceItems(langNames,selectIndex){
            dialog,which ->
            selectIndex = which
            selectItem = langNames[which]
        }
        langDialog.setPositiveButton("Ok"){
            dialog,which ->
            Toast.makeText(this,"$selectItem has selected",Toast.LENGTH_SHORT).show()
        }
        langDialog.setNeutralButton("Cancel"){
            dialog,which ->
            dialog.dismiss()
        }
        langDialog.show()
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