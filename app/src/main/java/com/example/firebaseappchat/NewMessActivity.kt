package com.example.firebaseappchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.*
import com.example.firebaseappchat.databinding.ActivityNewMessBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.R.layout.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.*

class NewMessActivity : AppCompatActivity() {
    private lateinit var binding : ActivityNewMessBinding
    private lateinit var data : FirebaseAuth
    private lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewMessBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_new_mess)

        supportActionBar?.title = "Chọn Người Muốn Nhắn"

        binding.recyclerviewNewmess.layoutManager = LinearLayoutManager(this)
        verifyUserLoggedIn()
        //addUser()
        LayUser()
    }

    // Get user name and images then show in user_row
    private fun LayUser() {
        val ref = FirebaseDatabase.getInstance().getReference()
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                snapshot.children.forEach() {
                    val user = it.getValue(SignUpActivity.User::class.java)

                    if (user != null) {
                        adapter.add(UItem(user))
                    }
                }
                binding.recyclerviewNewmess.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }



    //Check User Login
    private fun verifyUserLoggedIn() {
        val uid = FirebaseAuth.getInstance()?.uid
        if (uid == null) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}

//Connect user_row to recycler view
class UItem(val user: SignUpActivity.User): Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.user_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        //viewHolder.itemView.txtUserName.text = user.name
    }
}
