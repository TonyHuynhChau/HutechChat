package com.example.firebaseappchat.registerlogin

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.firebaseappchat.databinding.ActivityLoginBinding
import com.example.firebaseappchat.messages.MainActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import java.lang.Exception

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var data: FirebaseAuth
    private lateinit var uid: String
    private lateinit var Loading: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = ""
        data = FirebaseAuth.getInstance()

        binding.BtnDangNhap.setOnClickListener {
            Loading = ProgressDialog(this)
            Loading.setTitle("Đang Đăng Nhập")
            Loading.setMessage("Xin Đợi Trong Giây Lát")
            Loading.show()
            login()
        }

        binding.textView5.setOnClickListener {
            toDangKy()
        }

        supportActionBar?.title = ""
    }

    private fun toDangKy() {
        startActivity(Intent(this, SignUpActivity::class.java))
    }

    private fun TOKEN() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(
                    "MESSAGE FAILED TOKEN",
                    "Fetching FCM registration token failed",
                    task.exception
                )
                return@OnCompleteListener
            }
            val token = task.result
            // Log and toast
            FirebaseDatabase.getInstance().getReference("/user/$uid").child("Token").setValue(token)
                .addOnCompleteListener(
                    OnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("MESSAGE TOKEN", token.toString())
                        }
                    })

        })
    }

    private fun login() {
        try {
            val email = binding.TxtEmail.text.toString()
            val matkhau = binding.TxtMatKhau.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(this, "Vui Lòng Điền Email", Toast.LENGTH_SHORT).show()
                return
            }
            if (matkhau.isEmpty()) {
                Toast.makeText(this, "Vui Lòng Điền Mật Khẩu", Toast.LENGTH_SHORT).show()
                return
            }

            data.signInWithEmailAndPassword(email, matkhau).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    uid = FirebaseAuth.getInstance().uid.toString()
                    TOKEN()
                    Toast.makeText(this, "Đăng Nhập Thành Công", Toast.LENGTH_SHORT).show()
                    Loading.dismiss()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } else {//Đăng Nhập Thất Bại.
                    Loading.dismiss()
                    Loading = ProgressDialog(this)
                    Loading.setTitle("Đăng Nhập Thất Bại")
                    Loading.setMessage("Vui Lòng Đăng Ký Tại Tài Khoản Mới")
                    Loading.show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            return
        }

    }


}