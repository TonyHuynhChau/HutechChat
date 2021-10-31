package com.example.firebaseappchat.Post

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.firebaseappchat.NewMessActivity.Companion.USER_KEY
import com.example.firebaseappchat.R
import com.example.firebaseappchat.messages.MainActivity
import com.example.firebaseappchat.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.clickpost.*
import java.text.SimpleDateFormat
import java.util.*

class ClickPost : AppCompatActivity() {
    val userdata = FirebaseAuth.getInstance().currentUser
    var toUser: Post? = null
    var Anh: String? = ""
    var selectPhotoUrl: Uri? = null
    lateinit var ImgUser: ImageView
    lateinit var AddAnh: ImageView
    lateinit var XoaAnh: ImageView
    lateinit var Description: EditText
    lateinit var Post_username: TextView
    lateinit var Post_image: ImageView
    private lateinit var Loading: ProgressDialog

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.clickpost)
        supportActionBar?.hide()
        toUser = intent.getParcelableExtra(USER_KEY)
        Anh = intent.getStringExtra("Anh Nguoi Dung")
        Find()
        load()
        AddAnh.isVisible = false
        var BtnSua = findViewById<Button>(R.id.BtnSuaBai)
        var BtnXoa = findViewById<Button>(R.id.BtnXoaBai)
        var type = ""
        XoaAnh.setOnClickListener {
            type = "Xoa Anh"
            Post_image.isVisible = false
            AddAnh.isVisible = true
        }
        Post_image.setOnClickListener {
            OpenGallery()
        }
        AddAnh.setOnClickListener {
            OpenGallery()
        }
        BtnSua.setOnClickListener {
            Loading = ProgressDialog(this)
            Loading.setTitle("Đang Sửa Bài")
            Loading.setMessage("Xin Đợi Trong Giây Lát")
            Loading.show()
            val calendar = System.currentTimeMillis()
            val sdf1 = SimpleDateFormat("dd MMM yyyy")
            val sdf2 = SimpleDateFormat("hh:mm:ss")
            if (type == "Xoa Anh") {
                SuaBai(
                    userdata?.displayName.toString(),
                    userdata?.uid.toString(),
                    Description.text.toString(),
                    sdf1.format(calendar),
                    sdf2.format(calendar),
                    ""
                )
            } else {
                SuaBai(
                    userdata?.displayName.toString(),
                    userdata?.uid.toString(),
                    Description.text.toString(),
                    sdf1.format(calendar),
                    sdf2.format(calendar),
                    toUser?.Urlphoto.toString()
                )
            }
        }
        BtnXoa.setOnClickListener {
            Loading = ProgressDialog(this)
            Loading.setTitle("Đang Xóa Bài")
            Loading.setMessage("Xin Đợi Trong Giây Lát")
            Loading.show()
            XoaBai()
        }
    }

    private fun XoaBai() {
        var database = FirebaseDatabase.getInstance().getReference("Post")
        database.child(userdata?.uid.toString() + toUser?.date + toUser?.time).removeValue()
            .addOnSuccessListener {
                Loading.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
            }
    }


    private fun OpenGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
        Post_image.isVisible = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectPhotoUrl = data.data
            //select images
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectPhotoUrl)
            Post_image.setImageBitmap(bitmap)
            BtnSuaBai.setOnClickListener {
                Loading = ProgressDialog(this)
                Loading.setTitle("Đang Sửa Bài")
                Loading.setMessage("Xin Đợi Trong Giây Lát")
                Loading.show()
                UploadAnh()
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun UploadAnh() {
        if (selectPhotoUrl == null) {
            Toast.makeText(this, "Vui Lòng Chọn Ảnh", Toast.LENGTH_SHORT).show()
            return
        }
        val filename = UUID.randomUUID().toString()

        val ref = FirebaseStorage.getInstance().getReference("Post/$filename")
        ref.putFile(selectPhotoUrl!!).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener {
                val calendar = System.currentTimeMillis()
                val sdf1 = SimpleDateFormat("dd MMM yyyy")
                val sdf2 = SimpleDateFormat("hh:mm:ss")
                SuaBai(
                    userdata?.displayName.toString(),
                    userdata?.uid.toString(),
                    Description.text.toString(),
                    sdf1.format(calendar),
                    sdf2.format(calendar), it.toString()
                )
            }
        }
    }

    private fun SuaBai(
        Name: String,
        uid: String,
        status: String,
        date: String,
        time: String,
        Urlphoto: String
    ) {
        var database = FirebaseDatabase.getInstance().getReference("Post")
        database.child(userdata?.uid.toString() + toUser?.date + toUser?.time).removeValue()
            .addOnSuccessListener {
                //Cách Cập Nhật vào Realtime
                var user = mapOf(
                    "name" to Name,
                    "uid" to uid,
                    "status" to status,
                    "date" to date,
                    "time" to time,
                    "Urlphoto" to Urlphoto,
                )
                database.child(userdata?.uid.toString() + date + time)
                    .updateChildren(user)
                    .addOnSuccessListener {
                        Loading.dismiss()
                        startActivity(Intent(this, MainActivity::class.java))
                    }.addOnFailureListener {
                        Toast.makeText(this, "Lỗi" + it.toString(), Toast.LENGTH_SHORT).show()
                    }
            }

    }

    fun Find() {
        ImgUser = findViewById(R.id.click_post_profile_image)
        Post_username = findViewById(R.id.click_post_username)
        Description = findViewById(R.id.click_post_description)
        Post_image = findViewById(R.id.click_post_image)
        XoaAnh = findViewById(R.id.XoaAnh)
        AddAnh = findViewById(R.id.AddAnh)
    }

    private fun load() {
        Post_username.text = toUser?.name
        Picasso.get().load(Anh).into(ImgUser)
        Description.text = Editable.Factory.getInstance().newEditable(toUser?.status.toString())
        Picasso.get().load(toUser?.Urlphoto).into(Post_image)
    }
}