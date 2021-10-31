package com.example.firebaseappchat.Post

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.core.view.accessibility.AccessibilityEventCompat.setAction
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.firebaseappchat.R
import com.example.firebaseappchat.databinding.FragmentDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import fragment.DashboardFragment
import java.text.SimpleDateFormat
import java.util.*

class PostActivity : AppCompatActivity() {
    private lateinit var ImgButton: ImageView
    private lateinit var ImgAdd: ImageView
    private lateinit var TxtStatus: TextView
    private lateinit var BtnDang: Button
    var selectPhotoUrl: Uri? = null
    private lateinit var Loading: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        ImgButton = findViewById(R.id.addimag)
        TxtStatus = findViewById(R.id.TxtSatus)
        BtnDang = findViewById(R.id.BtnDang)
        ImgAdd = findViewById(R.id.imageAdd)
        ImgButton.setOnClickListener {
            OpenGallery()
        }
    }

    private fun OpenGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectPhotoUrl = data.data
            //select images
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectPhotoUrl)
            ImgButton.setImageBitmap(bitmap)

            ImgAdd.isVisible = false
            BtnDang.setOnClickListener {
                Loading = ProgressDialog(this)
                Loading.setTitle("Đang Up Ảnh")
                Loading.setMessage("Xin Đợi Trong Giây Lát")
                Loading.show()
                UploadAnh()
            }
        }
    }

    private fun UploadAnh() {
        if (selectPhotoUrl == null) {
            Toast.makeText(this, "Vui Lòng Chọn Ảnh", Toast.LENGTH_SHORT).show()
            return
        }
        val filename = UUID.randomUUID().toString()

        val ref = FirebaseStorage.getInstance().getReference("Post/$filename")
        ref.putFile(selectPhotoUrl!!).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener {
                pushPostToDB(it.toString())
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun pushPostToDB(image: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val calendar = System.currentTimeMillis()
        val sdf1 = SimpleDateFormat("dd MMM yyyy")
        val sdf2 = SimpleDateFormat("hh:mm:ss")

        if (user != null) {
            updateuser(
                user.uid,
                sdf1.format(calendar),
                sdf2.format(calendar),
                TxtStatus.text.toString(),
                user.displayName.toString(),
                image
            )
            Toast.makeText(this, "Đăng Bài Thành Công. Hãy Quay Lại Để Xem Bài Viết", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateuser(
        uid: String,
        date: String,
        time: String,
        status: String,
        fullname: String,
        image: String
    ) {
        val user = mapOf<String, String>(
            "uid" to uid,
            "date" to date,
            "time" to time,
            "status" to status,
            "name" to fullname,
            "Urlphoto" to image
        )
        val database = FirebaseDatabase.getInstance().getReference("Post")
        val userdata = FirebaseAuth.getInstance().currentUser
        database.child(userdata?.uid.toString() + date + time).updateChildren(user)
            .addOnSuccessListener {
                Loading.dismiss()
                val dashboardFragment = DashboardFragment()
            }
    }
}