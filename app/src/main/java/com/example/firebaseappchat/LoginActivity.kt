package com.example.firebaseappchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.firebaseappchat.databinding.ActivityLoginBinding
import com.example.firebaseappchat.messages.MainActivity
import com.example.firebaseappchat.registerlogin.SignUpActivity

import com.google.firebase.auth.FirebaseAuth
import java.lang.Exception

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private lateinit var data : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = ""
        data = FirebaseAuth.getInstance()

        binding.BtnDangNhap.setOnClickListener{
            login()
        }

        binding.textView5.setOnClickListener{
            toDangKy()
        }

        supportActionBar?.title = ""
    }

    private fun toDangKy() {
        startActivity(Intent(this, SignUpActivity::class.java))
    }

    private fun login() {
        try {
            val email = binding.TxtEmail.text.toString()
            val matkhau = binding.TxtMatKhau.text.toString()

            if (email.isEmpty()){
                Toast.makeText(this,"Vui Lòng Điền Email",Toast.LENGTH_SHORT).show()
                return
            }
            if (matkhau.isEmpty()){
                Toast.makeText(this,"Vui Lòng Điền Mật Khẩu",Toast.LENGTH_SHORT).show()
                return
            }

            data.signInWithEmailAndPassword(email,matkhau).addOnCompleteListener{
                    task->
                if (task.isSuccessful){
                    Toast.makeText(this,"Đăng Nhập Thành Công",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                else{
                    Toast.makeText(this,"Đăng Nhập Thất Bại. Vui Lòng Đăng Ký Tại Tài Khoản Mới",Toast.LENGTH_SHORT).show()
                }
            }
        }catch (e:Exception){
            Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
            return
        }

    }
}