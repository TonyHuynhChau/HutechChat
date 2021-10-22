package com.example.firebaseappchat.Friend

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.firebaseappchat.R
import com.example.firebaseappchat.databinding.ActivityProfileOtherUserBinding
import com.example.firebaseappchat.registerlogin.SignUpActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class Profile_Other_User_Activity : AppCompatActivity() {
    private lateinit var img: ImageView
    private lateinit var Ten: TextView
    private lateinit var btnSendRequestFriends: Button
    private lateinit var btnMessage: Button
    private lateinit var Email: TextView
    private lateinit var FriendsRequest: DatabaseReference
    private var Type = "Đã Hủy"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_other_user)

        img = findViewById(R.id.Avatar)
        Ten = findViewById(R.id.TxtName_Other_User)
        Email = findViewById(R.id.TxtEmail_Other_User)
        btnSendRequestFriends = findViewById(R.id.btnSendRequest)
        btnMessage = findViewById(R.id.btnMessage)
        btnMessage.isEnabled = false
        FriendsRequest = FirebaseDatabase.getInstance().getReference("FriendsRequest")
        val user = intent.getParcelableExtra<SignUpActivity.getUser>("USER_KEY")

        if (user != null) {
            Picasso.get().load(user.Urlphoto).into(img)
            Ten.setText(user.name)
            Email.setText(user.email)
            KnowSent(user.uid)
            btnSendRequestFriends.setOnClickListener(View.OnClickListener {
                if (Type == "Đã Hủy") {
                    btnSendRequestFriends.setText("Gửi Yêu Cầu Kết Bạn")
                    sendrequestfriends(user.uid)
                }
                if (Type == "Đã Gửi") {
                    btnSendRequestFriends.setText("Hủy Yêu Cầu")
                    HuyKetBan(user.uid)
                }
            })
        }
    }

    private fun HuyKetBan(receiverUserid: String) {

        val userNguoiDung = FirebaseAuth.getInstance().currentUser
        if (userNguoiDung != null) {
            FriendsRequest.child(receiverUserid).child(userNguoiDung.uid).child("request_type")
                .setValue("recall").addOnCompleteListener(OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        FriendsRequest.child(userNguoiDung.uid).child(receiverUserid)
                            .child("request_type")
                            .setValue("cancel").addOnCompleteListener(OnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Type = "Đã Hủy"
                                    btnSendRequestFriends.setText("Gửi Yêu Cầu Kết Bạn")
                                }
                            })
                    }
                })
        }
    }

    private fun KnowSent(receiverUserid: String) {
        val userNguoiDung = FirebaseAuth.getInstance().currentUser
        if (userNguoiDung != null) {

            FriendsRequest.child(userNguoiDung.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.hasChild(receiverUserid)) {

                            var Request_type =
                                snapshot.child(receiverUserid).child("request_type").value

                            if (Request_type != null) {
                                if (Request_type.equals("received")) {
                                    Type = "Đã Gửi"
                                    btnSendRequestFriends.setText("Hủy Yêu Cầu")
                                }
                                if (Request_type.equals("recall")) {
                                    Type = "Đã Hủy"
                                    btnSendRequestFriends.setText("Gửi Yêu Cầu Kết Bạn")
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("Error", error.message)
                    }

                })

        }
    }

    // "Đã Hủy Kết Bạn"
    private fun sendrequestfriends(receiverUserid: String) {

        val userNguoiDung = FirebaseAuth.getInstance().currentUser
        if (userNguoiDung != null) {
            FriendsRequest.child(receiverUserid).child(userNguoiDung.uid).child("request_type")
                .setValue("sent").addOnCompleteListener(OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        FriendsRequest.child(userNguoiDung.uid).child(receiverUserid)
                            .child("request_type")
                            .setValue("received").addOnCompleteListener(OnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Type = "Đã Gửi"
                                    btnSendRequestFriends.setText("Hủy Yêu Cầu")
                                }
                            })
                    }
                })
        }
    }
}