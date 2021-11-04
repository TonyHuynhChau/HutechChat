package com.example.firebaseappchat.registerlogin

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import com.example.firebaseappchat.databinding.ActivitySignupBinding
import com.google.android.gms.tasks.OnCompleteListener

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.parcel.Parcelize
import java.lang.Exception

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var data: FirebaseAuth
    private lateinit var Loading: ProgressDialog
    private var count = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        data = FirebaseAuth.getInstance()

        binding.BtnDangKy.setOnClickListener {
            Loading = ProgressDialog(this)
            Loading.setTitle("Đang Đăng Ký")
            Loading.setMessage("Xin Đợi Trong Giây Lát")
            Loading.show()
            Dangky()
        }

    }

    //class để nhận các giá trị User
    @Parcelize
    class getUser(
        val uid: String,
        val email: String,
        val name: String,
        val Urlphoto: String,
        val Token: String
    ) : Parcelable {
        constructor() : this("", "", "", "", "")
    }

    @Parcelize
    class User(val uid: String, val email: String, val name: String) : Parcelable {
        constructor() : this("", "", "")
    }

    private fun STT(uid: String) {
        val STT = FirebaseDatabase.getInstance().getReference("user")
        STT.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                count = snapshot.childrenCount.toInt()
                // STT.child(uid).child("STT").setValue(count + 1)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun TOKEN(uid: String) {
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
            FirebaseDatabase.getInstance().getReference("/user/$uid").child("Token").setValue(token)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("MESSAGE TOKEN", token.toString())
                    }
                }

        })
    }

    private fun Dangky() {
        try {
            val name = binding.TxtName.text.toString()
            val email = binding.TxtEmail.text.toString()
            val matkhau = binding.TxtMatKhau.text.toString()

            if (name.isEmpty()) {
                Toast.makeText(this, "Vui Lòng Điền UserName", Toast.LENGTH_SHORT).show()
                return
            }
            if (email.isEmpty()) {
                Toast.makeText(this, "Vui Lòng Điền Email", Toast.LENGTH_SHORT).show()
                return
            }
            if (matkhau.isEmpty()) {
                Toast.makeText(this, "Vui Lòng Điền Mật Khẩu", Toast.LENGTH_SHORT).show()
                return
            }
            if (matkhau.length < 6) {
                Toast.makeText(this, "Mật Khẩu Có Ít Nhất Là 6 Ký Tự", Toast.LENGTH_SHORT).show()
                return
            }
            data.createUserWithEmailAndPassword(email, matkhau).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //Đăng Ký Tài Khoản với Displayname
                    val user = FirebaseAuth.getInstance().currentUser
                    val profileUpdates = userProfileChangeRequest {
                        displayName = name
                    }
                    user!!.updateProfile(profileUpdates)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("NewMessage", "User profile updated.")
                            }
                        }
                    //
                    val ref = FirebaseDatabase.getInstance().getReference("/user/${user.uid}")
                    val Realtime = User(user.uid, user.email.toString(), name);
                    ref.setValue(Realtime)
                    STT(user.uid)
                    Log.d("COUNT", count.toString())
                    TOKEN(user.uid)
                    Loading.dismiss()
                    startActivity(Intent(this, LoginActivity::class.java))
                } else {
                    Loading.dismiss()
                    Loading = ProgressDialog(this)
                    Loading.setTitle("Lỗi")
                    Loading.setMessage("${task.exception?.message}")
                    Loading.show()
                }
            }
        } catch (e: Exception) {
            Loading.dismiss()
            Loading = ProgressDialog(this)
            Loading.setTitle("Lỗi")
            Loading.setMessage("${e.message}")
            Loading.show()
            return
        }
    }
}