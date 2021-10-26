package com.example.firebaseappchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firebaseappchat.databinding.ActivityNewMessBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.util.Log
import com.example.firebaseappchat.messages.ChatLogActivity
import com.example.firebaseappchat.registerlogin.LoginActivity
import com.example.firebaseappchat.registerlogin.SignUpActivity
import com.squareup.picasso.Picasso
import com.xwray.groupie.*
import kotlinx.android.synthetic.main.user_row.view.*

class NewMessActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewMessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewMessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Chọn Người Muốn Nhắn"

        verifyUserLoggedIn()

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
                    snapshot.children.forEach() {
                        val FriendUID = it.child("uid").value
                        Log.d("FriendsUID",FriendUID.toString())
                        val ref = FirebaseDatabase.getInstance().getReference("user")
                        ref.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val adapter = GroupAdapter<GroupieViewHolder>()
                                snapshot.children.forEach() {
                                    val user = it.getValue(SignUpActivity.getUser::class.java)

                                    if (userNguoiDung != null) {
                                        if (user != null) {
                                            if (userNguoiDung.uid != user.uid && user.uid==FriendUID) {
                                                adapter.add(UItem(user))
                                            }
                                        }
                                    }

                                }
                                adapter.setOnItemClickListener { item, view ->
                                    val userItem = item as UItem
                                    val intent = Intent(view.context, ChatLogActivity::class.java)
                                    Log.d("New Message:", USER_KEY)
                                    intent.putExtra(USER_KEY, userItem.user)
                                    startActivity(intent)
                                    finish()
                                }
                                binding.recyclerviewnewmess.adapter = adapter
                            }
                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }


    //Check User Login
    private fun verifyUserLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}


//Connect user_row to recycler view
class UItem(val user: SignUpActivity.getUser) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.user_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        if (user == null) {
            Log.d("Error Message", "User Name = ${user.name}")
            return
        } else if (user.Urlphoto.toString().isEmpty()) {
            val ImgDefault = "https://th.bing.com/th/id/R.502a73beb3f9263ca076457d525087c6?" +
                    "rik=OP8RShVgw6uFhQ&riu=http%3a%2f%2fdvdn247.net%2fwp-content%2fuploads%2f2020%2f07%2" +
                    "favatar-mac-dinh-1.png&ehk=NSFqDdL3jl9cMF3B9A4%2bzgaZX3sddpix%2bp7R%2bmTZHsQ%3d&risl=" +
                    "&pid=ImgRaw&r=0"
            viewHolder.itemView.TxtUserName.text = user.name + "(Need Update Profile)"
            Picasso.get().load(ImgDefault).into(viewHolder.itemView.iVUser)
        }

        if (user.equals("null")) {
            Log.d("Error Message", "User Name = ${user.name}")
            return
        } else if (user.Urlphoto.toString().isEmpty()) {
            val ImgDefault = "https://th.bing.com/th/id/R.502a73beb3f9263ca076457d525087c6?" +
                    "rik=OP8RShVgw6uFhQ&riu=http%3a%2f%2fdvdn247.net%2fwp-content%2fuploads%2f2020%2f07%2" +
                    "favatar-mac-dinh-1.png&ehk=NSFqDdL3jl9cMF3B9A4%2bzgaZX3sddpix%2bp7R%2bmTZHsQ%3d&risl=" +
                    "&pid=ImgRaw&r=0"
            viewHolder.itemView.TxtUserName.text = user.name + "(Need Update Profile)"
            Picasso.get().load(ImgDefault).into(viewHolder.itemView.iVUser)
        } else {
            viewHolder.itemView.TxtUserName.text = user.name

            Picasso.get().load(user.Urlphoto).into(viewHolder.itemView.iVUser)
        }
    }
}
