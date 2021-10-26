package com.example.firebaseappchat.Friend


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.firebaseappchat.NewMessActivity
import com.example.firebaseappchat.Notification.FirebaseService
import com.example.firebaseappchat.Notification.NotificationData
import com.example.firebaseappchat.Notification.PushNotification
import com.example.firebaseappchat.Notification.RetrofitInstance
import com.example.firebaseappchat.R
import com.example.firebaseappchat.UItem
import com.example.firebaseappchat.messages.ChatLogActivity
import com.example.firebaseappchat.registerlogin.SignUpActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val TOPIC = "/topics/myTopic"

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
            if (user.Urlphoto.isEmpty()) {
                val defaultIMG = "https://th.bing.com/th/id/R.502a73beb3f9263ca076457d525087c6?" +
                        "rik=OP8RShVgw6uFhQ&riu=http%3a%2f%2fdvdn247.net%2fwp-content%2fuploads%2f2020%2f07%2" +
                        "favatar-mac-dinh-1.png&ehk=NSFqDdL3jl9cMF3B9A4%2bzgaZX3sddpix%2bp7R%2bmTZHsQ%3d&risl=" +
                        "&pid=ImgRaw&r=0"
                Picasso.get().load(defaultIMG).into(img)
            } else {
                Picasso.get().load(user.Urlphoto).into(img)
            }

            Ten.setText(user.name)
            Email.setText(user.email)
            KnowSent(user.uid)
            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

            btnSendRequestFriends.setOnClickListener(View.OnClickListener {
                if (Type == "Đã Hủy") {
                    sendrequestfriends(user.uid)
                }
                if (Type == "Đã Gửi") {
                    HuyKetBan(user.uid)
                }
            })
        }
    }

    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
                if (response.isSuccessful) {
                    Log.d("ProfileOtherUseActivity", "Response: ${Gson().toJson(response)}")
                } else {
                    Log.e("ProfileOtherUseActivity", response.errorBody().toString())
                }
            } catch (e: Exception) {
                Log.e("ProfileOtherUseActivity", e.toString())
            }
        }

    private fun HuyKetBan(receiverUserid: String) {
        val userNguoiDung = FirebaseAuth.getInstance().currentUser
        if (userNguoiDung != null) {
            FriendsRequest.child("Nhận " + receiverUserid).child("Gửi " + userNguoiDung.uid)
                .removeValue().addOnCompleteListener(OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        FriendsRequest.child("Gửi " + userNguoiDung.uid)
                            .child("Nhận " + receiverUserid)
                            .removeValue()
                            .addOnCompleteListener(OnCompleteListener { task ->
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
            val Friends = FirebaseDatabase.getInstance().getReference("/Friends")
            Friends.child(userNguoiDung.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var Request_type = snapshot.child(receiverUserid).value
                        if (Request_type != null) {
                            if (Request_type.equals("Bạn")) {
                                btnSendRequestFriends.isVisible = false
                                btnMessage.isEnabled = true
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }

        if (userNguoiDung != null) {
            FriendsRequest.child("Gửi " + userNguoiDung.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.hasChild("Nhận " + receiverUserid)) {
                            val Request_type =
                                snapshot.child("Nhận " + receiverUserid)
                                    .child("request_type").value
                            if (Request_type != null) {
                                if (Request_type.equals("Đã Nhận")) {
                                    Type = "Đã Gửi"
                                    btnSendRequestFriends.setText("Hủy Yêu Cầu")
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("Error", error.message)
                    }

                })

            FriendsRequest.child("Nhận " + userNguoiDung.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.hasChild("Gửi " + receiverUserid)) {
                            var Request_type =
                                snapshot.child("Gửi " + receiverUserid)
                                    .child("request_type").value
                            if (Request_type != null) {
                                if (Request_type.equals("Đã Gửi")) {
                                    Type = "Đã Yêu Cầu"
                                    btnSendRequestFriends.setText("Đồng Ý Kết Bạn")
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

    private fun sendrequestfriends(receiverUserid: String) {
        val userNguoiDung = FirebaseAuth.getInstance().currentUser
        if (userNguoiDung != null) {
            //Thêm Thông Tin Cho Người Nhận
            FriendsRequest.child("Nhận " + receiverUserid).child("Gửi " + userNguoiDung.uid)
                .child("request_type")
                .setValue("Đã Gửi").addOnCompleteListener(OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //Thêm Tên Người Gửi vào Friend Request Của Người Nhận
                        FriendsRequest.child("Nhận " + receiverUserid)
                            .child("Gửi " + userNguoiDung.uid)
                            .child("name").setValue(userNguoiDung.displayName)
                            .addOnCompleteListener(
                                OnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.d("New Message", "Đã Thêm Tên Người Gửi")
                                        //Thêm Ảnh Người Gửi vào Friend Request Của Người Nhận
                                        if (userNguoiDung.photoUrl == null) {
                                            val IMGURL =
                                                "https://th.bing.com/th/id/R.502a73beb3f9263ca076457d525087c6?" +
                                                        "rik=OP8RShVgw6uFhQ&riu=http%3a%2f%2fdvdn247.net%2fwp-content%2fuploads%2f2020%2f07%2" +
                                                        "favatar-mac-dinh-1.png&ehk=NSFqDdL3jl9cMF3B9A4%2bzgaZX3sddpix%2bp7R%2bmTZHsQ%3d&risl=" +
                                                        "&pid=ImgRaw&r=0"
                                            FriendsRequest.child("Nhận " + receiverUserid)
                                                .child("Gửi " + userNguoiDung.uid)
                                                .child("Urlphoto").setValue(IMGURL)
                                                .addOnCompleteListener(
                                                    OnCompleteListener { task ->
                                                        if (task.isSuccessful) {

                                                        }
                                                    })
                                        } else {
                                            FriendsRequest.child("Nhận " + receiverUserid)
                                                .child("Gửi " + userNguoiDung.uid)
                                                .child("Urlphoto")
                                                .setValue(userNguoiDung.photoUrl.toString())
                                                .addOnCompleteListener(
                                                    OnCompleteListener { task ->
                                                        if (task.isSuccessful) {

                                                        }
                                                    })
                                        }
                                    }
                                })
                        FriendsRequest.child("Nhận " + receiverUserid)
                            .child("Gửi " + userNguoiDung.uid)
                            .child("uid").setValue(userNguoiDung.uid)
                            .addOnCompleteListener(OnCompleteListener { task ->
                                if (task.isSuccessful) {
                                }
                            })
                        //Thêm Thông Tin Cho Người Gửi
                        FriendsRequest.child("Gửi " + userNguoiDung.uid)
                            .child("Nhận " + receiverUserid)
                            .child("request_type")
                            .setValue("Đã Nhận").addOnCompleteListener(OnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Type = "Đã Gửi"
                                    btnSendRequestFriends.setText("Hủy Yêu Cầu")
                                    FindToken(receiverUserid, userNguoiDung.displayName.toString())
                                }
                            })
                    }
                })
        }
    }

    private fun FindToken(uid: String, displayName: String) {
        val ref = FirebaseDatabase.getInstance().getReference("/user")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val token = snapshot.child("$uid/Token").value
                Log.d("TOKENNNNNN", token.toString())
                val Title = "Lời Mời Kết Bạn"
                val Message =
                    "$displayName Đã Gửi Yêu Cầu Kết Bạn"
                PushNotification(
                    NotificationData(Title, Message),
                    token.toString()
                ).also { sendNotification(it) }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}