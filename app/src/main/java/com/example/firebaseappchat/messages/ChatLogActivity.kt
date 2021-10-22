package com.example.firebaseappchat.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.firebaseappchat.NewMessActivity
import com.example.firebaseappchat.R
import com.example.firebaseappchat.model.ChatMessage
import com.example.firebaseappchat.registerlogin.SignUpActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*


class ChatLogActivity : AppCompatActivity() {

    companion object{
        val TAG = "chat log"
    }
    val adapter =GroupAdapter<GroupieViewHolder>()

    var toUser: SignUpActivity.getUser? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        //supportActionBar?. title="chat log"

        //val username = intent.getStringExtra(NewMessActivity.USER_KEY)
        //supportActionBar?.title = username

        recyclerview_chat_log.adapter =adapter

        toUser = intent.getParcelableExtra<SignUpActivity.getUser>(NewMessActivity.USER_KEY)

        supportActionBar?.title = toUser?.name





        nhantinnhan()

        gui_button_chat_log.setOnClickListener{
            Log.d(TAG,"attempt to send message...")
            performsendMessage()
        }
    }
    private fun nhantinnhan(){
        var ref = FirebaseDatabase.getInstance().getReference("/messages")
        ref.addChildEventListener(object :ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                chatMessage?.text?.let { Log.d(TAG, it) }
                if (chatMessage != null) {
                    if (chatMessage.formId== FirebaseAuth.getInstance().uid) {
                        val currentUser = MainActivity.currentUser ?:return
                        adapter.add(ChatFromItem(chatMessage.text,currentUser))
                    }
                    else{
                        adapter.add(ChatToItem(chatMessage.text, toUser!!))
                    }
                }


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }
        })
    }




    private fun performsendMessage(){
        // gui du lieu vao firebase

        editText_chat_log.text.toString()

        val text = editText_chat_log.text.toString()

        val fromId = FirebaseAuth.getInstance().uid


        val user = intent.getParcelableExtra<SignUpActivity.getUser>(NewMessActivity.USER_KEY)

        val toId = user?.uid

        if(fromId == null ) return

        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()

        val chatMessage = toId?.let {
            ChatMessage(reference.key!!,text,fromId!!,
                it,System.currentTimeMillis()/1000)
        }
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG,"luu thong tin: ${reference.key}")
            }
    }



}
class ChatFromItem (val text:String,val user: SignUpActivity.getUser):Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textviewfrom_chat_from_row.text =text

        val url =user.Urlphoto
        val targetImageView = viewHolder.itemView.imageView_chat_from_row
        Picasso.get().load(url).into(targetImageView)
    }
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}
class ChatToItem (val text:String,val user: SignUpActivity.getUser):Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textviewfrom_chat_to_row.text = text

        val url =user.Urlphoto
        val targetImageView = viewHolder.itemView.imageViewchat_to_row
        Picasso.get().load(url).into(targetImageView)

    }
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}