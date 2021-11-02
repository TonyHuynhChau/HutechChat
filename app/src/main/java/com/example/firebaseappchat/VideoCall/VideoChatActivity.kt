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
import java.util.*
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.widget.*
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.firebaseappchat.NewMessActivity
import com.example.firebaseappchat.UItem
import com.example.firebaseappchat.messages.ChatLogActivity
import com.example.firebaseappchat.messages.MainActivity
import com.example.firebaseappchat.registerlogin.LoginActivity
import com.example.firebaseappchat.registerlogin.SignUpActivity
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import de.hdodenhof.circleimageview.CircleImageView
import fragment.HomeFragment
import kotlinx.android.synthetic.main.activity_video_chat.*
import kotlinx.android.synthetic.main.user_row.view.*
import kotlinx.android.synthetic.main.user_row_call.*
import kotlinx.android.synthetic.main.user_row_call.view.*
import kotlin.jvm.internal.Ref


class VideoChatActivity : AppCompatActivity() {

    private lateinit var swipeCall: SwipeRefreshLayout
    private lateinit var currentUserId: String
    private lateinit var ref: DatabaseReference
    private lateinit var calledBy: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.firebaseappchat.R.layout.activity_video_chat)
        this.swipeCall = findViewById<View>(R.id.swipeRefresh_call) as SwipeRefreshLayout

        currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        swipeCall.setOnRefreshListener {
            Handler().postDelayed(Runnable {
                swipeCall.isRefreshing = false
            }, 4000)
        }
        //Load Friend User
        verifyUserLoggedIn()
        LayUser()

        //Call Event
        validateUser()
        checkForReceivingCall()
    }

    private fun checkForReceivingCall() {
        ref.child(currentUserId).child("ringing")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild("ringing")) {
                        calledBy = snapshot.child("ringing").value.toString()
                        val intent = Intent(this@VideoChatActivity, Outcoming::class.java)
                        intent.putExtra(USER_KEY, calledBy)
                        startActivity(intent)
                        finish()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun validateUser() {
        ref = FirebaseDatabase.getInstance().getReference()

        ref.child("user").child(currentUserId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // TODO: 10/28/2021
            }

            override fun onCancelled(error: DatabaseError) {
                val intent = Intent(this@VideoChatActivity, VideoChatActivity::class.java)
                startActivity(intent)
                finish()
                Toast.makeText(this@VideoChatActivity, "Gọi Thất Bại", Toast.LENGTH_SHORT).show()
            }

        })
    }


    private fun verifyUserLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
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
                                            adapter.add(CallItem(user))
                                        }
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(
                                    this@VideoChatActivity,
                                    "Gọi Thất Bại",
                                    Toast.LENGTH_SHORT
                                ).show()
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

    //Connect user_row to recycler view
    class CallItem(val user: SignUpActivity.getUser) : Item<GroupieViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.user_row_call
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            if (user.equals("null")) {
                Log.d("Error Call", "User Name = ${user.name}")
                return
            } else if (user.Urlphoto.isEmpty()) {
                val ImgDefault = "https://th.bing.com/th/id/R.502a73beb3f9263ca076457d525087c6?" +
                        "rik=OP8RShVgw6uFhQ&riu=http%3a%2f%2fdvdn247.net%2fwp-content%2fuploads%2f2020%2f07%2" +
                        "favatar-mac-dinh-1.png&ehk=NSFqDdL3jl9cMF3B9A4%2bzgaZX3sddpix%2bp7R%2bmTZHsQ%3d&risl=" +
                        "&pid=ImgRaw&r=0"
                viewHolder.itemView.TxtUserName_call.text = user.name + "(Need Update Profile)"
                Picasso.get().load(ImgDefault).into(viewHolder.itemView.iVUser_call)
            } else {
                viewHolder.itemView.TxtUserName_call.text = user.name
                Picasso.get().load(user.Urlphoto).into(viewHolder.itemView.iVUser_call)
            }
            viewHolder.itemView.btn_VideoCall.setOnClickListener() {
                val intent = Intent(viewHolder.itemView.context, Outcoming::class.java)
                intent.putExtra(USER_KEY, user)
                viewHolder.itemView.context.startActivity(intent)
            }
        }
    }
}



