package com.example.firebaseappchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.firebaseappchat.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.lang.Exception

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySignupBinding
    private lateinit var data : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        data = FirebaseAuth.getInstance()

        binding.BtnDangKy.setOnClickListener{
            Dangky()
        }

    }
    class User(val uid: String,val name :String )
    private fun Dangky() {
        try {
            val name = binding.TxtName.text.toString()
            val email = binding.TxtEmail.text.toString()
            val matkhau = binding.TxtMatKhau.text.toString()
            val uid = FirebaseAuth.getInstance().uid ?:""
            val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

            val user = User(uid,binding.TxtName.text.toString());



            if (name.isEmpty()){
                Toast.makeText(this,"Vui Lòng Điền UserName", Toast.LENGTH_SHORT).show()
                return
            }
            if (email.isEmpty()){
                Toast.makeText(this,"Vui Lòng Điền Email", Toast.LENGTH_SHORT).show()
                return
            }
            if (matkhau.isEmpty()){
                Toast.makeText(this,"Vui Lòng Điền Mật Khẩu", Toast.LENGTH_SHORT).show()
                return
            }

            data.createUserWithEmailAndPassword(email,matkhau).addOnCompleteListener{
                    task->
                if (task.isSuccessful){

                    Toast.makeText(this,"Đăng Ký Thành Công", Toast.LENGTH_SHORT).show()
                    ref.setValue(user)
                    startActivity(Intent(this,LoginActivity::class.java))
                }
            }
        }catch (e: Exception){
            Toast.makeText(this,e.message, Toast.LENGTH_SHORT).show()
            return
        }
    }
}