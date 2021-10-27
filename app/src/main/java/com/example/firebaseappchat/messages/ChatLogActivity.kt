package com.example.firebaseappchat.messages

import android.annotation.SuppressLint
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
import kotlinx.android.synthetic.main.fragment_home.*
import java.text.SimpleDateFormat


class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "chat log"
    }

    val adapter = GroupAdapter<GroupieViewHolder>()

    var toUser: SignUpActivity.getUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chat_log.adapter = adapter

        toUser = intent.getParcelableExtra<SignUpActivity.getUser>(NewMessActivity.USER_KEY)

        supportActionBar?.title = toUser?.name

        nhantinnhan()

        gui_button_chat_log.setOnClickListener {
            performsendMessage()
        }
        editText_chat_log.setOnClickListener {
            if (editText_chat_log.text.toString() != "") {
                performsendMessage()
            }
        }
    }

    private fun nhantinnhan() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
        ref.addChildEventListener(object : ChildEventListener {
            @SuppressLint("SimpleDateFormat")
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                chatMessage?.text?.let { Log.d(TAG, it) }
                val sdf = SimpleDateFormat("hh:mm")
                if (chatMessage != null) {
                    if (chatMessage.formId == FirebaseAuth.getInstance().uid) {
                        val currentUser = MainActivity.currentUser ?: return

                        adapter.add(
                            ChatFromItem(
                                chatMessage.text,
                                sdf.format(chatMessage.timestamp),
                                currentUser
                            )
                        )
                    } else {
                        adapter.add(
                            ChatToItem(
                                chatMessage.text,
                                sdf.format(chatMessage.timestamp),
                                toUser!!
                            )
                        )
                    }
                }
                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)

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


    private fun performsendMessage() {
        // gui du lieu vao firebase
        editText_chat_log.text.toString()
        val text = editText_chat_log.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<SignUpActivity.getUser>(NewMessActivity.USER_KEY)
        val toId = user?.uid

        if (fromId == null) return

        // tin nhan tu nguoi gui
        val reference =
            FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        //tin nhan tu nguoi nhan
        val toReference =
            FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()
        val chatMessage = toId?.let {
            ChatMessage(
                reference.key!!, text, fromId!!,
                it, System.currentTimeMillis()
            )
        }
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "luu thong tin: ${reference.key}")
                editText_chat_log.text.clear()
                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
            }
        toReference.setValue(chatMessage)

        val latestMessagesRef =
            FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessagesRef.setValue(chatMessage)
        val latestMessagesToRef =
            FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessagesToRef.setValue(chatMessage)
    }
}

class ChatFromItem(val text: String, val time: String, val user: SignUpActivity.getUser) :
    Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textviewfrom_chat_from_row.text = text
        viewHolder.itemView.Txt_time_From.text = time

        val url = user.Urlphoto
        val targetImageView = viewHolder.itemView.imageView_chat_from_row
        Picasso.get().load(url).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem(val text: String, val time: String, val user: SignUpActivity.getUser) :
    Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textviewfrom_chat_to_row.text = text
        viewHolder.itemView.Txt_time_To.text = time

        val url = user.Urlphoto
        val targetImageView = viewHolder.itemView.imageViewchat_to_row
        Picasso.get().load(url).into(targetImageView)

    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}