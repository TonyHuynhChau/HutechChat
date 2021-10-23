package com.example.firebaseappchat.PageProfile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseappchat.Friend.Profile_Other_User_Activity
import com.example.firebaseappchat.R
import com.example.firebaseappchat.SearchUser.SearchUserActivity
import com.example.firebaseappchat.registerlogin.SignUpActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.layout_list_find.view.*
import kotlinx.android.synthetic.main.layout_list_find.view.IVUser
import kotlinx.android.synthetic.main.layout_thong_bao.view.*

class ThongBaoActivity : AppCompatActivity() {
    private lateinit var recyclervew: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thong_bao)
        recyclervew = findViewById(R.id.List_Thong_Bao)
        RequesrFriend()
    }

    private fun RequesrFriend() {
        val userNguoiDung = FirebaseAuth.getInstance().currentUser
        val FirebaseDb = FirebaseDatabase.getInstance().getReference("/FriendsRequest")
        if (userNguoiDung != null) {
            FirebaseDb.child("Nhận ${userNguoiDung.uid}")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val adapter = GroupAdapter<GroupieViewHolder>()
                        snapshot.children.forEach() {
                            val user = it.getValue(SignUpActivity.getUser::class.java)!!
                            adapter.add(UItem(user))

                        }
                        adapter.setOnItemClickListener { item, view ->

                            val userItem = item as UItem

                            val intent =
                                Intent(view.context, Profile_Other_User_Activity::class.java)
                            Log.d("New Message", userItem.user.name)
                            intent.putExtra("USER_KEY", userItem.user)
                            startActivity(intent)
                            finish()
                        }
                        recyclervew.adapter = adapter
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("Error: ", "${error.message}")
                    }
                })
        }
    }

    class UItem(val user: SignUpActivity.getUser) : Item<GroupieViewHolder>() {
        override fun getLayout(): Int {
            return R.layout.layout_thong_bao
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {

            if (user.Urlphoto.isEmpty()) {
                val ImgDefault = "https://th.bing.com/th/id/R.502a73beb3f9263ca076457d525087c6?" +
                        "rik=OP8RShVgw6uFhQ&riu=http%3a%2f%2fdvdn247.net%2fwp-content%2fuploads%2f2020%2f07%2" +
                        "favatar-mac-dinh-1.png&ehk=NSFqDdL3jl9cMF3B9A4%2bzgaZX3sddpix%2bp7R%2bmTZHsQ%3d&risl=" +
                        "&pid=ImgRaw&r=0"
                viewHolder.itemView.Txtusername_Thong_Bao.text = user.name
                Picasso.get().load(ImgDefault).into(viewHolder.itemView.IVUser_Thong_Bao)
                Log.d("New Message :", "User Name : ${user.name} \n PhotoUrl : $ImgDefault")
            } else {
                viewHolder.itemView.Txtusername_Thong_Bao.text = user.name
                Picasso.get().load(user.Urlphoto).into(viewHolder.itemView.IVUser_Thong_Bao)
                Log.d("New Message :", "User Name : ${user.name} \n PhotoUrl : ${user.Urlphoto}")
            }
        }
    }
}