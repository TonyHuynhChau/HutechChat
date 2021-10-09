package com.example.firebaseappchat

import android.app.Activity
import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import com.example.firebaseappchat.databinding.ActivityUserProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class UserProfile : AppCompatActivity(),DatePickerDialog.OnDateSetListener {
    private lateinit var binding : ActivityUserProfileBinding
    var year = 0
    var month = 0
    var day = 0

    var saveyear = 0
    var savemonth = 0
    var saveday = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Profile"

        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            binding.TxtName.text = Editable.Factory.getInstance().newEditable(user.displayName.toString())
        }

        binding.btnSelectImage.setOnClickListener(){
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
        pickDate()



    }

    private fun pickDate() {
        binding.TxtDate.setOnClickListener{
            getDateTimeCalendar()
            DatePickerDialog(this,this,year,month,day).show()
        }
    }

    private fun getDateTimeCalendar(){
        val calendar : Calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        day = calendar.get(Calendar.DAY_OF_MONTH)
    }
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        saveday = dayOfMonth
        savemonth = month+1
        saveyear = year

        var Datepick = "$saveday-$savemonth-$saveyear"
        binding.TxtDate.setText(Datepick)
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
            binding.btnSave.setOnClickListener{
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
            //Lấy URL Của Ảnh
            ref.downloadUrl.addOnSuccessListener {
            // binding.TxtName.text = Editable.Factory.getInstance().newEditable(it.toString())
                photoUrl = it
                saveUserToRealtime(it.toString())
            }.addOnFailureListener{
                Toast.makeText(this,"Lỗi :"+it.toString(),Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this,"Lỗi :"+it.toString(),Toast.LENGTH_SHORT).show()
        }
    }

    lateinit var photoUrl :Uri
    private fun saveUserToRealtime(profileImageUrl: String){

        var NguoiDung = FirebaseAuth.getInstance().currentUser
        val ref = FirebaseDatabase.getInstance().getReference("user/${NguoiDung?.uid.toString()}")

        val fullName = binding.TxtName.text.toString()
        val birth = binding.TxtDate.text.toString()
        val sex = binding.TxtSex.text.toString()
        val phone = binding.TxtSDT.text.toString()
        val userIMG = profileImageUrl
        updateuser(fullName,birth,sex,phone,userIMG)
    }

    //Cập Nhật Lại Thông Tin Người Dùng Trên Realtime
    private fun updateuser(fullName: String, birth: String, sex: String, phone: String, userIMG: String) {
        var database = FirebaseDatabase.getInstance().getReference("user")
        //Lấy uid Của Người Dùng Từ Authentication gồm (Email,Pass,Display,.....)
        var NguoiDung = FirebaseAuth.getInstance().currentUser
        //Cách Cập Nhật Vào Authentication
        val profileUpdates = userProfileChangeRequest {
            displayName = fullName
            photoUri = photoUrl
        }
        NguoiDung!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("NewMessage", "User profile updated.")
                }
            }
        //Cách Cập Nhật vào Realtime
        var user = mapOf<String,String>(
                "name" to fullName,
                "Date" to birth,
                "Sex" to sex,
                "Phone" to phone,
                "Urlphoto" to userIMG
        )
        database.child(NguoiDung?.uid.toString()).updateChildren(user).addOnSuccessListener {
            binding.TxtName.text.clear()
            binding.TxtDate.text = ""
            binding.TxtSex.text.clear()
            binding.TxtSDT.text.clear()
            Toast.makeText(this,"Cập Nhật Thành Công",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(this,"Lỗi"+it.toString(),Toast.LENGTH_SHORT).show()
        }
    }
}