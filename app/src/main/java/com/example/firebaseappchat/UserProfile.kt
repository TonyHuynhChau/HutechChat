package com.example.firebaseappchat

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.widget.Toast
import com.example.firebaseappchat.databinding.ActivityUserProfileBinding
import com.google.firebase.auth.ActionCodeUrl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.lang.Exception
import java.net.URI
import java.util.*

class UserProfile : AppCompatActivity() {
    private lateinit var binding : ActivityUserProfileBinding
    private lateinit var data : FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Profile"

        binding.btnSelectImage.setOnClickListener(){
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

    }

    var selectPhotoUrl: Uri?=null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            selectPhotoUrl = data.data
            //select images
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectPhotoUrl)
            binding.selectImages.setImageBitmap(bitmap)
            //val bitmapDrawable = BitmapDrawable(bitmap)
            //binding.btnSelectImage.setBackgroundDrawable(bitmapDrawable)
            binding.btnSelectImage.setText("")
            //Click button to save images and info user
            binding.btnSave.setOnClickListener(){
                updateImages()
            }

        }
    }

    private fun updateImages(){
        if(selectPhotoUrl == null) {
            Toast.makeText(this,"Vui Lòng Chọn Ảnh",Toast.LENGTH_SHORT).show()
            return
        }

        //code thành công tới lưu vào storage images
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/Images/$filename")

        ref.putFile(selectPhotoUrl!!).addOnSuccessListener {
            Toast.makeText(this,"Lưu Ảnh Thành Công",Toast.LENGTH_SHORT).show()
        }

        // Add images and infomation user to Realtime DB Firebase
        ref.downloadUrl.addOnSuccessListener {
            saveUserToDB(it.toString())
            Toast.makeText(this,"Lưu Thông Tin Thành Công",Toast.LENGTH_SHORT).show()
            binding.TxtName.setText("")
            binding.TxtSDT.setText("")
            binding.TxtSex.setText("")
            binding.TxtName.setText("")
            binding.TxtDate.setText("")
        }
            .addOnFailureListener{
                Toast.makeText(this,"Lưu Thông Tin Thất Bại",Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUserToDB(profileImageUrl: String){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("user/$uid")

        val fullName = binding.TxtName.text.toString()
        val birth = binding.TxtDate.text.toString()
        val sex = binding.TxtSex.text.toString()
        val phone = binding.TxtSDT.text.toString()
        val user = UserPic(profileImageUrl)

        //val user = UserPic(fullName, birth, sex, phone, profileImageUrl)
        ref.setValue(user)
    }

    //class UserPic(val userName: String, val date:String, val sex:String, val phone:String, val url:String)
    class UserPic(val url:String)

}