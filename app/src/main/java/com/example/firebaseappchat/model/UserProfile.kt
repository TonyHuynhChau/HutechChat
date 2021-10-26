package com.example.firebaseappchat.model

import android.app.Activity
import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import com.example.firebaseappchat.databinding.ActivityUserProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.util.*

class UserProfile : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    companion object{
        val IMGURL = "https://th.bing.com/th/id/R.502a73beb3f9263ca076457d525087c6?" +
                "rik=OP8RShVgw6uFhQ&riu=http%3a%2f%2fdvdn247.net%2fwp-content%2fuploads%2f2020%2f07%2" +
                "favatar-mac-dinh-1.png&ehk=NSFqDdL3jl9cMF3B9A4%2bzgaZX3sddpix%2bp7R%2bmTZHsQ%3d&risl=" +
                "&pid=ImgRaw&r=0"
    }
    private lateinit var binding: ActivityUserProfileBinding
    var year = 0
    var month = 0
    var day = 0

    var saveyear = 0
    var savemonth = 0
    var saveday = 0

    //Lấy uid Của Người Dùng Từ Authentication gồm (Email,Pass,Display,.....)
    val userdata = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Profile"

        if (userdata != null) {
            if (userdata.photoUrl != null) {
                readata(userdata.uid)
            }
        }

        binding.btnSelectImage.setOnClickListener() {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
        pickDate()
        binding.btnSave.setOnClickListener {
            val ifn = userdata?.photoUrl.toString()
            if (!ifn.equals("null")) {
                saveUserToRealtimeold()
            } else {
                saveUserToRealtimeForNoIMG(IMGURL)
            }
        }
    }

    private fun saveUserToRealtimeForNoIMG(profileImageUrl: String) {
        val fullName = binding.TxtName.text.toString()
        val birth = binding.TxtDate.text.toString()
        val sex = binding.TxtSex.text.toString()
        val phone = binding.TxtSDT.text.toString()
        val userIMG = profileImageUrl
        updateuser(fullName, birth, sex, phone, userIMG)
    }

    private fun readata(uid: String) {
        var database = FirebaseDatabase.getInstance().getReference("user")
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val name = dataSnapshot.child("$uid/name").value
                val date = dataSnapshot.child("$uid/Date").value
                val phonenumber = dataSnapshot.child("$uid/Phone").value
                val Sex = dataSnapshot.child("$uid/Sex").value
                val photo = dataSnapshot.child("$uid/Urlphoto").value

                Picasso.get().load(photo.toString()).into(select_images)
                binding.TxtName.text = Editable.Factory.getInstance().newEditable(name.toString())
                binding.TxtDate.text = Editable.Factory.getInstance().newEditable(date.toString())
                binding.TxtSex.text = Editable.Factory.getInstance().newEditable(Sex.toString())
                binding.TxtSDT.text = Editable.Factory.getInstance().newEditable(phonenumber.toString())
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("loadPost:onCancelled", error.toException())
            }
        }
        database.addValueEventListener(postListener)
    }

    private fun pickDate() {
        binding.TxtDate.setOnClickListener {
            getDateTimeCalendar()
            DatePickerDialog(this, this, year, month, day).show()
        }
    }

    private fun getDateTimeCalendar() {
        val calendar: Calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        day = calendar.get(Calendar.DAY_OF_MONTH)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        saveday = dayOfMonth
        savemonth = month + 1
        saveyear = year
        var Datepick = "$saveday-$savemonth-$saveyear"
        binding.TxtDate.setText(Datepick)
    }


    var selectPhotoUrl: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectPhotoUrl = data.data
            //select images
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectPhotoUrl)
            binding.selectImages.setImageBitmap(bitmap)

            binding.btnSelectImage.setText("")
            binding.btnSave.setOnClickListener {
                updateImages()
            }

        } else {
            Toast.makeText(this, "TEST", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateImages() {
        if (selectPhotoUrl == null) {
            Toast.makeText(this, "Vui Lòng Chọn Ảnh", Toast.LENGTH_SHORT).show()
            return
        }

        //code thành công tới lưu vào storage images
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/Images/$filename")
        ref.putFile(selectPhotoUrl!!).addOnSuccessListener {
            //Lấy URL Của Ảnh
            ref.downloadUrl.addOnSuccessListener {
                photoUrl = it
                saveUserToRealtime(it.toString())
                Toast.makeText(this, "Lưu Thành Công", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Lỗi :" + it.toString(), Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Lỗi :" + it.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    var photoUrl: Uri? = null
    private fun saveUserToRealtime(profileImageUrl: String) {
        val fullName = binding.TxtName.text.toString()
        val birth = binding.TxtDate.text.toString()
        val sex = binding.TxtSex.text.toString()
        val phone = binding.TxtSDT.text.toString()
        val userIMG = profileImageUrl
        updateuser(fullName, birth, sex, phone, userIMG)


    }

    private fun saveUserToRealtimeold() {
        val fullName = binding.TxtName.text.toString()
        val birth = binding.TxtDate.text.toString()
        val sex = binding.TxtSex.text.toString()
        val phone = binding.TxtSDT.text.toString()
        val userIMG = userdata?.photoUrl.toString()
        updateuser(fullName, birth, sex, phone, userIMG)
    }

    //Cập Nhật Lại Thông Tin Người Dùng Trên Realtime
    private fun updateuser(
        fullName: String,
        birth: String,
        sex: String,
        phone: String,
        userIMG: String
    ) {
        var database = FirebaseDatabase.getInstance().getReference("user")
        //Cách Cập Nhật Vào Authentication
        val profileUpdates = userProfileChangeRequest {
            displayName = fullName
            if (photoUrl == null) {
                if (userdata != null) {
                    photoUri = userdata.photoUrl
                }
            } else {
                photoUri = photoUrl
            }

        }
        userdata!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("NewMessage", "User profile updated.")
                }
            }
        //Cách Cập Nhật vào Realtime
        var user = mapOf<String, String>(
            "name" to fullName,
            "Date" to birth,
            "Sex" to sex,
            "Phone" to phone,
            "Urlphoto" to userIMG
        )
        database.child(userdata?.uid.toString()).updateChildren(user).addOnSuccessListener {
            binding.TxtName.text.clear()
            binding.TxtDate.text = ""
            binding.TxtSex.text.clear()
            binding.TxtSDT.text.clear()
            Toast.makeText(this, "Cập Nhật Thành Công", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Lỗi" + it.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}