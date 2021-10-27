package com.example.firebaseappchat.VideoCall

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firebaseappchat.R
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.view.SurfaceView;
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout;
import java.util.*
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.firebaseappchat.UItem
import com.example.firebaseappchat.messages.ChatLogActivity
import com.example.firebaseappchat.registerlogin.SignUpActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_video_chat.*


class VideoChatActivity : AppCompatActivity() {

    private lateinit var swipeCall: SwipeRefreshLayout
    private lateinit var callRecyclerView: RecyclerView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.firebaseappchat.R.layout.activity_video_chat)
        this.swipeCall = findViewById<View>(com.example.firebaseappchat.R.id.swipeRefresh_call) as SwipeRefreshLayout
        this.callRecyclerView = findViewById<View>(com.example.firebaseappchat.R.id.recyclerview_latest_messages_call) as RecyclerView

        swipeCall.setOnRefreshListener {
            Handler().postDelayed(Runnable {
                swipeCall.isRefreshing = false
            }, 4000)
        }

        LayUser()
    }

    companion object {
        var USER_KEY = "USER_KEY"
    }

    // Get user name and images then show in user_row
    private fun LayUser() {
        val userNguoiDung = FirebaseAuth.getInstance().currentUser
        if (userNguoiDung != null) {
            val Friends = FirebaseDatabase.getInstance().getReference("Friends")
            Friends.child(userNguoiDung.uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val adapter = GroupAdapter<GroupieViewHolder>()
                    snapshot.children.forEach() {
                        val FriendUID = it.child("uid").value
                        Log.d("FriendsUID", FriendUID.toString())
                        val ref = FirebaseDatabase.getInstance().getReference("user")
                        ref.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                snapshot.children.forEach() {
                                    val user = it.getValue(SignUpActivity.getUser::class.java)
                                    if (user != null) {
                                        if (userNguoiDung.uid != user.uid && FriendUID == user.uid) {
                                            adapter.add(UItem(user))
                                        }
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })
                    }
                    recyclerview_latest_messages_call.adapter = adapter
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

}